package service;

import ai_module.BudgetRecommender;
import ai_module.BudgetRecommender.BudgetSuggestion; // Import the DTO
// import ai_module.SavingsOptimizer; // Keep for future implementation
import dto.Transaction; // Use the DTO from the ai_module package
import exception.AlException;
import exception.QueryException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RealFinancialHealthAlService implements FinancialHealthAlService {

    private final RealTransactionQueryService queryService; // Use specific type for getAllTransactionsForUser

    public RealFinancialHealthAlService(RealTransactionQueryService queryService) {
        this.queryService = queryService;
        System.out.println("[Real Service] Initializing RealFinancialHealthAlService.");
    }

    @Override
    public Map<String, BigDecimal> recommendBudget(String userId) throws AlException {
        System.out.println("[Real Service] Recommending budget for user: " + userId);
        try {
            // 1. Fetch ALL user transactions
            List<Transaction> userTransactions = queryService.getAllTransactionsForUser(userId);

            // 2. Call the AI algorithm module's method
            // Signature: List<BudgetSuggestion> recommendBudget(List<Transaction> transactions)
            List<BudgetSuggestion> suggestions = BudgetRecommender.recommendBudget(userTransactions);

            // 3. Convert List<BudgetSuggestion> to Map<String, BigDecimal>
            Map<String, BigDecimal> recommendedBudget = new HashMap<>();
            if (suggestions != null) {
                for (BudgetSuggestion suggestion : suggestions) {
                    if (suggestion != null && suggestion.category != null) {
                        // Convert double to BigDecimal, ensuring scale and rounding
                        recommendedBudget.put(suggestion.category,
                                BigDecimal.valueOf(suggestion.recommendedAmount).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }
            }
            System.out.println("[Real Service] Budget recommendation generated from AI module.");
            return recommendedBudget;

        } catch (QueryException e) {
            System.err.println("Error fetching transactions for budget recommendation: " + e.getMessage());
            throw new AlException("Could not retrieve transaction data to recommend budget.", e);
        } catch (Exception e) {
            System.err.println("Error during budget recommendation AI execution: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("AI budget recommendation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException {
        System.out.println("[Real Service] Allocating savings for user: " + userId);
        // --- AI Integration Point ---
        // TODO: Implement savings allocation. The AI module has SavingsOptimizer with:
        // - SavingsPlan generatePlan(double totalGoal, LocalDate deadline, List<Transaction> transactions)
        // - List<GoalPlan> planMultiGoal(List<Goal> goals, List<Transaction> transactions)
        // This service interface method needs clarification on how it maps to the AI module.
        // Does it assume a single default goal? Or needs goal info passed differently?

        System.err.println("Real savings allocation AI logic needs implementation based on SavingsOptimizer.");
        throw new AlException("Savings allocation feature requires mapping to SavingsOptimizer AI.");
    }

    @Override
    public List<String> detectSpendingPatterns(String userId) throws AlException {
        System.out.println("[Real Service] Detecting spending patterns for user: " + userId);
        try {
            // 1. Fetch user transactions
            List<Transaction> userTransactions = queryService.getAllTransactionsForUser(userId);

            // 2. Call the AI algorithm module's method
            // Signature: void detectAbnormalBudgets(List<Transaction> transactions)
            // This method prints warnings to the console according to the API doc.
            System.out.println("[Real Service] Calling AI: BudgetRecommender.detectAbnormalBudgets...");
            BudgetRecommender.detectAbnormalBudgets(userTransactions);
            System.out.println("[Real Service] AI call BudgetRecommender.detectAbnormalBudgets completed.");

            // 3. Process results
            // Since the AI method is void and prints, we can't directly return its findings here.
            // We return a message indicating the check was performed.
            // For real implementation, consider modifying the AI to return results,
            // or using a logging framework to capture the AI's output.
            List<String> patterns = new ArrayList<>();
            patterns.add("AI abnormal budget detection routine executed (check console output for details).");
            // Optionally, could call other pattern analysis AI if available
            // Example: List<String> trendInsights = SpendingPatternAnalyzer.analyzeTrends(userTransactions);
            // patterns.addAll(trendInsights);

            return patterns;

        } catch (QueryException e) {
            System.err.println("Error fetching transactions for pattern detection: " + e.getMessage());
            throw new AlException("Could not retrieve transaction data for pattern detection.", e);
        } catch (Exception e) {
            System.err.println("Error during spending pattern detection AI execution: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("AI pattern detection failed: " + e.getMessage(), e);
        }
    }
} 