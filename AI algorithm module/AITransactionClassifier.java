import java.util.*;

public class AITransactionClassifier {

    private static final Map<String, Map<String, Integer>> wordCategoryFreq = new HashMap<>();
    private static final Map<String, Integer> categoryDocCount = new HashMap<>();

    public static void trainModel(List<Transaction> transactions) {
        for (Transaction t : transactions) {
            if (t.category == null) continue;
            String category = t.category;
            categoryDocCount.put(category, categoryDocCount.getOrDefault(category, 0) + 1);
            for (String word : tokenize(t.description)) {
                wordCategoryFreq.putIfAbsent(word, new HashMap<>());
                Map<String, Integer> freqMap = wordCategoryFreq.get(word);
                freqMap.put(category, freqMap.getOrDefault(category, 0) + 1);
            }
        }
    }

    public static String classify(String description) {
        Set<String> words = tokenize(description);
        String bestCategory = null;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (String category : categoryDocCount.keySet()) {
            double logProb = Math.log(categoryDocCount.get(category) + 1);
            for (String word : words) {
                int wordInCat = wordCategoryFreq.getOrDefault(word, new HashMap<>()).getOrDefault(category, 0);
                logProb += Math.log(wordInCat + 1);
            }
            if (logProb > bestScore) {
                bestScore = logProb;
                bestCategory = category;
            }
        }
        return bestCategory;
    }

    public static void autoClassify(List<Transaction> transactions, String trainingCsvPath) {
        List<Transaction> trainingData = DataLoader.loadTransactionsFromCSV(trainingCsvPath);
        trainModel(trainingData);
        for (Transaction t : transactions) {
            if (t.category != null) continue;
            String predicted = classify(t.description);
            if (predicted != null) {
                t.category = predicted;
            }
        }
    }

    private static Set<String> tokenize(String text) {
        String clean = text.toLowerCase().replaceAll("[^a-z0-9 ]", " ");
        String[] words = clean.split("\\s+");
        return new HashSet<>(Arrays.asList(words));
    }
}