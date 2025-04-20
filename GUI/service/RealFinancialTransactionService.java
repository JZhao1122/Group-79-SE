package service;

import dto.Transaction;
import dto.TransactionData;
import exception.TransactionException;
import ai_module.DataLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RealFinancialTransactionService implements FinancialTransactionService {

    private final RealTransactionQueryService queryService;

    // Constructor injection
    public RealFinancialTransactionService(RealTransactionQueryService queryService) {
        this.queryService = queryService;
        System.out.println("[Real Service] Initializing RealFinancialTransactionService.");
    }

    @Override
    public String addTransaction(TransactionData data) throws TransactionException {
        System.out.println("[Real Service] Adding transaction: " + data.getDescription());
        if (data == null) {
            throw new TransactionException("Transaction data cannot be null.");
        }
        // Assuming TransactionData has getUserId() - ** CRITICAL ASSUMPTION **
        String userId = data.getUserId();
        if (userId == null || userId.trim().isEmpty()) {
             // Handle missing userId - throw exception or assign default?
             // Throwing exception is safer to enforce data integrity.
             throw new TransactionException("User ID is missing in TransactionData.");
        }

        // Generate a unique ID
        String newId = "TXN-" + UUID.randomUUID().toString();

        // Convert TransactionData to Transaction
        Transaction newTransaction = new Transaction(
                newId,
                data.getDate() != null ? data.getDate() : LocalDate.now(),
                data.getAmount() != null ? data.getAmount().doubleValue() : 0.0,
                data.getCategory(),
                data.getDescription(),
                data.getPaymentMethod(),
                userId // Pass the userId
        );

        // Add to the store via the query service (which handles saving)
        queryService.addTransactionInternal(newTransaction);
        System.out.println("[Real Service] Transaction added via QueryService with ID: " + newId);
        return newId;
    }

    @Override
    public int importTransactions(InputStream fileStream) throws TransactionException {
        System.out.println("[Real Service] Importing transactions from stream.");
        if (fileStream == null) {
            throw new TransactionException("Input stream cannot be null.");
        }

        List<Transaction> loadedTransactions = new ArrayList<>();
        // --- Real InputStream Parsing Logic (Example for CSV) ---
        // TODO: Determine the format (CSV, OFX, etc.) and use appropriate parser.
        //       This CSV example assumes the same format as transactions.csv and assigns a fixed userId.
        //       A real implementation needs a way to determine the userId for imported transactions.
        String importUserId = "user123"; // Hardcoded User ID for import - NEEDS REVIEW

        try (BufferedReader br = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8))) {
            String headerLine = br.readLine(); // Read/skip header
            // Optional: Validate header if format is known
            System.out.println("Importing with header: " + headerLine);

            String line;
            while ((line = br.readLine()) != null) {
                 if (line.trim().isEmpty()) continue;
                 String[] parts = line.split(",", -1); // Basic CSV split
                 if (parts.length >= 5) { // Expecting date, amount, category, description, paymentMethod
                     try {
                         // Generate unique ID for imported transaction
                         String id = "IMP-" + UUID.randomUUID().toString();
                         LocalDate date = LocalDate.parse(parts[0]);
                         double amount = Double.parseDouble(parts[1]);
                         String category = parts[2].isEmpty() ? null : parts[2];
                         String description = parts[3];
                         String paymentMethod = parts[4];
                         // Assign the predetermined importUserId
                         loadedTransactions.add(new Transaction(id, date, amount, category, description, paymentMethod, importUserId));
                     } catch (DateTimeParseException | NumberFormatException e) {
                         System.err.println("WARN: Skipping invalid line during import: " + line + " Error: " + e.getMessage());
                     } catch (ArrayIndexOutOfBoundsException e) {
                          System.err.println("WARN: Skipping malformed line (not enough columns) during import: " + line);
                     }
                 } else {
                      System.err.println("WARN: Skipping malformed line (not enough columns) during import: " + line);
                 }
            }
        } catch (IOException e) {
            System.err.println("ERROR: Failed to read from input stream during import: " + e.getMessage());
            throw new TransactionException("Failed to read import file.", e);
        }

        // --- End Parsing Logic ---

        if (!loadedTransactions.isEmpty()) {
            int count = queryService.importTransactionsInternal(loadedTransactions);
            System.out.println("[Real Service] Imported " + count + " transactions via QueryService.");
            return count;
        } else {
            System.out.println("[Real Service] No valid transactions found in the input stream.");
            return 0;
        }
    }
} 