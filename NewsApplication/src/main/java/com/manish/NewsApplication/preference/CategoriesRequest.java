package com.manish.NewsApplication.preference;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CategoriesRequest {
    private Set<String> categories;
}
