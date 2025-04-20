
package service;

import dto.TransactionData;
import exception.TransactionException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FinancialTransactionServiceImpl implements FinancialTransactionService {
    @Override
    public String addTransaction(TransactionData transaction) throws TransactionException {
        // Implementation for adding a single transaction
        // (Assuming you have validation logic here)
        return "Transaction added successfully"; // Return some confirmation ID
    }

    @Override
    public int importTransactions(InputStream inputStream) throws TransactionException {
        if (inputStream == null) {
            throw new TransactionException("Input stream cannot be null");
        }

        List<TransactionData> transactions = new ArrayList<>();
        int lineNumber = 0;
        int successCount = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    // Skip empty lines and comments
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    // Parse the line into TransactionData
                    TransactionData transaction = parseTransactionLine(line);
                    addTransaction(transaction); // Add to system
                    successCount++;
                    
                } catch (Exception e) {
                    System.err.printf("Error processing line %d: %s%n", lineNumber, e.getMessage());
                    // Or collect all errors and throw at the end
                }
            }
            
            return successCount;
            
        } catch (Exception e) {
            throw new TransactionException("Failed to import transactions: " + e.getMessage(), e);
        }
    }

    private TransactionData parseTransactionLine(String line) throws TransactionException {
        // Example CSV format: "amount,currency,description,date"
        String[] parts = line.split(",");
        
        if (parts.length < 4) {
            throw new TransactionException("Invalid transaction format: " + line);
        }

        try {
            TransactionData transaction = new TransactionData();
            transaction.setAmount(Double.parseDouble(parts[0].trim()));
            transaction.setCurrency(parts[1].trim());
            transaction.setDescription(parts[2].trim());
            transaction.setDate(parts[3].trim()); // Or parse to Date object
            
            // Add validation as needed
            if (transaction.getAmount() <= 0) {
                throw new TransactionException("Amount must be positive");
            }
            
            return transaction;
            
        } catch (NumberFormatException e) {
            throw new TransactionException("Invalid number format in: " + line, e);
        }
    }
}