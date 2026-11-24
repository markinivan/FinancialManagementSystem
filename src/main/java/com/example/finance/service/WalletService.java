package com.example.finance.service;
import com.example.finance.model.*;
import com.example.finance.repository.WalletRepository;
import java.math.BigDecimal;

public class WalletService {
    private final WalletRepository repo;
    private Wallet currentWallet;

    public WalletService(WalletRepository repo) {
        this.repo = repo;
    }

    public void loadWallet(String login) { this.currentWallet = repo.load(login); }
    public void saveWallet() { if (currentWallet != null) repo.save(currentWallet); }
    public Wallet getWallet() { return currentWallet; }

    public void editBudget(String category, BigDecimal limit) {
        if (!currentWallet.getBudgets().containsKey(category)) {
            throw new com.example.finance.exception.AppException("Категория не найдена.");
        }
        currentWallet.getBudgets().get(category).setLimit(limit);
    }

    public void addTransaction(TransactionType type, String category, String amountStr) {
        BigDecimal amount = new BigDecimal(amountStr);
        Transaction t = new Transaction(type, category, amount);
        currentWallet.getTransactions().add(t);
        
        if (type == TransactionType.EXPENSE) {
            checkNotifications(category);
        }
    }

    private void checkNotifications(String category) {
        Budget b = currentWallet.getBudgets().get(category);
        if (b != null) {
            BigDecimal spent = getSpentByCategory(category);
            if (spent.compareTo(b.getLimit()) > 0) {
                System.out.println("[ВНИМАНИЕ] Превышен лимит по категории '" + category + "'!");
            } else if (spent.compareTo(b.getLimit().multiply(new BigDecimal("0.8"))) >= 0) {
                System.out.println("[ВНИМАНИЕ] Израсходовано более 80% бюджета '" + category + "'!");
            }
        }
    }

    public BigDecimal getSpentByCategory(String category) {
        return currentWallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCategory().equals(category))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
