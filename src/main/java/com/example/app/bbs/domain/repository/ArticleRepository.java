package com.example.app.bbs.domain.repository;

import com.example.app.bbs.domain.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    @Transactional
    public Integer deleteByIdIn(List<Integer> ids);
    public Page<Article> findAllByUserId(Integer userId, Pageable pageable);
}
