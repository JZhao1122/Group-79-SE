// === SpendingPatternAnalyzer.java ===
import java.time.YearMonth;
import java.util.*;

public class SpendingPatternAnalyzer {

    public static Map<YearMonth, Double> getMonthlySpendingTrend(List<Transaction> transactions) {
        Map<YearMonth, Double> monthlyTotals = new TreeMap<>();
        for (Transaction t : transactions) {
            YearMonth ym = YearMonth.from(t.date);
            monthlyTotals.put(ym, monthlyTotals.getOrDefault(ym, 0.0) + t.amount);
        }
        return monthlyTotals;
    }

    public static Map<String, Map<YearMonth, Double>> getCategorySpendingTrend(List<Transaction> transactions) {
        Map<String, Map<YearMonth, Double>> result = new HashMap<>();
        for (Transaction t : transactions) {
            if (t.category == null) continue;
            YearMonth ym = YearMonth.from(t.date);
            result.putIfAbsent(t.category, new TreeMap<>());
            Map<YearMonth, Double> monthMap = result.get(t.category);
            monthMap.put(ym, monthMap.getOrDefault(ym, 0.0) + t.amount);
        }
        return result;
    }

    private static double computeIQR(List<Double> values) {
        List<Double> sorted = new ArrayList<>(values);
        Collections.sort(sorted);
        int n = sorted.size();
        double q1 = sorted.get(n / 4);
        double q3 = sorted.get(3 * n / 4);
        return q3 - q1;
    }

    public static List<YearMonth> detectSpendingSpikes(Map<YearMonth, Double> monthlySpending) {
        List<Double> values = new ArrayList<>(monthlySpending.values());
        double avg = values.stream().mapToDouble(d -> d).average().orElse(0);
        double iqr = computeIQR(values);

        List<YearMonth> spikes = new ArrayList<>();
        for (Map.Entry<YearMonth, Double> entry : monthlySpending.entrySet()) {
            if (entry.getValue() > avg + 1.5 * iqr) {
                spikes.add(entry.getKey());
            }
        }
        return spikes;
    }

    public static double predictNextMonthSpending(Map<YearMonth, Double> monthlySpending) {
        
        List<Double> values = new ArrayList<>(monthlySpending.values());
        double avg = values.stream().mapToDouble(d -> d).average().orElse(0);
        double iqr = computeIQR(values);
        List<Double> filtered = new ArrayList<>();
        for (double v : values) {
            if (v < avg + 1.5 * iqr) filtered.add(v);
        }
        int n = filtered.size();
        if (n < 2) return avg;

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i + 1;
            double y = filtered.get(i);
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double a = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX + 1e-6);
        double b = (sumY - a * sumX) / n;
        return a * (n + 1) + b;
    }

    public static void printSpendingTrendSummary(List<Transaction> transactions) {
        Map<YearMonth, Double> trend = getMonthlySpendingTrend(transactions);
        List<YearMonth> spikes = detectSpendingSpikes(trend);

        System.out.println("\n >>> Monthly Spending Trend <<<");
        for (Map.Entry<YearMonth, Double> entry : trend.entrySet()) {
            String mark = spikes.contains(entry.getKey()) ? "  (Anomaly)" : "";
            System.out.printf(" - %s : $%.2f%s%n", entry.getKey(), entry.getValue(), mark);
        }

        double predicted = predictNextMonthSpending(trend);
        YearMonth nextMonth = trend.keySet().stream().max(Comparator.naturalOrder()).orElse(YearMonth.now()).plusMonths(1);
        System.out.printf("\n Predicted spending for next month (%s) ≈ $%.2f%n", nextMonth, predicted);

        System.out.println("\n >>> Category Spending Trend (Last 3 Months) <<<");
        Map<String, Map<YearMonth, Double>> catTrend = getCategorySpendingTrend(transactions);
        YearMonth latest = trend.keySet().stream().max(Comparator.naturalOrder()).orElse(null);

        if (latest != null) {
            for (String category : catTrend.keySet()) {
                System.out.printf(" - %s: ", category);
                Map<YearMonth, Double> perMonth = catTrend.get(category);
                for (int i = 2; i >= 0; i--) {
                    YearMonth m = latest.minusMonths(i);
                    double amt = perMonth.getOrDefault(m, 0.0);
                    System.out.printf(" %s:$%.1f ", m.getMonth().toString().substring(0, 3), amt);
                }
                System.out.println();
            }
        }
    }
}
