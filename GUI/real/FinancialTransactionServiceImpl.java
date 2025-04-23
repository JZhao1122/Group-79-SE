package service;

import dto.TransactionData;
import exception.TransactionException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FinancialTransactionServiceImpl implements FinancialTransactionService {
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public String addTransaction(TransactionData transaction) throws TransactionException {
        validateTransaction(transaction);
        // 实际存储逻辑...
        return "TXN-" + System.currentTimeMillis();
    }

    @Override
    public int importTransactions(InputStream inputStream) throws TransactionException {
        Objects.requireNonNull(inputStream, "Input stream cannot be null");
        
        int successCount = 0;
        int lineNumber = 0;
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            // 跳过可能的标题行
            reader.readLine();
            lineNumber++;
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    TransactionData transaction = parseTransactionLine(line);
                    addTransaction(transaction);
                    successCount++;
                } catch (Exception e) {
                    System.err.printf("Line %d: %s - %s%n", 
                        lineNumber, e.getMessage(), line);
                }
            }
            return successCount;
        } catch (Exception e) {
            throw new TransactionException("Failed to import transactions", e);
        }
    }

    private TransactionData parseTransactionLine(String line) throws TransactionException {
        String[] parts = line.split(",");
        if (parts.length < 5) {
            throw new TransactionException("Expected 5 fields: Date,Amount,Category,Description,PaymentMethod");
        }

        try {
            TransactionData transaction = new TransactionData();
            // 字段顺序: Date,Amount,Category,Description,PaymentMethod
            transaction.setDate(LocalDate.parse(parts[0].trim(), DATE_FORMATTER));
            transaction.setAmount(new BigDecimal(parts[1].trim()));
            transaction.setCategory(parts[2].trim());
            transaction.setDescription(parts[3].trim());
            transaction.setPaymentMethod(parts[4].trim());
            
            return transaction;
        } catch (NumberFormatException e) {
            throw new TransactionException("Invalid amount format: " + parts[1], e);
        } catch (Exception e) {
            throw new TransactionException("Invalid transaction data: " + line, e);
        }
    }

    private void validateTransaction(TransactionData transaction) throws TransactionException {
        if (transaction.getDate() == null) {
            throw new TransactionException("Date is required");
        }
        if (transaction.getAmount() == null || 
            transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Amount must be positive");
        }
        if (transaction.getCategory() == null || transaction.getCategory().isBlank()) {
            throw new TransactionException("Category is required");
        }
    }
}