package com.example.finance.repository;
import com.example.finance.model.User;
import java.util.HashMap;
import java.util.Map;

public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    @Override public User findByLogin(String login) { return users.get(login); }
    @Override public void save(User user) { users.put(user.getLogin(), user); }
}
