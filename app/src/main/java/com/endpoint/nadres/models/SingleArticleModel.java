package com.endpoint.nadres.models;

import java.io.Serializable;
import java.util.List;

public class SingleArticleModel implements Serializable {
    private int id;
    private String title;
    private String details;
    private String photo;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDetails() {
        return details;
    }

    public String getPhoto() {
        return photo;
    }
}
