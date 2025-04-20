package service;

import ai_module.AITransactionClassifier; // Renamed based on API doc
import ai_module.SeasonalSpendingDetector; // Found in TestRunner
import dto.Transaction;
import dto.TransactionDetails; // Assuming this DTO has getDescription()
import exception.AlException;
import exception.QueryException;

import java.util.ArrayList;
import java.util.List;

public class RealTransactionAnalysisAlService implements TransactionAnalysisAlService {

    private final RealTransactionQueryService queryService; // Use specific type

    // Assume AITransactionClassifier needs initialization (e.g., loading model)
    // This is a placeholder; real initialization depends on the AI class implementation
    private final AITransactionClassifier classifier = new AITransactionClassifier(); // Placeholder init

    public RealTransactionAnalysisAlService(RealTransactionQueryService queryService) {
        this.queryService = queryService;
        System.out.println("[Real Service] Initializing RealTransactionAnalysisAlService.");
        // Placeholder: Train or load the classifier model if needed
        // classifier.loadModel("path/to/model");
        // Or potentially train on existing data if autoClassify is used initially
        // try {
        //     List<Transaction> allData = queryService.getAllTransactionsForUser("*"); // Get all data? Needs user concept.
        //     classifier.autoClassify(allData, "path/to/training.csv");
        // } catch (Exception e) { System.err.println("Initial classifier training failed: " + e); }
    }

    @Override
    public String categorizeTransaction(TransactionDetails details) throws AlException {
        if (details == null || details.getDescription() == null) {
            throw new AlException("Transaction details or description cannot be null for categorization.");
        }
        String description = details.getDescription();
        System.out.println("[Real Service] Categorizing transaction description: " + description);
        try {
            // --- AI Integration Point ---
            // Signature: String classify(String description)
            String category = classifier.classify(description);
            System.out.println("[Real Service] AI classified as: " + category);
            return category != null ? category : "Unclassified (AI)"; // Handle potential null from AI

        } catch (Exception e) {
            System.err.println("Error during transaction categorization AI execution: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("AI transaction categorization failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> analyzeSeasonalSpending(String userId) throws AlException {
        System.out.println("[Real Service] Analyzing seasonal spending for user: " + userId);
        try {
            // 1. Fetch user transactions
            List<Transaction> userTransactions = queryService.getAllTransactionsForUser(userId);

            // 2. Call the AI algorithm module's method
            // From TestRunner, found: SeasonalSpendingDetector.printSeasonalSummary(transactions)
            // This is a void method that prints to console.
            System.out.println("[Real Service] Calling AI: SeasonalSpendingDetector.printSeasonalSummary...");
            SeasonalSpendingDetector.printSeasonalSummary(userTransactions);
            System.out.println("[Real Service] AI call SeasonalSpendingDetector.printSeasonalSummary completed.");

            // 3. Process results
            // Similar to detectAbnormalBudgets, the AI method prints. We return a placeholder.
            List<String> insights = new ArrayList<>();
            insights.add("AI seasonal spending analysis executed (check console output for details).");
            return insights;

        } catch (QueryException e) {
            System.err.println("Error fetching transactions for seasonal analysis: " + e.getMessage());
            throw new AlException("Could not retrieve transaction data for seasonal analysis.", e);
        } catch (Exception e) {
            System.err.println("Error during seasonal spending AI execution: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("AI seasonal spending analysis failed: " + e.getMessage(), e);
        }
    }
} 