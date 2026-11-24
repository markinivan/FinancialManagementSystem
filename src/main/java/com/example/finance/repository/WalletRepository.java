package com.example.finance.repository;
import com.example.finance.model.Wallet;
public interface WalletRepository {
    void save(Wallet wallet);
    Wallet load(String login);
}
