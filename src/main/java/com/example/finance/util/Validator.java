package com.example.finance.util;
import com.example.finance.exception.AppException;
import java.math.BigDecimal;

public class Validator {
    public static void validateAmount(String amountStr) {
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new AppException("Сумма должна быть больше нуля.");
            }
        } catch (NumberFormatException e) {
            throw new AppException("Некорректный формат суммы. Введите число.");
        }
    }
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new AppException(fieldName + " не может быть пустым.");
        }
    }
}
