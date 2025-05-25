import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Financial Management Application Core Functions Test
 * Test Coverage: Transaction classification, budget recommendations, savings optimization, data processing
 */
@DisplayName("Financial Management Application Core Functions Test")
public class FinancialAppMainFunctionsTest {
    
    private List<Transaction> testTransactions;
    private LocalDate today;
    
    @BeforeEach
    @DisplayName("Prepare Test Data")
    void setUp() {
        today = LocalDate.now();
        testTransactions = createTestTransactions();
        System.out.println("🔧 Test environment prepared, total " + testTransactions.size() + " test transaction records");
    }
    
    @Test
    @DisplayName("1. Test Transaction Object Creation and Basic Properties")
    void testTransactionCreation() {
        System.out.println("\n📝 Testing transaction object creation...");
        
        Transaction transaction = new Transaction(
            today, 
            150.50, 
            "Dining", 
            "KFC Lunch", 
            "Alipay"
        );
        
        assertNotNull(transaction, "Transaction object should not be null");
        assertEquals(today, transaction.date, "Transaction date should be correct");
        assertEquals(150.50, transaction.amount, 0.01, "Transaction amount should be correct");
        assertEquals("Dining", transaction.category, "Transaction category should be correct");
        assertEquals("KFC Lunch", transaction.description, "Transaction description should be correct");
        assertEquals("Alipay", transaction.paymentMethod, "Payment method should be correct");
        
        System.out.println("✅ Transaction object creation test passed");
    }
    
    @Test
    @DisplayName("2. Test AI Transaction Classification Function")
    void testAITransactionClassifier() {
        System.out.println("\n🤖 Testing AI transaction classification...");
        
        // Train classifier
        AITransactionClassifier.trainModel(testTransactions);
        
        // Test known classifications
        String category1 = AITransactionClassifier.classify("Starbucks Coffee");
        String category2 = AITransactionClassifier.classify("Sinopec Gas Station");
        String category3 = AITransactionClassifier.classify("JD Shopping");
        
        assertNotNull(category1, "Classification result should not be null");
        System.out.println("   Starbucks Coffee → Classified as: " + category1);
        System.out.println("   Sinopec Gas Station → Classified as: " + category2);
        System.out.println("   JD Shopping → Classified as: " + category3);
        
        // Test automatic classification function
        List<Transaction> unclassifiedTransactions = Arrays.asList(
            new Transaction(today, 88.0, null, "McDonald's Meal", "WeChat Pay"),
            new Transaction(today, 500.0, null, "Walmart Supermarket", "Credit Card")
        );
        
        AITransactionClassifier.trainModel(testTransactions);
        for (Transaction t : unclassifiedTransactions) {
            String predicted = AITransactionClassifier.classify(t.description);
            t.category = predicted;
        }
        
        assertTrue(unclassifiedTransactions.stream().allMatch(t -> t.category != null), 
                  "All transactions should be classified");
        
        System.out.println("✅ AI transaction classification test passed");
    }
    
    @Test
    @DisplayName("3. Test Budget Recommendation Function")
    void testBudgetRecommender() {
        System.out.println("\n💰 Testing budget recommendation...");
        
        List<BudgetRecommender.BudgetSuggestion> suggestions = 
            BudgetRecommender.recommendBudget(testTransactions);
        
        assertFalse(suggestions.isEmpty(), "Should have budget suggestions");
        
        for (BudgetRecommender.BudgetSuggestion suggestion : suggestions) {
            assertNotNull(suggestion.category, "Category should not be null");
            assertTrue(suggestion.recommendedAmount > 0, "Recommended amount should be greater than 0");
            assertNotNull(suggestion.explanation, "Explanation should not be null");
            
            System.out.println("   " + suggestion.category + ": " + 
                             String.format("$%.2f", suggestion.recommendedAmount) + 
                             " (" + suggestion.explanation + ")");
        }
        
        System.out.println("✅ Budget recommendation test passed");
    }
    
    @Test
    @DisplayName("4. Test Savings Plan Generation Function")
    void testSavingsOptimizer() {
        System.out.println("\n💎 Testing savings plan generation...");
        
        double savingsGoal = 10000.0; // $10,000 savings goal
        LocalDate deadline = today.plusMonths(6); // 6 months later
        
        SavingsOptimizer.SavingsPlan plan = SavingsOptimizer.generatePlan(
            savingsGoal, deadline, testTransactions);
        
        assertNotNull(plan, "Savings plan should not be null");
        assertEquals(savingsGoal, plan.totalGoal, 0.01, "Savings goal should be correct");
        assertEquals(deadline, plan.endDate, "Deadline should be correct");
        assertTrue(plan.monthlyAmount > 0, "Monthly savings amount should be greater than 0");
        assertTrue(plan.weeklyAmount > 0, "Weekly savings amount should be greater than 0");
        assertNotNull(plan.recommendation, "Recommendation should not be null");
        
        System.out.println("   Savings Goal: $" + plan.totalGoal);
        System.out.println("   Monthly Savings: " + String.format("$%.2f", plan.monthlyAmount));
        System.out.println("   Weekly Savings: " + String.format("$%.2f", plan.weeklyAmount));
        System.out.println("   Recommendation: " + plan.recommendation);
        
        System.out.println("✅ Savings plan generation test passed");
    }
    
    @Test
    @DisplayName("5. Test Multi-Goal Savings Planning Function")
    void testMultiGoalSavingsPlanning() {
        System.out.println("\n🎯 Testing multi-goal savings planning...");
        
        List<SavingsOptimizer.Goal> goals = Arrays.asList(
            new SavingsOptimizer.Goal("Emergency Fund", 20000.0, today.plusMonths(12), 1),
            new SavingsOptimizer.Goal("Travel Fund", 8000.0, today.plusMonths(8), 2),
            new SavingsOptimizer.Goal("New Phone", 5000.0, today.plusMonths(4), 3)
        );
        
        List<SavingsOptimizer.GoalPlan> plans = 
            SavingsOptimizer.planMultiGoal(goals, testTransactions);
        
        assertEquals(goals.size(), plans.size(), "Number of plans should equal number of goals");
        
        for (SavingsOptimizer.GoalPlan plan : plans) {
            assertNotNull(plan.goal, "Goal should not be null");
            assertTrue(plan.monthly > 0, "Monthly savings should be greater than 0");
            assertTrue(plan.weekly > 0, "Weekly savings should be greater than 0");
            assertNotNull(plan.remark, "Remark should not be null");
            
            System.out.println("   " + plan.goal.name + ": Monthly $" + 
                             String.format("%.2f", plan.monthly) + " | " + plan.remark);
        }
        
        System.out.println("✅ Multi-goal savings planning test passed");
    }
    
    @Test
    @DisplayName("6. Test Abnormal Spending Detection Function")
    void testAbnormalSpendingDetection() {
        System.out.println("\n⚠️ Testing abnormal spending detection...");
        
        List<Map<String, Object>> results = 
            BudgetRecommender.detectAbnormalBudgetsStructured(testTransactions);
        
        assertFalse(results.isEmpty(), "Should have detection results");
        
        for (Map<String, Object> result : results) {
            assertNotNull(result.get("category"), "Category should not be null");
            assertNotNull(result.get("currentSpend"), "Current spend should not be null");
            assertNotNull(result.get("level"), "Level should not be null");
            assertNotNull(result.get("message"), "Message should not be null");
            
            String level = (String) result.get("level");
            assertTrue(Arrays.asList("normal", "medium", "high").contains(level), 
                      "Level should be a valid value");
            
            System.out.println("   " + result.get("category") + ": " + 
                             result.get("level") + " - " + result.get("message"));
        }
        
        System.out.println("✅ Abnormal spending detection test passed");
    }
    
    @Test
    @DisplayName("7. Test Data Processing and Statistics Function")
    void testDataProcessingAndStatistics() {
        System.out.println("\n📊 Testing data processing and statistics...");
        
        // Test category statistics
        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        
        for (Transaction t : testTransactions) {
            if (t.category != null) {
                categoryTotals.put(t.category, 
                    categoryTotals.getOrDefault(t.category, 0.0) + t.amount);
                categoryCount.put(t.category, 
                    categoryCount.getOrDefault(t.category, 0) + 1);
            }
        }
        
        assertFalse(categoryTotals.isEmpty(), "Should have category statistics data");
        
        // Find the category with highest spending
        String maxCategory = categoryTotals.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("None");
        
        // Calculate total spending
        double totalSpending = testTransactions.stream()
            .mapToDouble(t -> t.amount)
            .sum();
        
        assertTrue(totalSpending > 0, "Total spending should be greater than 0");
        assertNotEquals("None", maxCategory, "Should be able to find the highest spending category");
        
        System.out.println("   Total Spending: " + String.format("$%.2f", totalSpending));
        System.out.println("   Highest Spending Category: " + maxCategory + 
                         " (" + String.format("$%.2f", categoryTotals.get(maxCategory)) + ")");
        System.out.println("   Number of Categories: " + categoryTotals.size());
        
        System.out.println("✅ Data processing and statistics test passed");
    }
    
    @Test
    @DisplayName("8. Test Edge Cases and Exception Handling")
    void testEdgeCasesAndExceptionHandling() {
        System.out.println("\n🛡️ Testing edge cases and exception handling...");
        
        // Test empty transaction list
        List<Transaction> emptyList = new ArrayList<>();
        List<BudgetRecommender.BudgetSuggestion> emptySuggestions = 
            BudgetRecommender.recommendBudget(emptyList);
        assertTrue(emptySuggestions.isEmpty(), "Empty transaction list should return empty suggestions");
        
        // Test transaction with null category
        Transaction nullCategoryTransaction = new Transaction(
            today, 100.0, null, "Unknown Expense", "Cash");
        assertDoesNotThrow(() -> {
            List<Transaction> singleList = Arrays.asList(nullCategoryTransaction);
            BudgetRecommender.recommendBudget(singleList);
        }, "Should not throw exception when handling null category");
        
        // Test negative amount transaction
        Transaction negativeTransaction = new Transaction(
            today, -50.0, "Refund", "Return", "Alipay");
        assertDoesNotThrow(() -> {
            List<Transaction> negativeList = Arrays.asList(negativeTransaction);
            BudgetRecommender.recommendBudget(negativeList);
        }, "Should not throw exception when handling negative amount");
        
        // Test savings plan with past deadline
        assertThrows(Exception.class, () -> {
            SavingsOptimizer.generatePlan(1000.0, today.minusDays(1), testTransactions);
        }, "Past deadline should throw exception");
        
        System.out.println("   ✓ Empty list handling normal");
        System.out.println("   ✓ Null category handling normal");
        System.out.println("   ✓ Negative amount handling normal");
        System.out.println("   ✓ Exception date detection normal");
        
        System.out.println("✅ Edge cases and exception handling test passed");
    }
    
    @AfterEach
    void tearDown() {
        System.out.println("🧹 Test cleanup completed\n");
    }
    
    /**
     * Create test transaction data
     */
    private List<Transaction> createTestTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        
        // Dining transactions
        transactions.add(new Transaction(today.minusDays(30), 45.0, "Dining", "Starbucks Coffee", "Credit Card"));
        transactions.add(new Transaction(today.minusDays(25), 68.5, "Dining", "Haidilao Hotpot", "Alipay"));
        transactions.add(new Transaction(today.minusDays(20), 32.0, "Dining", "KFC Lunch", "WeChat Pay"));
        
        // Transportation transactions
        transactions.add(new Transaction(today.minusDays(28), 300.0, "Transportation", "Sinopec Gas", "Credit Card"));
        transactions.add(new Transaction(today.minusDays(15), 15.5, "Transportation", "Subway", "Transit Card"));
        transactions.add(new Transaction(today.minusDays(10), 28.0, "Transportation", "Didi Taxi", "Alipay"));
        
        // Shopping transactions
        transactions.add(new Transaction(today.minusDays(22), 299.0, "Shopping", "JD Digital Accessories", "Credit Card"));
        transactions.add(new Transaction(today.minusDays(18), 158.0, "Shopping", "Taobao Clothing", "Alipay"));
        transactions.add(new Transaction(today.minusDays(12), 89.0, "Shopping", "Supermarket Daily Items", "Cash"));
        
        // Entertainment transactions
        transactions.add(new Transaction(today.minusDays(26), 80.0, "Entertainment", "Cinema Movie", "Alipay"));
        transactions.add(new Transaction(today.minusDays(14), 120.0, "Entertainment", "KTV Party", "WeChat Pay"));
        
        // Living expenses transactions
        transactions.add(new Transaction(today.minusDays(29), 1200.0, "Living", "Rent", "Bank Transfer"));
        transactions.add(new Transaction(today.minusDays(16), 180.0, "Living", "Utilities", "Alipay"));
        
        return transactions;
    }
} 