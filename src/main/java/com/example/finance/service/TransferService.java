package com.example.finance.service;
import com.example.finance.model.*;
import com.example.finance.repository.WalletRepository;
import java.math.BigDecimal;

public class TransferService {
    private final WalletRepository repo;
    
    public TransferService(WalletRepository repo) {
        this.repo = repo;
    }

    public void transfer(Wallet sender, String recipientLogin, BigDecimal amount) {
        Wallet recipient = repo.load(recipientLogin);
        
        sender.getTransactions().add(new Transaction(TransactionType.EXPENSE, "Перевод", amount));
        recipient.getTransactions().add(new Transaction(TransactionType.INCOME, "Перевод", amount));
        
        repo.save(recipient);
        System.out.println("Перевод на сумму " + amount + " выполнен пользователю " + recipientLogin);
    }
}
