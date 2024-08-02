package com.manish.NewsApplication.OpenAI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MistralService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mistral.api.key}")
    private String apiKey;

    @Value("${mistral.api.url}")
    private String url;

    public String summarizeContent(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        // Prepare the payload for Mistral API
        Map<String, Object> requestPayload = new HashMap<>();
        requestPayload.put("model", "mistral-large-latest");
        requestPayload.put("messages", List.of(
                Map.of("role", "user", "content", "Summarize the following content in 100 words:\n\n" + content)
        ));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestPayload, headers);

        System.out.println("Calling URL: " + url);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");

                    return (String) message.get("content");
                }
            }
            System.out.println("Unexpected response structure: " + responseBody);
            throw new RuntimeException("Failed to get a summary from Mistral API");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            if (e instanceof HttpClientErrorException httpError) {
                System.out.println("HTTP Error: " + httpError.getStatusCode() + " " + httpError.getStatusText());
                System.out.println("Response body: " + httpError.getResponseBodyAsString());
            }
            throw new RuntimeException("Error calling Mistral API", e);
        }
    }
}
