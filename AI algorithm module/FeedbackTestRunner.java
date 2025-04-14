// === FeedbackTestRunner.java ===
import java.util.*;
import java.util.Scanner;

public class FeedbackTestRunner {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String path = "data/sample_transactions.csv";
        String trainPath = "data/train.csv";

        List<Transaction> transactions = DataLoader.loadTransactionsFromCSV(path);
        AITransactionClassifier.autoClassify(transactions, trainPath);

        System.out.println(" 加载并分类完成，开始用户反馈纠错模拟：");
        int correctionCount = 0;

        for (Transaction t : transactions) {
            if (t.category == null || t.category.equalsIgnoreCase("Entertainment")) {
                System.out.printf("描述：%s\n当前分类：%s\n请输入你认为正确的分类（或回车跳过）：",
                        t.description, t.category == null ? "未分类" : t.category);
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    ClassifierFeedbackManager.recordCorrection(t.description, input);
                    correctionCount++;
                }
            }
        }

        System.out.println("\n 共记录 " + correctionCount + " 条用户纠错。");

        System.out.print("是否立即更新训练集 (y/n)：");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            ClassifierFeedbackManager.updateTrainingData();
        } else {
            System.out.println(" 反馈记录已保存，可稍后调用 updateTrainingData()。\n");
        }
    }
}
