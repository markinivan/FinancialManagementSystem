package com.example.finance.repository;
import com.example.finance.model.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;

public class FileWalletRepository implements WalletRepository {
    private final ObjectMapper mapper;
    private final String dataDir = "data/wallets/";

    public FileWalletRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        new File(dataDir).mkdirs();
    }

    @Override
    public void save(Wallet wallet) {
        try {
            mapper.writeValue(new File(dataDir + wallet.getOwnerLogin() + ".json"), wallet);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    @Override
    public Wallet load(String login) {
        File file = new File(dataDir + login + ".json");
        if (file.exists()) {
            try {
                return mapper.readValue(file, Wallet.class);
            } catch (IOException e) {
                System.err.println("Ошибка загрузки: " + e.getMessage());
            }
        }
        return new Wallet(login);
    }
}
