package com.example.app.bbs.service;

import com.example.app.bbs.domain.entity.User;
import com.example.app.bbs.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserManagerServiceImpl implements IUserDetailsService{
    @Autowired
    UserRepository userRepository = null;

    @Autowired
    PasswordEncoder passwordEncoder = null;

    //一般ユーザを登録するメソッド
    public void registerUser(User user, String rawPassword) {

        // エンコードしたパスワードでユーザーを作成する
        user.setPassword(passwordEncoder.encode(rawPassword));

        userRepository.save(user);
    }
}