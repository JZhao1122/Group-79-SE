// === TestRunner.java（最终精简版） ===
import java.time.LocalDate;
import java.util.*;

public class TestRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String testPath = "data/sample_transactions.csv";
        String trainPath = "data/train.csv";
        List<Transaction> transactions = DataLoader.loadTransactionsFromCSV(testPath);

        AITransactionClassifier.autoClassify(transactions, trainPath);

        System.out.println("\n 欢迎使用 AI 财务分析系统DeepManage");
        System.out.println("1. 全面运行所有功能");
        System.out.println("2. 分类器纠错反馈 → 更新训练集");
        System.out.print("请输入操作编号：");
        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {
            case 1 -> runAll(transactions, scanner);
            case 2 -> runFeedbackCorrection(transactions);
            default -> System.out.println("无效选项。");
        }
    }

    public static void runAll(List<Transaction> transactions, Scanner scanner) {
        System.out.println("\n 分类结果：");
        for (Transaction t : transactions) {
            System.out.printf(" - [%s] $%.2f → 分类：%s [%s]%n",
                    t.description, t.amount,
                    t.category == null ? "未识别" : t.category,
                    t.paymentMethod);
        }

        System.out.println("\n >>> AI预算建议 <<<");
        List<BudgetRecommender.BudgetSuggestion> suggestions =
                BudgetRecommender.recommendBudget(transactions);
        for (var s : suggestions) System.out.println(" - " + s);

        System.out.println("\n >>> 支出趋势分析 <<<");
        SpendingPatternAnalyzer.printSpendingTrendSummary(transactions);

        System.out.println("\n >>> 储蓄规划建议（单目标） <<<");
        var singlePlan = SavingsOptimizer.generatePlan(8000, LocalDate.of(2025, 12, 1), transactions);
        System.out.println(singlePlan);

        System.out.println("\n >>> 季节性支出检测 <<<");
        SeasonalSpendingDetector.printSeasonalSummary(transactions);

        System.out.println("\n >>> 预算异常预警 <<<");
        BudgetRecommender.detectAbnormalBudgets(transactions);

        System.out.print("\n是否添加多目标储蓄计划？(y/n)：");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            runMultiGoalPlanner(transactions, scanner);
        }
    }

    public static void runFeedbackCorrection(List<Transaction> transactions) {
        Scanner scanner = new Scanner(System.in);
        int count = 0;
        for (Transaction t : transactions) {
            if (t.category == null || t.category.equals("Entertainment")) {
                System.out.printf("描述：%s\n预测分类：%s\n请输入修正（回车跳过）：",
                        t.description, t.category == null ? "未分类" : t.category);
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    ClassifierFeedbackManager.recordCorrection(t.description, input);
                    count++;
                }
            }
        }
        System.out.println(" 共记录 " + count + " 条纠错。");
        System.out.print("是否写入训练集？(y/n)：");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            ClassifierFeedbackManager.updateTrainingData();
        }
    }

    public static void runMultiGoalPlanner(List<Transaction> transactions, Scanner scanner) {
        System.out.print("请输入要添加的目标个数：");
        int n = Integer.parseInt(scanner.nextLine());
        List<SavingsOptimizer.Goal> goals = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("\n 目标 " + (i + 1));
            System.out.print("名称：");
            String name = scanner.nextLine();
            System.out.print("金额：");
            double amt = Double.parseDouble(scanner.nextLine());
            System.out.print("截止日期（YYYY-MM-DD）：");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.print("优先级（1高 2中 3低）：");
            int pri = Integer.parseInt(scanner.nextLine());
            goals.add(new SavingsOptimizer.Goal(name, amt, date, pri));
        }
        List<SavingsOptimizer.GoalPlan> plans = SavingsOptimizer.planMultiGoal(goals, transactions);
        for (var p : plans) System.out.println(p);
    }
}
