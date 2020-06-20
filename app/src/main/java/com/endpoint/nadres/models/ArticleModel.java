package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class ArticleModel implements Serializable {
    private List<SingleArticleModel> data;
    private int current_page;

    public List<SingleArticleModel> getData() {
        return data;
    }

    public int getCurrent_page() {
        return current_page;
    }
}
