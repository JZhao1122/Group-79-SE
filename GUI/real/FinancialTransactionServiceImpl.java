package real;

import dto.TransactionData;
import dto.TransactionDetails;
import exception.TransactionException;
import exception.AlException;
import service.FinancialTransactionService;
import service.TransactionAnalysisAlService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class FinancialTransactionServiceImpl implements FinancialTransactionService {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final List<TransactionData> transactionList = new ArrayList<>();
    private final TransactionAnalysisAlService aiService;

    public FinancialTransactionServiceImpl(TransactionAnalysisAlService aiService) {
        this.aiService = aiService;
    }

    @Override
    public String addTransaction(TransactionData transaction) throws TransactionException {
        validateTransaction(transaction);
        // AI classification
        try {
            TransactionDetails details = new TransactionDetails();
            details.setDescription(transaction.getDescription());
            details.setAmount(transaction.getAmount());
            details.setDate(transaction.getDate());
            String aiCategory = aiService.categorizeTransaction(details);
            transaction.setCategory(aiCategory);
        } catch (AlException e) {
            throw new TransactionException("AI classification failed: " + e.getMessage(), e);
        }
        String id = "TXN-" + System.currentTimeMillis();
        transaction.setId(id);
        transactionList.add(transaction);
        return id;
    }

    @Override
    public int importTransactions(InputStream inputStream) throws TransactionException {
        Objects.requireNonNull(inputStream, "Input stream cannot be null");

        int successCount = 0;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            reader.readLine();
            lineNumber++;

            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    TransactionData transaction = parseTransactionLine(line);
                    // 自动AI分类
                    try {
                        TransactionDetails details = new TransactionDetails();
                        details.setDescription(transaction.getDescription());
                        details.setAmount(transaction.getAmount());
                        details.setDate(transaction.getDate());
                        String aiCategory = aiService.categorizeTransaction(details);
                        transaction.setCategory(aiCategory);
                    } catch (AlException e) {
                        System.err.printf("Line %d: AI classification failed - %s%n", lineNumber, e.getMessage());
                    }
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
            transaction.setDate(LocalDate.parse(parts[0].trim(), DATE_FORMATTER));
            transaction.setAmount(new BigDecimal(parts[1].trim()));
            transaction.setCategory(parts[2].trim());
            transaction.setDescription(parts[3].trim());
            transaction.setPaymentMethod(parts[4].trim());
            return transaction;
        } catch (NumberFormatException e) {
            throw new TransactionException("Invalid amount format: " + parts[1], e);
        } catch (Exception e) {
            // e.printStackTrace();
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
        if (transaction.getDescription() == null || transaction.getDescription().isBlank()) {
            throw new TransactionException("Description is required");
        }
    }

    @Override
    public List<TransactionData> getAllTransactions() {
        return new ArrayList<>(transactionList);
    }
    
    @Override
    public List<TransactionData> getAllTransactions(String userId) throws TransactionException {
        // This method returns all transaction data without filtering by userId
        // In production environment, should filter transaction data by userId
        return new ArrayList<>(transactionList);
    }
}