package com.example.finance.repository;
import com.example.finance.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileUserRepository implements UserRepository {
    private final ObjectMapper mapper = new ObjectMapper();
    private final String dataFile = "data/users.json";
    private final Map<String, User> users = new HashMap<>();

    public FileUserRepository() {
        new File("data").mkdirs();
        load();
    }

    private void load() {
        File file = new File(dataFile);
        if (file.exists()) {
            try {
                Map<String, User> loaded = mapper.readValue(file, new TypeReference<Map<String, User>>() {});
                users.putAll(loaded);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки пользователей: " + e.getMessage());
            }
        }
    }

    private void saveToFile() {
        try {
            mapper.writeValue(new File(dataFile), users);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользователей: " + e.getMessage());
        }
    }

    @Override
    public User findByLogin(String login) {
        return users.get(login);
    }

    @Override
    public void save(User user) {
        users.put(user.getLogin(), user);
        saveToFile();
    }
}
