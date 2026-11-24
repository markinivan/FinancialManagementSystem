package com.example.finance;

import com.example.finance.exception.AppException;
import com.example.finance.model.TransactionType;
import com.example.finance.repository.FileWalletRepository;
import com.example.finance.service.*;
import com.example.finance.util.Validator;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    private static final AuthService auth = new AuthService();
    private static final FileWalletRepository repo = new FileWalletRepository();
    private static final WalletService walletService = new WalletService(repo);
    private static final StatisticsService stats = new StatisticsService();
    private static final TransferService transferService = new TransferService(repo);
    private static final CsvService csvService = new CsvService();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Finance Manager ===");

        while (true) {
            if (!auth.isLoggedIn()) {
                System.out.print("> (guest) ");
            } else {
                System.out.print("> (" + auth.getCurrentUser().getLogin() + ") ");
            }

            String input = sc.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+");
            String cmd = parts[0].toLowerCase();

            try {
                if (cmd.equals("exit")) {
                    if (auth.isLoggedIn()) walletService.saveWallet();
                    System.out.println("До свидания.");
                    break;
                } 
                else if (cmd.equals("help")) {
                    printHelp();
                } 
                else if (cmd.equals("register") && parts.length == 3) {
                    auth.register(parts[1], parts[2]);
                    System.out.println("Пользователь зарегистрирован.");
                } 
                else if (cmd.equals("login") && parts.length == 3) {
                    auth.login(parts[1], parts[2]);
                    walletService.loadWallet(parts[1]);
                    System.out.println("Успешный вход.");
                } 
                else if (cmd.equals("logout")) {
                    walletService.saveWallet();
                    auth.logout();
                    System.out.println("Выход выполнен. Данные сохранены.");
                }
                else {
                    auth.getCurrentUser(); 
                    
                    if (cmd.equals("income") && parts.length == 3) {
                        Validator.validateNotEmpty(parts[1], "Категория");
                        Validator.validateAmount(parts[2]);
                        walletService.addTransaction(TransactionType.INCOME, parts[1], parts[2]);
                        System.out.println("Доход добавлен.");
                    }
                    else if (cmd.equals("expense") && parts.length == 3) {
                        Validator.validateNotEmpty(parts[1], "Категория");
                        Validator.validateAmount(parts[2]);
                        walletService.addTransaction(TransactionType.EXPENSE, parts[1], parts[2]);
                        System.out.println("Расход добавлен.");
                    }
                    else if (cmd.equals("budget") && parts.length >= 3) {
                        if (parts[1].equals("set") && parts.length == 4) {
                            Validator.validateAmount(parts[3]);
                            walletService.getWallet().getBudgets().put(parts[2], 
                                new com.example.finance.model.Budget(parts[2], new BigDecimal(parts[3])));
                            System.out.println("Бюджет установлен.");
                        } else if (parts[1].equals("edit") && parts.length == 4) {
                            Validator.validateAmount(parts[3]);
                            walletService.editBudget(parts[2], new BigDecimal(parts[3]));
                            System.out.println("Бюджет обновлен.");
                        } else {
                            throw new AppException("Использование: budget set/edit <категория> <сумма>");
                        }
                    }
                    else if (cmd.equals("stats")) {
                        String[] cats = Arrays.copyOfRange(parts, 1, parts.length);
                        stats.printStats(walletService.getWallet(), cats);
                    }
                    else if (cmd.equals("transfer") && parts.length == 3) {
                        Validator.validateAmount(parts[2]);
                        BigDecimal amount = new BigDecimal(parts[2]);
                        if (stats.getTotal(walletService.getWallet(), TransactionType.EXPENSE)
                                .add(amount).compareTo(stats.getTotal(walletService.getWallet(), TransactionType.INCOME)) > 0) {
                            throw new AppException("Недостаточно средств для перевода.");
                        }
                        transferService.transfer(walletService.getWallet(), parts[1], amount);
                    }
                    else if (cmd.equals("export") && parts.length == 2) {
                        csvService.exportWallet(walletService.getWallet(), parts[1]);
                    }
                    else if (cmd.equals("import") && parts.length == 2) {
                        csvService.importWallet(walletService.getWallet(), parts[1]);
                    }
                    else {
                        System.out.println("Неизвестная команда. Введите help.");
                    }
                }
            } catch (AppException e) {
                System.err.println("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Системная ошибка: " + e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                Доступные команды:
                register <login> <password>
                login <login> <password>
                logout
                income <категория> <сумма>
                expense <категория> <сумма>
                budget set <категория> <сумма>
                budget edit <категория> <сумма>
                stats
                stats <категория1> <категория2>
                transfer <login> <сумма>
                export <файл.csv>
                import <файл.csv>
                exit
                """);
    }
}
