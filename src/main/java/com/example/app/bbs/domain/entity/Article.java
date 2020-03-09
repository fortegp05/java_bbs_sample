package com.example.app.bbs.domain.entity;

import java.util.*;
import javax.persistence.*;

@Entity
public class Article {

    public Article() {

    };

    public Article(int id, String name, String title,
                   String contents, String articleKey,
                   Date registerAt, Date updateAt,
                   Integer userId
    ) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.contents = contents;
        this.articleKey = articleKey;
        this.registerAt = registerAt;
        this.updateAt = updateAt;
        this.userId = userId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id = 0;
    public String name = "";
    public String title = "";
    public String contents = "";
    @Column(name = "article_key")
    public String articleKey = "";
    @Column(name = "register_at")
    public Date registerAt = new Date();
    @Column(name = "update_at")
    public Date updateAt = new Date();
    @Column(name = "user_id")
    Integer userId = null;

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

    public Date getRegisterAt() {
        return registerAt;
    }

    public void setRegisterAt(Date registerAt) {
        this.registerAt = registerAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
