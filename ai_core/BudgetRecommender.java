// === BudgetRecommender.java ===
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class BudgetRecommender {

    public static class BudgetSuggestion {
        public String category;
        public double recommendedAmount;
        public String explanation;

        public BudgetSuggestion(String category, double recommendedAmount, String explanation) {
            this.category = category;
            this.recommendedAmount = recommendedAmount;
            this.explanation = explanation;
        }

        @Override
        public String toString() {
            return String.format("Category: %s, Suggestion: %.2f CNY, Reason: %s",
                    category, recommendedAmount, explanation);
        }
    }

    private static double predictNextMonth(List<Double> monthlyTotals) {
        int n = monthlyTotals.size();
        if (n < 2) return monthlyTotals.get(n - 1);
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += monthlyTotals.get(i);
            sumXY += i * monthlyTotals.get(i);
            sumXX += i * i;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX + 1e-8);
        double intercept = (sumY - slope * sumX) / n;
        return slope * n + intercept;
    }

    public static List<BudgetSuggestion> recommendBudget(List<Transaction> transactions) {
        Map<String, Map<Month, List<Double>>> categoryMonthlyMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.category == null || t.amount < 0) continue;
            categoryMonthlyMap
                    .computeIfAbsent(t.category, k -> new HashMap<>())
                    .computeIfAbsent(t.date.getMonth(), k -> new ArrayList<>())
                    .add(t.amount);
        }

        List<BudgetSuggestion> result = new ArrayList<>();
        Month currentMonth = LocalDate.now().getMonth();

        for (String category : categoryMonthlyMap.keySet()) {
            Map<Month, List<Double>> monthly = categoryMonthlyMap.get(category);

            List<Double> monthlyTotals = monthly.values().stream()
                    .map(list -> list.stream().mapToDouble(Double::doubleValue).sum())
                    .collect(Collectors.toList());

            if (monthlyTotals.isEmpty()) continue;

            double avg = monthlyTotals.stream().mapToDouble(d -> d).average().orElse(0);

            double currentMonthTotal = monthly.getOrDefault(currentMonth, new ArrayList<>())
                    .stream().mapToDouble(Double::doubleValue).sum();
            double adjusted = avg;
            String reason = "";

            if (monthlyTotals.size() == 1) {
                result.add(new BudgetSuggestion(category, avg,
                        "Only one month of data available, using that as reference."));
                continue;
            }

            double stdDev = computeStdDev(monthlyTotals, avg);

            double trendPrediction = predictNextMonth(monthlyTotals);
            if (trendPrediction > avg * 1.1) {
                adjusted = trendPrediction;
                reason = "Upward spending trend detected, using trend prediction.";
            } else if (currentMonthTotal > avg + stdDev) {
                adjusted = currentMonthTotal * 1.05;
                reason = "Seasonal peak detected for this month, budget slightly increased.";
            } else if (Math.abs(currentMonthTotal - avg) > 1.5 * stdDev) {
                adjusted = avg;
                reason = "Significant fluctuation last month; keep average budget.";
            } else if (currentMonthTotal > avg * 1.1) {
                adjusted = avg * 1.1;
                reason = "Slight increase detected; increasing budget slightly.";
            } else {
                adjusted = avg * 1.05;
                reason = "Stable spending; slightly increasing budget.";
            }

            result.add(new BudgetSuggestion(category, adjusted, reason));
        }

        return result;
    }

    private static double computeStdDev(List<Double> values, double mean) {
        double sumSquaredDiffs = 0;
        for (double v : values) {
            sumSquaredDiffs += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sumSquaredDiffs / values.size());
    }

    private static double computeIQR(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        double q1 = sorted.get(n / 4);
        double q3 = sorted.get(3 * n / 4);
        return q3 - q1;
    }

    public static List<Map<String, Object>> detectAbnormalBudgetsStructured(List<Transaction> transactions) {
        Map<String, Double> currentMonthTotal = new HashMap<>();
        Map<String, List<Double>> categoryMonthlyHistory = new HashMap<>();

        String thisMonth = getYearMonth(transactions.stream()
                .map(t -> t.date)
                .max(Comparator.naturalOrder()).orElse(LocalDate.now()));

        for (Transaction t : transactions) {
            if (t.category == null) continue;
            String ym = getYearMonth(t.date);
            categoryMonthlyHistory.putIfAbsent(t.category, new ArrayList<>());

            if (ym.equals(thisMonth)) {
                currentMonthTotal.put(t.category,
                        currentMonthTotal.getOrDefault(t.category, 0.0) + t.amount);
            }
            categoryMonthlyHistory.get(t.category).add(t.amount);
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (String category : currentMonthTotal.keySet()) {
            double currentSpend = currentMonthTotal.get(category);
            List<Double> history = categoryMonthlyHistory.get(category);
            double avg = history.stream().mapToDouble(d -> d).average().orElse(0);
            double iqr = computeIQR(history);

            Map<String, Object> record = new HashMap<>();
            record.put("category", category);
            record.put("currentSpend", currentSpend);
            record.put("average", avg);
            record.put("iqr", iqr);

            if (currentSpend > avg + 1.5 * iqr) {
                record.put("level", "high");
                record.put("message", "Significantly above normal range, please review.");
            } else if (currentSpend > avg + 0.75 * iqr) {
                record.put("level", "medium");
                record.put("message", "Slightly above normal range.");
            } else {
                record.put("level", "normal");
                record.put("message", "No significant overspending detected.");
            }
            result.add(record);
        }
        return result;
    }

    private static String getYearMonth(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }
}
