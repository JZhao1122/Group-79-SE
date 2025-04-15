// === ClassifierFeedbackManager.java ===
import java.io.*;
import java.util.*;

public class ClassifierFeedbackManager {

    private static final String FEEDBACK_FILE = "data/feedback_log.csv";
    private static final String TRAINING_FILE = "data/train.csv";

    // Record correction: save to feedback_log.csv
    public static void recordCorrection(String description, String correctCategory) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FEEDBACK_FILE, true))) {
            writer.write(String.format("%s,%s\n", escape(description), correctCategory));
        } catch (IOException e) {
            System.err.println(" Failed to write correction record: " + e.getMessage());
        }
    }

    // View correction history
    public static List<String[]> loadCorrectionHistory() {
        List<String[]> list = new ArrayList<>();
        File f = new File(FEEDBACK_FILE);
        if (!f.exists()) return list;
        try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) list.add(parts);
            }
        } catch (IOException e) {
            System.err.println(" Failed to load correction history: " + e.getMessage());
        }
        return list;
    }

    // Append corrections to training data (train.csv)
    public static void updateTrainingData() {
        List<String[]> feedback = loadCorrectionHistory();
        if (feedback.isEmpty()) {
            System.out.println(" No need to update training data (no new corrections).");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRAINING_FILE, true))) {
            for (String[] entry : feedback) {
                String desc = entry[0];
                String category = entry[1];
                String today = java.time.LocalDate.now().toString();
                double dummyAmount = 100.00;
                String dummyPayment = "Manual";
                writer.write(String.format("%s,%.2f,%s,%s,%s\n",
                        today, dummyAmount, category, unescape(desc), dummyPayment));
            }
            System.out.println(" Appended " + feedback.size() + " correction(s) to training set (train.csv).");
            new File(FEEDBACK_FILE).delete();
        } catch (IOException e) {
            System.err.println(" Failed to update training data: " + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace(",", "<COMMA>");
    }

    private static String unescape(String s) {
        return s.replace("<COMMA>", ",");
    }
}
