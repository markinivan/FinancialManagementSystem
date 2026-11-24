package com.example.finance.service;
import com.example.finance.model.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public void exportWallet(Wallet wallet, String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        
        for (Transaction t : wallet.getTransactions()) {
            lines.add("T," + t.getId() + "," + t.getType() + "," + t.getCategory() + "," + t.getAmount() + "," + t.getTimestamp().format(FORMATTER));
        }
        
        for (Budget b : wallet.getBudgets().values()) {
            lines.add("B," + b.getCategory() + "," + b.getLimit());
        }
        
        Files.write(Path.of(filename), lines);
        System.out.println("Экспорт завершен в файл " + filename);
    }

    public void importWallet(Wallet wallet, String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Файл не найден.");
            return;
        }
        
        List<String> lines = Files.readAllLines(Path.of(filename));
        int tCount = 0;
        int bCount = 0;
        
        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length > 0) {
                if (parts[0].equals("T") && parts.length == 6) {
                    Transaction t = new Transaction();
                    t.setId(parts[1]);
                    t.setType(TransactionType.valueOf(parts[2]));
                    t.setCategory(parts[3]);
                    t.setAmount(new BigDecimal(parts[4]));
                    t.setTimestamp(LocalDateTime.parse(parts[5], FORMATTER));
                    wallet.getTransactions().add(t);
                    tCount++;
                } else if (parts[0].equals("B") && parts.length == 3) {
                    wallet.getBudgets().put(parts[1], new Budget(parts[1], new BigDecimal(parts[2])));
                    bCount++;
                }
            }
        }
        System.out.println("Импортировано транзакций: " + tCount + ", бюджетов: " + bCount);
    }
}
