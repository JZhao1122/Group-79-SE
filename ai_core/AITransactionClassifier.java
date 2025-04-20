import java.util.*;

public class AITransactionClassifier {

    private static final Map<String, Map<String, Integer>> wordCategoryFreq = new HashMap<>();
    private static final Map<String, Integer> categoryDocCount = new HashMap<>();
    private static final Map<String, Integer> categoryWordCount = new HashMap<>(); 

    public static void trainModel(List<Transaction> transactions) {
        wordCategoryFreq.clear();
        categoryDocCount.clear();
        categoryWordCount.clear(); 
        for (Transaction t : transactions) {
            if (t.category == null) continue;
            String category = t.category;
            categoryDocCount.put(category, categoryDocCount.getOrDefault(category, 0) + 1);
            for (String word : tokenize(t.description)) {
                wordCategoryFreq.putIfAbsent(word, new HashMap<>());
                Map<String, Integer> freqMap = wordCategoryFreq.get(word);
                freqMap.put(category, freqMap.getOrDefault(category, 0) + 1);
                categoryWordCount.put(category, categoryWordCount.getOrDefault(category, 0) + 1); 
            }
        }
    }

    public static String classify(String description) {
        Set<String> words = tokenize(description);
        String bestCategory = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        int vocabSize = wordCategoryFreq.size(); 

        int totalDocs = categoryDocCount.values().stream().mapToInt(i -> i).sum();

        for (String category : categoryDocCount.keySet()) {
            double logProb = Math.log((categoryDocCount.get(category) + 1.0) / (totalDocs + categoryDocCount.size()));
            int totalWordsInCat = categoryWordCount.getOrDefault(category, 0);

            for (String word : words) {
                int wordInCat = wordCategoryFreq.getOrDefault(word, new HashMap<>()).getOrDefault(category, 0);
                logProb += Math.log((wordInCat + 1.0) / (totalWordsInCat + vocabSize));
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

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "the", "and", "is", "in", "at", "of", "a", "to", "for", "on", "with", "by", "an", "be", "this", "that"
    ));

    private static Set<String> tokenize(String text) {
        String clean = text.toLowerCase().replaceAll("[^a-z0-9 ]", " ");
        String[] words = clean.split("\\s+");
        Set<String> tokens = new HashSet<>();
        for (String word : words) {
            if (!STOP_WORDS.contains(word) && !word.isEmpty()) {
                tokens.add(word);
            }
        }
        return tokens;
    }
}
