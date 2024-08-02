package com.manish.NewsApplication.news;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class NewsController {



   private final NewsService newsService;
    @GetMapping("/latest-news")
    public ResponseEntity<List<Article>> getLatest(@RequestParam String email) {
        List<Article> latestNews = (List<Article>) newsService.getLatest(email);
        return ResponseEntity.ok(latestNews);
    }

}
