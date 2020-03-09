package com.example.app.bbs.unit.controller;


import com.example.app.bbs.app.controller.AdminController;
import com.example.app.bbs.domain.entity.Article;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AdminController target;

    @Test
    public void noAuthenticationTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void authenticationTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.get("/admin/index")
        )
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("page"))
        .andExpect(view().name("admin_index"))
        .andExpect(model().attributeExists("isAdmin"));
    }

    @Test
    @WithUserDetails(value = "admin")
    public void singleDeleteNotExistsArticleTest() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/0")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS)
             );
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');")
    @WithUserDetails(value = "admin")
    public void singleDeleteExistsArticleTest() throws Exception{
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/delete/" + latestArticle.id)
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute(
                                "message",
                        target.MESSAGE_DELETE_NORMAL)
     );
    }

    @Test
    @WithUserDetails(value = "admin")
    public void multiDeleteNotSelectedArticleTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_ARTICLE_NOT_SELECTED));
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');" +
            "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');" +
            "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');")
    @WithUserDetails(value = "admin")
    public void multiDeleteSelectedArticleTest() throws Exception{
        List<Article> latestArticles = target.articleRepository.findAll();
        StringBuilder bf = new StringBuilder();
        for (Article article : latestArticles) {
            if (bf.length() > 1) bf.append(",");
            bf.append(article.id);
        }

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/article/deletes")
                        .with(csrf())
                        .param("article_checks", bf.toString())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/index"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message",
                        target.MESSAGE_DELETE_NORMAL));
    }

    @Test
    public void  getAdminLoginTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/admin/login")
        )
        .andExpect(status().isOk());
    }

    @Test
    @Sql(statements = "INSERT INTO users (name, email, password, role) VALUES ('admin1', 'admin1@example.com', '$2a$10$CPNJ.PlWH8k1aMhC6ytjIuwxYuLWKMXTP3H6h.LRnpumtccpvXEGy', 'USER');")
    public void adminLoginAuthTest() throws Exception{

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/admin/login/auth")
                        .with(csrf())
                        .param("username","admin1")
                        .param("password","root")
        )
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl("/"));
    }
}