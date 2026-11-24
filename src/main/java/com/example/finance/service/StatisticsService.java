package com.example.finance.service;
import com.example.finance.model.*;
import java.math.BigDecimal;
import java.util.*;

public class StatisticsService {
    public void printStats(Wallet wallet, String[] categories) {
        BigDecimal income = getTotal(wallet, TransactionType.INCOME);
        BigDecimal expense = getTotal(wallet, TransactionType.EXPENSE);

        System.out.println("\n--- СТАТИСТИКА ---");
        System.out.printf(Locale.US, "Общий доход: %.2f\n", income);
        System.out.printf(Locale.US, "Общие расходы: %.2f\n", expense);
        System.out.printf(Locale.US, "Текущий баланс: %.2f\n", income.subtract(expense));

        if (expense.compareTo(income) > 0) {
            System.out.println("[ВНИМАНИЕ] Ваши расходы превысили доходы!");
        }
        if (income.subtract(expense).compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("[ВНИМАНИЕ] Баланс равен нулю.");
        }

        System.out.println("\nБюджеты по категориям:");
        for (Budget b : wallet.getBudgets().values()) {
            boolean print = false;
            if (categories.length == 0) {
                print = true;
            } else {
                for (String c : categories) {
                    if (c.equalsIgnoreCase(b.getCategory())) { print = true; break; }
                }
            }
            
            if (print) {
                BigDecimal spent = wallet.getTransactions().stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE && t.getCategory().equals(b.getCategory()))
                        .map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal rem = b.getLimit().subtract(spent);
                System.out.printf(Locale.US, "- %s: Лимит %.2f | Потрачено %.2f | Остаток: %.2f\n", 
                        b.getCategory(), b.getLimit(), spent, rem);
            }
        }
        
        if (categories.length > 0) {
            boolean foundAny = false;
            for (String c : categories) {
                if (wallet.getBudgets().containsKey(c)) foundAny = true;
            }
            if (!foundAny) System.out.println("Указанные категории не найдены.");
        }
    }

    public BigDecimal getTotal(Wallet wallet, TransactionType type) {
        return wallet.getTransactions().stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
