package com.example.app.bbs.validator;

import com.example.app.bbs.domain.entity.User;
import com.example.app.bbs.domain.entity.UserRole;
import com.example.app.bbs.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

    public final int PASSWORD_LENGTH_MIN = 4;
    public final int  PASSWORD_LENGTH_MAX = 8;

    @Autowired
    UserRepository userRepository = null;

    @Autowired
    MessageSource messageSource = null;

    @Override
    public void validate(Object target, Errors errors) {
        if (!(target instanceof User)) {
            errors.reject("TAEGET_IS_NOT_USER_OBJECT");
            return;
        }

        // Emailアドレスの重複チェック
        if (userRepository.findByEmail(((User)target).getEmail()) != null) {
            errors.rejectValue("email", "REGISTERED_EMAIL");
        }

        // パスワードの長さチェック
        if ( !(PASSWORD_LENGTH_MIN <= ((User)target).getPassword().length()
            && ((User)target).getPassword().length() <= PASSWORD_LENGTH_MAX)
        ) {
            Integer array[] = {PASSWORD_LENGTH_MIN, PASSWORD_LENGTH_MAX};
            errors.rejectValue(
            "password", "PASSWORD_LENGTH_ERROR",
                array,null
            );
        }

        // パスワード使用文字のチェック
        if (!((User) target).getPassword().matches("[a-zA-Z0-9\\-!_]*")) {
            errors.rejectValue("password", "PASSWORD_ILLEGAL_CHAR");
        }

        // ロールのチェック(画面からはエラーにはならない)
        if (((User) target).getRole() != UserRole.USER) {
            errors.rejectValue("role", "NOT_USER_ROLE");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }
}