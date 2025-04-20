package service;

import dto.Transaction; // Assuming Transaction is in dto or ai_module
import exception.QueryException;
import exception.TransactionException;

import java.io.*; // Import IO classes
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets; // Specify charset
import java.nio.file.Files; // For directory creation
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // For date parsing errors
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class RealTransactionQueryService implements TransactionQueryService {

    private static final String DATA_DIR = "data"; // Data directory name
    private static final String DATA_FILE = DATA_DIR + "/transactions.csv"; // CSV file path
    private static final String CSV_HEADER = "id,date,amount,category,description,paymentMethod,userId";
    private static final String CSV_DELIMITER = ",";

    // Use thread-safe list as cache/working copy after loading from file
    private final List<Transaction> transactionStore = new CopyOnWriteArrayList<>();

    public RealTransactionQueryService() {
        System.out.println("[Real Service] Initializing RealTransactionQueryService with file store: " + DATA_FILE);
        ensureDataDirectoryExists();
        loadTransactionsFromFile();
    }

    private void ensureDataDirectoryExists() {
        try {
            Path path = Paths.get(DATA_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                System.out.println("Created data directory: " + path.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("FATAL: Could not create data directory: " + DATA_DIR + " Error: " + e.getMessage());
            // Depending on requirements, might throw a runtime exception
        }
    }

    private void loadTransactionsFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.out.println("Data file not found: " + DATA_FILE + ". Starting with empty store. Will create on first save.");
            return;
        }

        transactionStore.clear(); // Clear existing cache before loading
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line = br.readLine(); // Read header

            // Basic header check (optional but recommended)
            if (line == null || !line.trim().equals(CSV_HEADER)) {
                System.err.println("WARN: CSV header mismatch or empty file. Expected: '" + CSV_HEADER + "' Found: '" + line + "'. Attempting to load anyway or starting fresh.");
                // Decide how to handle: throw error, log, or try loading anyway
                if (line == null) return; // Empty file is okay
            }

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // Skip empty lines
                String[] parts = line.split(CSV_DELIMITER, -1); // Use -1 to keep trailing empty strings
                if (parts.length >= 7) { // Check for enough parts
                    try {
                        String id = parts[0];
                        LocalDate date = LocalDate.parse(parts[1]);
                        double amount = Double.parseDouble(parts[2]);
                        String category = parts[3].isEmpty() ? null : parts[3]; // Handle empty category as null
                        String description = parts[4];
                        String paymentMethod = parts[5];
                        String userId = parts[6];
                        transactionStore.add(new Transaction(id, date, amount, category, description, paymentMethod, userId));
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.err.println("WARN: Skipping invalid line in CSV: " + line + " Error: " + e.getMessage());
                    } catch (ArrayIndexOutOfBoundsException e) {
                         System.err.println("WARN: Skipping malformed line (not enough columns) in CSV: " + line);
                    }
                } else {
                     System.err.println("WARN: Skipping malformed line (not enough columns) in CSV: " + line);
                }
            }
            System.out.println("Loaded " + transactionStore.size() + " transactions from " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to load transactions from file: " + DATA_FILE + " Error: " + e.getMessage());
            // Consider clearing the store or throwing an exception depending on policy
            transactionStore.clear();
        }
    }

    // Synchronized to prevent race conditions when multiple threads trigger saves
    private synchronized void saveTransactionsToFile() {
        System.out.println("Attempting to save " + transactionStore.size() + " transactions to " + DATA_FILE);
        // Write the entire list back to the file, overwriting it
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DATA_FILE), StandardCharsets.UTF_8))) {
            bw.write(CSV_HEADER);
            bw.newLine();
            for (Transaction t : transactionStore) {
                bw.write(escapeCsvField(t.getId()) + CSV_DELIMITER +
                         t.getDate().toString() + CSV_DELIMITER +
                         String.valueOf(t.getAmount()) + CSV_DELIMITER +
                         escapeCsvField(Objects.toString(t.getCategory(), "")) + CSV_DELIMITER + // Write null category as empty string
                         escapeCsvField(t.getDescription()) + CSV_DELIMITER +
                         escapeCsvField(t.getPaymentMethod()) + CSV_DELIMITER +
                         escapeCsvField(t.getUserId()));
                bw.newLine();
            }
            System.out.println("Successfully saved transactions to " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("ERROR: Failed to save transactions to file: " + DATA_FILE + " Error: " + e.getMessage());
            // Handle error - maybe log, maybe notify user, maybe throw exception
        }
    }

    // Helper to handle potential commas or quotes in CSV fields (basic version)
    private String escapeCsvField(String field) {
        if (field == null) return "";
        field = field.replace("\"", "\"\""); // Escape double quotes
        if (field.contains(CSV_DELIMITER) || field.contains("\"") || field.contains("\n")) {
            return "\"" + field + "\""; // Enclose in double quotes
        }
        return field;
    }

    // Internal method called by RealFinancialTransactionService
    void addTransactionInternal(Transaction transaction) {
        if (transaction != null) {
            transactionStore.add(transaction);
            saveTransactionsToFile(); // Persist after adding
        }
    }

    // Internal method called by RealFinancialTransactionService
    int importTransactionsInternal(List<Transaction> transactions) {
        if (transactions != null && !transactions.isEmpty()) {
            transactionStore.addAll(transactions);
            saveTransactionsToFile(); // Persist after importing
            return transactions.size();
        }
        return 0;
    }

    // --- Public Service Methods --- (Operate on the in-memory cache)

    @Override
    public List<Transaction> getTransactionsForReview(String userId) throws QueryException {
        System.out.println("[Real Service] Getting transactions needing review (uncategorized) for user: " + userId + " from cache.");
        if (userId == null) throw new QueryException("User ID cannot be null");
        return transactionStore.stream()
                .filter(t -> userId.equals(t.getUserId()) && (t.getCategory() == null || t.getCategory().isEmpty()))
                .collect(Collectors.toList());
    }

    @Override
    public void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException {
        System.out.println("[Real Service] Updating category for transaction ID: " + transactionId + " to " + correctedCategory + " in cache.");
        if (transactionId == null) throw new TransactionException("Transaction ID cannot be null");

        boolean updated = false;
        for (Transaction t : transactionStore) {
            if (transactionId.equals(t.getId())) { // Use Objects.equals if ID can be null
                t.setCategory(correctedCategory);
                updated = true;
                System.out.println("Transaction " + transactionId + " category updated in cache.");
                break;
            }
        }

        if (updated) {
            saveTransactionsToFile(); // Persist the change
        } else {
            System.err.println("Transaction with ID " + transactionId + " not found for category update.");
            // Optionally throw an exception
            // throw new TransactionException("Transaction with ID " + transactionId + " not found.");
        }
    }

    public List<Transaction> getAllTransactionsForUser(String userId) throws QueryException {
         System.out.println("[Real Service] Getting ALL transactions for user: " + userId + " from cache.");
         if (userId == null) throw new QueryException("User ID cannot be null");
         return transactionStore.stream()
                 .filter(t -> userId.equals(t.getUserId()))
                 .collect(Collectors.toList());
     }
} 