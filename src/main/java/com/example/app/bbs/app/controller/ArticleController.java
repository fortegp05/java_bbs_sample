package com.example.app.bbs.app.controller;

import com.example.app.bbs.app.request.ArticleRequest;
import com.example.app.bbs.domain.entity.Article;
import com.example.app.bbs.domain.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
public class ArticleController {
    public final String MESSAGE_REGISTER_NORMAL = "正常に投稿できました。";
    public final String MESSAGE_ARTICLE_DOES_NOT_EXISTS = "対象の記事が見つかりませんでした。";
    public final String MESSAGE_UPDATE_NORMAL = "正常に更新しました。";
    public final String MESSAGE_ARTICLE_KEY_UNMATCH = "投稿KEYが一致しません。";
    public final String MESSAGE_DELETE_NORMAL = "正常に削除しました。";

    public String ALERT_CLASS_ERROR = "alert-error";

    public int PAGE_SIZE = 10;

    @Autowired
    public ArticleRepository articleRepository = null;

    @GetMapping("/seed")
    @ResponseBody
    public String seed()  {
        for (int i=0; i < 50; i++) {
            Article article = new Article();
            article.setName("name_" + i);
            article.setName("title_" + i);
            article.setContents("contents_" + i);
            article.setArticleKey("1234");
            articleRepository.save(article);
        }

        return "Finish";
    }

    @PostMapping("/")
    public String registerArticle(@Validated @ModelAttribute ArticleRequest articleRequest,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes
    ) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result);
            redirectAttributes.addFlashAttribute("request", articleRequest);

            return "redirect:/";
        }

        articleRepository.save(
            new Article(
                articleRequest.getId(),
                articleRequest.getName(),
                articleRequest.getTitle(),
                articleRequest.getContents(),
                articleRequest.getArticleKey(),
                new Date(),
                new Date()
            )
        );

        redirectAttributes.addFlashAttribute(
                "message", MESSAGE_REGISTER_NORMAL
        );

        return "redirect:/";
    }

    @GetMapping("/")
    public String getArticleList(@ModelAttribute ArticleRequest articleRequest,
                                @RequestParam(
                                    value = "page",
                                    defaultValue = "0",
                                    required = false
                                ) int page,
                                Model model
    ) {

        Pageable pageable = PageRequest.of(
                page,
                this.PAGE_SIZE,
                new Sort(Sort.Direction.DESC, "updateAt")
                        .and(new Sort(Sort.Direction.ASC, "id"))
        );

        if (model.containsAttribute("errors")) {
            String key = BindingResult.MODEL_KEY_PREFIX + "articleRequest";
            model.addAttribute(key, model.asMap().get("errors"));
        }

        if (model.containsAttribute("request")) {
            model.addAttribute("articleRequest", model.asMap().get("request"));
        }

        Page<Article> articles = articleRepository.findAll(pageable);
        model.addAttribute("page", articles);

        return "index";
    }

    @GetMapping("/edit/{id}")
    public String getArticleEdit(
            @PathVariable int id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        if (articleRepository.existsById(id)) {
            if (model.containsAttribute("request")) {
                model.addAttribute("article", model.asMap().get("request"));
            } else {
                model.addAttribute("article", articleRepository.findById(id).get());
            }

            if (model.containsAttribute("errors")) {
                String key = BindingResult.MODEL_KEY_PREFIX + "article";
                model.addAttribute(key, model.asMap().get("errors"));
            }

            return "edit";
        } else {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS
            );
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR
            );

            return "redirect:/";
        }
    }

    @PostMapping("/update")
    public String updateArticle(@Validated ArticleRequest articleRequest,
                         BindingResult result,
                         RedirectAttributes redirectAttributes
    ) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result);
            redirectAttributes.addFlashAttribute("request", articleRequest);

            return "redirect:/edit/" + articleRequest.id;
        }

        if (!articleRepository.existsById(articleRequest.getId())) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS
            );
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR
            );

            return "redirect:/";
        }

        Article article = articleRepository.findById(articleRequest.getId()).get();

        if (!articleRequest.getArticleKey().equals(article.getArticleKey())) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_KEY_UNMATCH
            );
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR
            );

            return "redirect:/edit/" + articleRequest.id;
        }

        article.setName(articleRequest.getName());
        article.setTitle(articleRequest.getTitle());
        article.setContents(articleRequest.getContents());
        article.setUpdateAt(new Date());

        articleRepository.save(article);

        redirectAttributes.addFlashAttribute("message", MESSAGE_UPDATE_NORMAL);

        return "redirect:/";
    }

    @GetMapping("/delete/confirm/{id}")
    public String getDeleteConfirm(@PathVariable int id, Model model,
                            RedirectAttributes redirectAttributes) {

        if (!articleRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS);
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR);

            return "redirect:/";
        }

        if (!articleRepository.existsById(id)) {

            return "redirect:/";
        }

        model.addAttribute("article", articleRepository.findById(id).get());

        String key = BindingResult.MODEL_KEY_PREFIX + "article";
        if (model.containsAttribute("errors")) {
            model.addAttribute(key, model.asMap().get("errors"));
        }

        return "delete_confirm";
    }

    @PostMapping("/delete")
    public String deleteArticle(@Validated @ModelAttribute ArticleRequest articleRequest,
                         BindingResult result,
                         RedirectAttributes redirectAttributes

    ) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result);
            redirectAttributes.addFlashAttribute("request", articleRequest);

            return "redirect:/delete/confirm/" + articleRequest.id;
        }

        if (!articleRepository.existsById(articleRequest.getId())) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS);
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR);

            return "redirect:/";
        }

        Article article = articleRepository.findById(articleRequest.getId()).get();

        if (!articleRequest.getArticleKey().equals(article.getArticleKey())) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_KEY_UNMATCH);
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR);

            return "redirect:/delete/confirm/" + article.id;
        }

        articleRepository.deleteById(articleRequest.getId());

        redirectAttributes.addFlashAttribute("message",
                MESSAGE_DELETE_NORMAL);

        return "redirect:/";
    }
}
