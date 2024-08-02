package com.manish.NewsApplication.OpenAI;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OpenAIConfig {


    @Value("${mistral.api.key}")
    private String apikey;


    @Value("${mistral.api.url}")
    private String url;

    @Bean
    public  String openAIApiKey()
    {
        return apikey;
    }

    @Bean
    public String openAIApiUrl() {
        return url;
    }


}
