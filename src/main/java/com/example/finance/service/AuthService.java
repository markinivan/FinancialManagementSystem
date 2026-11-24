package com.example.finance.service;
import com.example.finance.exception.AppException;
import com.example.finance.model.User;
import com.example.finance.repository.FileUserRepository;
import com.example.finance.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepo;
    private User currentUser = null;

    public AuthService() {
        this(new FileUserRepository());
    }

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public void register(String login, String password) {
        if (userRepo.findByLogin(login) != null) throw new AppException("Пользователь уже существует.");
        userRepo.save(new User(login, password));
    }

    public void login(String login, String password) {
        User user = userRepo.findByLogin(login);
        if (user == null || !user.getPassword().equals(password)) {
            throw new AppException("Неверный логин или пароль.");
        }
        this.currentUser = user;
    }

    public void logout() { this.currentUser = null; }
    
    public User getCurrentUser() { 
        if (currentUser == null) throw new AppException("Вы не авторизованы.");
        return currentUser; 
    }
    public boolean isLoggedIn() { return currentUser != null; }
}
