package com.manish.NewsApplication.news;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ArticleConfig {



    @Value("${X-Api-Key}")
    public String apiKey;


    @Value("${x-rapidapi-host}")
    public String host;
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
    {

        return restTemplateBuilder.build();
    }
}
