package com.example.app.bbs.app.controller;

import com.example.app.bbs.app.request.ArticleRequest;
import com.example.app.bbs.domain.entity.Article;
import com.example.app.bbs.domain.entity.User;
import com.example.app.bbs.domain.entity.UserRole;
import com.example.app.bbs.domain.repository.ArticleRepository;
import com.example.app.bbs.service.UserDetailsImpl;
import com.example.app.bbs.service.UserManagerServiceImpl;
import com.example.app.bbs.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Date;

@Controller
public class UserController {

    public final int PAGE_SIZE = 10;
    public final String MESSAGE_REGISTER_NORMAL = "正常に投稿できました。";

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserManagerServiceImpl userManagerServiceImpl = null;

    @Autowired
    UserValidator userValidator = null;

    @GetMapping("/user/login")
    String getUserLogin()  {
        return "user_login";
    }

    @GetMapping("/user/signup")
    String getUserSignup(
            @ModelAttribute User user,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        model.addAttribute("user_role", UserRole.USER.name());

        if (model.containsAttribute("errors")) {
            String key =
                    BindingResult.MODEL_KEY_PREFIX + "user";
            model.addAttribute(key, model.asMap().get("errors"));
        }

        if (model.containsAttribute("request")) {
            model.addAttribute("user", model.asMap().get("request"));
        }

        return "user_signup";
    }

    @PostMapping("/user/signup")
    String userSignup(
            @Validated @ModelAttribute User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes
    ) {

        userValidator.validate(user, result);

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result);
            redirectAttributes.addFlashAttribute("request", user);

            return "redirect:/user/signup";
        }

        userManagerServiceImpl.registerUser(user, user.getPassword());

        return "redirect:/user/login";
    }

    @PostMapping("/user/login/auth")
    String userLogin() {

        return "redirect:/user/index";
    }

    @GetMapping("/user/logout")
    String getUserlogout() {
        return "redirect:/";
    }

    @GetMapping("/user/index")
    String getUserIndex(
            @ModelAttribute ArticleRequest articleRequest,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            @RequestParam(value = "page",
                    defaultValue = "0",
                    required = false) int page,
            Model model
    ) {
        model.addAttribute("user", userDetailsImpl.getUser());

        Pageable pageable  = PageRequest.of(
                page,
                this.PAGE_SIZE,
                new Sort(Sort.Direction.DESC, "updateAt")
                        .and(new Sort(Sort.Direction.ASC, "id"))
        );

        Page<Article> articles = articleRepository.findAllByUserId(userDetailsImpl.getUser().getId(), pageable);
        model.addAttribute("page", articles);

        return "user_index";
    }

    @PostMapping("/user/article/register")
    String userArticleRegister(
            @Validated @ModelAttribute ArticleRequest articleRequest,
            BindingResult result,
            @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", result);
            redirectAttributes.addFlashAttribute("request", articleRequest);

            return "redirect:/user/index";
        }

        articleRepository.save(
                new Article(
                        articleRequest.id,
                        articleRequest.name,
                        articleRequest.title,
                        articleRequest.contents,
                        articleRequest.articleKey,
                        new Date(),
                        new Date(),
                        userDetailsImpl.getUser().getId()
                )
        );

        redirectAttributes.addFlashAttribute(
                "message", MESSAGE_REGISTER_NORMAL
        );

        return "redirect:/user/index";
    }
}
