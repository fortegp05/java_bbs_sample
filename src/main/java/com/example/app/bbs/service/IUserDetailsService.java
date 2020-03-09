package com.example.app.bbs.service;

import com.example.app.bbs.domain.entity.User;

interface IUserDetailsService {
    void registerUser(User user, String rawPassword);
}