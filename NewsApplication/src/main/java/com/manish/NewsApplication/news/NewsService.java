package com.manish.NewsApplication.news;


import com.manish.NewsApplication.OpenAI.MistralService;
import com.manish.NewsApplication.preference.UserPreferences;
import com.manish.NewsApplication.user.User;
import com.manish.NewsApplication.user.UserService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NewsService {

    @Autowired
    private RestTemplate restTemplate;


    @Value("${X-Api-Key}")
    private String apiKey;


    @Value("${x-rapidapi-host}")
    public String host;


    /* private static final String BASE_URL = "https://google-news13.p.rapidapi.com/%s?lr=en-US";*/
    private static final String BASE_URL = "https://newsapi.org/v2/everything?q=%s&apiKey=%s&pageSize=5";

    private final UserService userService;

    @Autowired
    @Lazy
    private MistralService mistralService;

    public NewsService(UserService userService) {
        this.userService = userService;

    }

    private final ExecutorService executorService= Executors.newFixedThreadPool(10);

    public Object getLatest(String email) {

        User findUser = userService.findByEmail(email);
        UserPreferences userPreferences = findUser.getUserPreference();

        if (userPreferences == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User preferences not found");
        }

        List<String> interest = new ArrayList<>(userPreferences.getCategoriesOfInterest());
        if (interest.isEmpty()) {
            interest.add("latest");
        }


        Set<Article> articles = ConcurrentHashMap.newKeySet(); //Thread safety

        List<Future<Void>> futures = new ArrayList<>();
        for (String category : interest) {
            String url = String.format(BASE_URL, category, apiKey);
            log.info("Fetching article from URL :{}", url);

            Future<Void> future = executorService.submit(() -> {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("x-api-key", apiKey);


                    ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

                    log.info("Response Status Code: {}", response.getStatusCode());
                    /*  log.info("Response Body: {}", response.getBody());*/

                    if (response.getStatusCode() == HttpStatus.OK) {
                        Map<String, Object> body = response.getBody();
                        if (body != null) {
                            List<Map<String, Object>> articlesFromAPI = (List<Map<String, Object>>) body.get("articles");

                            if (articlesFromAPI != null) {
                                for (Map<String, Object> item : articlesFromAPI) {

                                    String title = (String) item.get("title");
                                    String content = (String) item.get("content");
                                    String publishedAt = (String) item.get("publishedAt");
                                    String newsUrl = (String) item.get("url");

                                    if (title != null && content != null) {

                                        Article article = new Article(title, content, publishedAt, newsUrl);
                                        articles.add(article);
                                    } else {
                                        log.warn("Article title or URL is null in the response for category: {}", category);
                                    }
                                }
                            } else {
                                log.warn("No articles found in the response for category: {}", category);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error calling NewsAPI endpoint for category '{}'", category, e);
                }
                return null;
            });
            futures.add(future);
        }


        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error waiting for API calls to complete", e);
            }
        }


        int n = articles.size();
        List<Article> topArticles = articles.stream()
                .sorted(Comparator.comparing(Article::getPublishedAt).reversed()) // Sort by date, most recent first
                .limit(n)
                .toList();

        List<Future<Article>> summarizationFutures = topArticles.stream()
                .map(article -> executorService.submit(() -> {
                    String content = mistralService.summarizeContent(article.getContent());
                    return new Article(article.getTitle(), content, article.getPublishedAt(), article.getNewsUrl());
                }))
                .toList();

        List<Article> summarizedArticles = new ArrayList<>();
        for (Future<Article> future : summarizationFutures) {
            try {
                summarizedArticles.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
              if (e.getCause() instanceof HttpStatusCodeException httpStatusCodeException)
              {
                  if (httpStatusCodeException.getStatusCode()==HttpStatus.TOO_MANY_REQUESTS)
                  {
                      System.out.println(e.getMessage());
                      log.info("Request limit exceeded for Mistral API. Retry after sometime");
                      break;
                  }
              }
                log.error("Error summarizing article", e);
            }
        }
        return summarizedArticles;
    }

        // Ensure to shut down the executor service gracefully when the application shuts down
    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
        }



