package com.example.finance.repository;
import com.example.finance.model.User;
public interface UserRepository {
    User findByLogin(String login);
    void save(User user);
}
