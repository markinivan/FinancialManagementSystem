package com.example.finance.model;
import java.math.BigDecimal;

public class Budget {
    private String category;
    private BigDecimal limit;

    public Budget() {}
    
    public Budget(String category, BigDecimal limit) {
        this.category = category;
        this.limit = limit;
    }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getLimit() { return limit; }
    public void setLimit(BigDecimal limit) { this.limit = limit; }
}
