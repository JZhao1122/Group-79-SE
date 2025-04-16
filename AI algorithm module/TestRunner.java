// === TestRunner.java (Final Compact Version) ===
import java.time.LocalDate;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String testPath = "data/sample_transactions.csv";
        String trainPath = "data/train.csv";
        List<Transaction> transactions = DataLoader.loadTransactionsFromCSV(testPath);

        AITransactionClassifier.autoClassify(transactions, trainPath);

        System.out.println("\nWelcome to DeepManage: AI Financial Analysis System");
        System.out.println("1. Run all features");
        System.out.println("2. Feedback correction → Update training set");
        System.out.print("Enter your choice: ");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> runAll(transactions, scanner);
            case 2 -> runFeedbackCorrection(transactions);
            default -> System.out.println("Invalid option.");
        }
    }

    public static void runAll(List<Transaction> transactions, Scanner scanner) {
        System.out.println("\nClassification Results:");
        for (Transaction t : transactions) {
            System.out.printf(" - [%s] $%.2f → Category: %s [%s]%n",
                    t.description, t.amount,
                    t.category == null ? "Unrecognized" : t.category,
                    t.paymentMethod);
        }

        System.out.println("\n>>> AI Budget Recommendations <<<");
        List<BudgetRecommender.BudgetSuggestion> suggestions =
                BudgetRecommender.recommendBudget(transactions);
        for (var s : suggestions) System.out.println(" - " + s);

        System.out.println("\n>>> Spending Trend Analysis <<<");
        SpendingPatternAnalyzer.printSpendingTrendSummary(transactions);

        System.out.println("\n>>> Savings Plan Recommendation (Single Goal) <<<");
        var singlePlan = SavingsOptimizer.generatePlan(8000, LocalDate.of(2025, 12, 1), transactions);
        System.out.println(singlePlan);

        System.out.println("\n>>> Seasonal Spending Detection <<<");
        SeasonalSpendingDetector.printSeasonalSummary(transactions);

        System.out.println("\n>>> Budget Anomaly Detection <<<");
        BudgetRecommender.detectAbnormalBudgets(transactions);

        System.out.print("\nWould you like to add multi-goal savings plans? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            runMultiGoalPlanner(transactions, scanner);
        }
    }

    public static void runFeedbackCorrection(List<Transaction> transactions) {
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        for (Transaction t : transactions) {
            if (t.category == null || t.category.equals("Entertainment")) {
                System.out.printf("Description: %s\nPredicted Category: %s\nEnter correction (or press Enter to skip): ",
                        t.description, t.category == null ? "Uncategorized" : t.category);
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    ClassifierFeedbackManager.recordCorrection(t.description, input);
                    count++;
                }
            }
        }
        System.out.println("Total corrections recorded: " + count);
        System.out.print("Write to training set? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            ClassifierFeedbackManager.updateTrainingData();
        }
    }

    public static void runMultiGoalPlanner(List<Transaction> transactions, Scanner scanner) {
        System.out.print("Enter the number of goals to add: ");
        int n = Integer.parseInt(scanner.nextLine());
        List<SavingsOptimizer.Goal> goals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("\nGoal " + (i + 1));
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Amount: ");
            double amt = Double.parseDouble(scanner.nextLine());
            System.out.print("Deadline (YYYY-MM-DD): ");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.print("Priority (1=High, 2=Medium, 3=Low): ");
            int pri = Integer.parseInt(scanner.nextLine());
            goals.add(new SavingsOptimizer.Goal(name, amt, date, pri));
        }
        List<SavingsOptimizer.GoalPlan> plans = SavingsOptimizer.planMultiGoal(goals, transactions);
        for (var p : plans) System.out.println(p);
    }
}
