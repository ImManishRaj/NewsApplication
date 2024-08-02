package com.manish.NewsApplication.news;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Article {
    private String title;
    private String publishedAt;
    private String content;
    private String newsUrl;

    public Article(String title, String content, String publishedAt,String newsUrl) {
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.newsUrl=newsUrl;
    }

}
