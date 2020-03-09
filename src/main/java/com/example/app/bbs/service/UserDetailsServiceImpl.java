package com.example.app.bbs.service;

import com.example.app.bbs.domain.entity.User;
import com.example.app.bbs.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository = null;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = null;
        if (username != null) {
            user = userRepository.findByName(username);

            if (user == null) {
                user = userRepository.findByEmail(username);
            }
        }

        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetailsImpl(user);
    }
}