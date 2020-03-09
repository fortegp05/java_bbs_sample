package com.example.app.bbs.app.controller;

import com.example.app.bbs.domain.entity.Article;
import com.example.app.bbs.domain.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@Controller
public class AdminController {
    public final int PAGE_SIZE = 10;
    public final String MESSAGE_ARTICLE_DOES_NOT_EXISTS = "対象の記事が見つかりませんでした。";
    public final String MESSAGE_DELETE_NORMAL = "正常に削除しました。";
    public final String MESSAGE_ARTICLE_NOT_SELECTED = "削除する記事を選択してください。";

    public final String ALERT_CLASS_ERROR = "alert-error";

    @Autowired
    public ArticleRepository articleRepository = null;

    @GetMapping("/admin/index")
    public String getAdminIndex(@RequestParam(value = "page",
            defaultValue = "0",
            required = false) int page,
            Model model) {

        Pageable pageable = PageRequest.of(
                page,
                this.PAGE_SIZE,
                new Sort(Sort.Direction.DESC, "updateAt")
                        .and(new Sort(Sort.Direction.ASC, "id"))
        );

        Page<Article> articles = articleRepository.findAll(pageable);
        model.addAttribute("page", articles);
        model.addAttribute("isAdmin", true);

        return "admin_index";
    }

    @PostMapping("/admin/article/delete/{id}")
    String deleteArticle(@PathVariable int id,
                         RedirectAttributes redirectAttributes
    ) {

        if (!articleRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_DOES_NOT_EXISTS);
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR);

            return "redirect:/admin/index";
        }

        articleRepository.deleteById(id);

        redirectAttributes.addFlashAttribute("message",
                MESSAGE_DELETE_NORMAL);

        return "redirect:/admin/index";
    }

    @PostMapping("/admin/article/deletes")
    String deleteArticles(
            @RequestParam(value = "article_checks", required = false)
            String[] checkboxValues,
            RedirectAttributes redirectAttributes
    ) {


        if (checkboxValues == null || checkboxValues.length == 0) {
            redirectAttributes.addFlashAttribute("message",
                    MESSAGE_ARTICLE_NOT_SELECTED);
            redirectAttributes.addFlashAttribute("alert_class",
                    ALERT_CLASS_ERROR);

            return "redirect:/admin/index";
        }
        ArrayList<Integer> checkboxValueList = new ArrayList<>();
        for (String id : checkboxValues) {
            checkboxValueList.add(new Integer(id));
        }

        articleRepository.deleteByIdIn(checkboxValueList);

        redirectAttributes.addFlashAttribute("message",
                MESSAGE_DELETE_NORMAL);

        return "redirect:/admin/index";
    }

    @GetMapping("/admin/login")
    String getAdminLoginTest() {
        return "admin_login";
    }

    @PostMapping("/admin/login/auth")
    String adminLogin() {

        return "redirect:/admin/index";
    }

    @PostMapping("/admin/logout")
    String adminLogout() {

        return "redirect:/";
    }
}
