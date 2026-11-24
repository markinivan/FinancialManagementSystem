package com.example.finance.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private String ownerLogin;
    private List<Transaction> transactions = new ArrayList<>();
    private Map<String, Budget> budgets = new HashMap<>();

    public Wallet() {}
    
    public Wallet(String ownerLogin) { this.ownerLogin = ownerLogin; }

    public String getOwnerLogin() { return ownerLogin; }
    public void setOwnerLogin(String ownerLogin) { this.ownerLogin = ownerLogin; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setTransactions(List<Transaction> transactions) { this.transactions = transactions; }
    public Map<String, Budget> getBudgets() { return budgets; }
    public void setBudgets(Map<String, Budget> budgets) { this.budgets = budgets; }
}
