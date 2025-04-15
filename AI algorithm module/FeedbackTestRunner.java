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

        System.out.println("Data loaded and classification complete. Starting user feedback simulation:");
        int correctionCount = 0;

        for (Transaction t : transactions) {
            if (t.category == null || t.category.equalsIgnoreCase("Entertainment")) {
                System.out.printf("Description: %s\nCurrent category: %s\nEnter the correct category (or press Enter to skip): ",
                        t.description, t.category == null ? "Uncategorized" : t.category);
                String input = scanner.nextLine().trim();
                if (!input.isEmpty()) {
                    ClassifierFeedbackManager.recordCorrection(t.description, input);
                    correctionCount++;
                }
            }
        }

        System.out.println("\nTotal corrections recorded: " + correctionCount);

        System.out.print("Would you like to update the training set now? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            ClassifierFeedbackManager.updateTrainingData();
        } else {
            System.out.println("Feedback has been saved. You can call updateTrainingData() later.\n");
        }
    }
}
