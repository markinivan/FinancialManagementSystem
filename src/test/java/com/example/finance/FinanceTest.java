package com.example.finance;

import com.example.finance.exception.AppException;
import com.example.finance.model.*;
import com.example.finance.repository.FileWalletRepository;
import com.example.finance.repository.InMemoryUserRepository;
import com.example.finance.service.*;
import com.example.finance.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class FinanceTest {
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = new Wallet("testUser");
    }

    @Test
    void testAddIncome() {
        wallet.getTransactions().add(new Transaction(TransactionType.INCOME, "Salary", new BigDecimal("5000")));
        assertEquals(1, wallet.getTransactions().size());
    }

    @Test
    void testAddExpense() {
        wallet.getTransactions().add(new Transaction(TransactionType.EXPENSE, "Food", new BigDecimal("1500")));
        assertEquals(1, wallet.getTransactions().size());
    }

    @Test
    void testTotalIncomeCalculation() {
        wallet.getTransactions().add(new Transaction(TransactionType.INCOME, "Salary", new BigDecimal("3000")));
        wallet.getTransactions().add(new Transaction(TransactionType.INCOME, "Bonus", new BigDecimal("1000")));
        StatisticsService stats = new StatisticsService();
        assertEquals(new BigDecimal("4000"), stats.getTotal(wallet, TransactionType.INCOME));
    }

    @Test
    void testTotalExpenseCalculation() {
        wallet.getTransactions().add(new Transaction(TransactionType.EXPENSE, "Food", new BigDecimal("500")));
        wallet.getTransactions().add(new Transaction(TransactionType.EXPENSE, "Taxi", new BigDecimal("500")));
        StatisticsService stats = new StatisticsService();
        assertEquals(new BigDecimal("1000"), stats.getTotal(wallet, TransactionType.EXPENSE));
    }

    @Test
    void testBudgetCalculation() {
        wallet.getBudgets().put("Food", new Budget("Food", new BigDecimal("2000")));
        wallet.getTransactions().add(new Transaction(TransactionType.EXPENSE, "Food", new BigDecimal("1500")));
        WalletService ws = new WalletService(new FileWalletRepository());
        ws.loadWallet("test");
        ws.getWallet().getTransactions().add(new Transaction(TransactionType.EXPENSE, "Food", new BigDecimal("1500")));
        assertEquals(new BigDecimal("1500"), ws.getSpentByCategory("Food"));
    }

    @Test
    void testValidationNegativeAmount() {
        assertThrows(AppException.class, () -> Validator.validateAmount("-100"));
    }

    @Test
    void testValidationInvalidStringAmount() {
        assertThrows(AppException.class, () -> Validator.validateAmount("abc"));
    }

    @Test
    void testValidationEmptyCategory() {
        assertThrows(AppException.class, () -> Validator.validateNotEmpty("", "Категория"));
    }

    @Test
    void testEmptyWalletBalance() {
        StatisticsService stats = new StatisticsService();
        assertEquals(BigDecimal.ZERO, stats.getTotal(wallet, TransactionType.INCOME));
    }

    @Test
    void testAuthRegisterAndLogin() {
        AuthService auth = new AuthService(new InMemoryUserRepository());
        auth.register("user1", "pass1");
        auth.login("user1", "pass1");
        assertTrue(auth.isLoggedIn());
    }

    @Test
    void testAuthLoginFail() {
        AuthService auth = new AuthService(new InMemoryUserRepository());
        auth.register("user1", "pass1");
        assertThrows(AppException.class, () -> auth.login("user1", "wrong"));
    }

    @Test
    void testAuthDuplicateRegister() {
        AuthService auth = new AuthService(new InMemoryUserRepository());
        auth.register("user1", "pass1");
        assertThrows(AppException.class, () -> auth.register("user1", "pass2"));
    }

    @Test
    void testBudgetEdit() {
        WalletService ws = new WalletService(new FileWalletRepository());
        ws.loadWallet("test");
        ws.getWallet().getBudgets().put("Food", new Budget("Food", new BigDecimal("1000")));
        ws.editBudget("Food", new BigDecimal("2000"));
        assertEquals(new BigDecimal("2000"), ws.getWallet().getBudgets().get("Food").getLimit());
    }

    @Test
    void testTransferLogic() {
        FileWalletRepository repo = new FileWalletRepository();
        TransferService ts = new TransferService(repo);
        ts.transfer(wallet, "recipient", new BigDecimal("100"));
        assertEquals(1, wallet.getTransactions().size());
        assertEquals(TransactionType.EXPENSE, wallet.getTransactions().get(0).getType());
    }

    @Test
    void testTransactionTypeEnum() {
        assertEquals("INCOME", TransactionType.INCOME.name());
        assertEquals("EXPENSE", TransactionType.EXPENSE.name());
    }

    @Test
    void testWalletGetOwner() {
        assertEquals("testUser", wallet.getOwnerLogin());
    }
}
