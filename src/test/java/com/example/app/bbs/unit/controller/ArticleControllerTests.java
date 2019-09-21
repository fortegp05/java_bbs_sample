package com.example.app.bbs.unit.controller;

import com.example.app.bbs.app.controller.ArticleController;
import com.example.app.bbs.domain.entity.Article;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ArticleControllerTests {
    MockMvc mockMvc;

    @Autowired
    ArticleController target;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(target).build();
    }

    @Test
    public void registerArticleTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                        .param("id", "0")
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "test")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", target.MESSAGE_REGISTER_NORMAL));
    }

    @Test
    public void registerArticleRequestErrorTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/")
                        .param("name", "")
                        .param("title", "")
                        .param("contents", "")
                        .param("articleKey", "")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errors"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("request"));
    }


    @Test
    public void getArticleListTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/")
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("page"))
                .andExpect(MockMvcResultMatchers.view().name("index"));
    }


    @Test
    public void getArticleEditNotExistsIdTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/edit/" + 0)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"));
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');")
    public void  getArticleEditExistsIdTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/edit/" + latestArticle.getId())
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("edit"));
    }


    @Test
    public void updateArticleNotExistsArticleTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .param("id", "0")
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS))
                .andExpect(MockMvcResultMatchers.flash().attribute("alert_class",
                        target.ALERT_CLASS_ERROR));
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key,register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());")
    public void updateArticleNotMatchArticleKeyTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .param("id", String.valueOf(latestArticle.getId()))
                        .param("name", latestArticle.getName())
                        .param("title", latestArticle.getTitle())
                        .param("contents", latestArticle.getContents())
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(
                        MockMvcResultMatchers.view().name("redirect:/edit/" + latestArticle.id)
                )
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_ARTICLE_KEY_UNMATCH))
                .andExpect(MockMvcResultMatchers.flash().attribute("alert_class", target.ALERT_CLASS_ERROR));
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());")
    public void updateArticleExistsArticleTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
                        .param("id", String.valueOf(latestArticle.getId()))
                        .param("name", latestArticle.getName())
                        .param("title", latestArticle.getTitle())
                        .param("contents", latestArticle.getContents())
                        .param("articleKey", latestArticle.getArticleKey())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_UPDATE_NORMAL));
    }

    @Test
    public void updateArticleRequestErrorTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/update")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/edit/0"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errors"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("request"));
    }


    @Test
    public void getDeleteConfirmNotExistsIdTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/delete/confirm/0")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message", target.MESSAGE_ARTICLE_DOES_NOT_EXISTS));
    }


    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key) VALUES ('test', 'test', 'test', 'test');")
    public void  getDeleteConfirmExistsIdTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.get(
                        "/delete/confirm/" + latestArticle.id
                )
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("delete_confirm"));
    }

    @Test
    public void deleteArticleNotExistsArticleTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .param("id", "0")
                        .param("name", "test")
                        .param("title", "test")
                        .param("contents", "test")
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_ARTICLE_DOES_NOT_EXISTS));
    }

    @Test
    @Sql(statements = "INSERT INTO article (name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());")
    public void  deleteArticleNotMatchArticleKeyTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .param("id", String.valueOf(latestArticle.getId()))
                        .param("name", latestArticle.getName())
                        .param("title", latestArticle.getTitle())
                        .param("contents", latestArticle.getContents())
                        .param("articleKey", "err.")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name(
                        "redirect:/delete/confirm/" + latestArticle.id)
                )
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_ARTICLE_KEY_UNMATCH));
    }

    @Test
    @Sql(statements = "INSERT INTO article(name, title, contents, article_key, register_at, update_at) VALUES ('test', 'test', 'test', 'test', now(), now());")
    public void deleteArticleExistsArticleTest() throws Exception {
        List<Article> articleList = target.articleRepository.findAll();
        Article latestArticle = articleList.get(articleList.size() - 1);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
                        .param("id", String.valueOf(latestArticle.getId()))
                        .param("name", latestArticle.getName())
                        .param("title", latestArticle.getTitle())
                        .param("contents", latestArticle.getContents())
                        .param("articleKey", latestArticle.getArticleKey())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("message"))
                .andExpect(MockMvcResultMatchers.flash().attribute("message",
                        target.MESSAGE_DELETE_NORMAL));
    }

    @Test
    public void deleteArticleRequestErrorTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/delete")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/delete/confirm/0"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("errors"))
                .andExpect(MockMvcResultMatchers.flash().attributeExists("request"));
    }

}
