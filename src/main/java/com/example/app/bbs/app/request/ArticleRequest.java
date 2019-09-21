package com.example.app.bbs.app.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ArticleRequest {
    public int id = 0;
    @NotBlank
    @Size(min = 1, max = 50)
    public String name = "";
    @NotBlank
    @Size(min = 1, max = 50)
    public String title = "";
    @NotBlank
    @Size(min = 1, max = 500)
    public String contents = "";
    @NotBlank
    @Size(min = 1, max = 4)
    public String articleKey = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getArticleKey() {
        return articleKey;
    }

    public void setArticleKey(String articleKey) {
        this.articleKey = articleKey;
    }
}
