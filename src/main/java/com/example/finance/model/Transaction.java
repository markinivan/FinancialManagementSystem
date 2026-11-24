package com.example.finance.model;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id;
    private TransactionType type;
    private String category;
    private BigDecimal amount;
    private LocalDateTime timestamp;

    public Transaction() {}
    
    public Transaction(TransactionType type, String category, BigDecimal amount) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
