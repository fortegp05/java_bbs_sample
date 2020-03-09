package com.example.app.bbs.domain.repository;

import com.example.app.bbs.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    public User findByName(String name);
    public User findByEmail(String email);
}
