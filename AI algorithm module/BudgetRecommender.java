// === BudgetRecommender.java===
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
            return String.format("类别：%s, 建议：%.2f 元, 说明：%s",
                    category, recommendedAmount, explanation);
        }
    }

    public static List<BudgetSuggestion> recommendBudget(List<Transaction> transactions) {
        Map<String, Map<Month, List<Double>>> categoryMonthlyMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.category == null) continue;
            categoryMonthlyMap
                    .computeIfAbsent(t.category, k -> new HashMap<>())
                    .computeIfAbsent(t.date.getMonth(), k -> new ArrayList<>())
                    .add(t.amount);
        }

        List<BudgetSuggestion> result = new ArrayList<>();

        for (String category : categoryMonthlyMap.keySet()) {
            Map<Month, List<Double>> monthly = categoryMonthlyMap.get(category);

            List<Double> monthlyTotals = monthly.values().stream()
                    .map(list -> list.stream().mapToDouble(Double::doubleValue).sum())
                    .collect(Collectors.toList());

            if (monthlyTotals.isEmpty()) continue;

            double avg = monthlyTotals.stream().mapToDouble(d -> d).average().orElse(0);

            if (monthlyTotals.size() == 1) {
                result.add(new BudgetSuggestion(category, avg,
                        "仅有1个月历史数据, 建议预算以该值为参考"));
                continue;
            }

            double stdDev = computeStdDev(monthlyTotals, avg);
            double lastMonthSpend = monthlyTotals.get(monthlyTotals.size() - 1);

            double adjusted = avg;
            String reason;

            if (Math.abs(lastMonthSpend - avg) > 1.5 * stdDev) {
                adjusted = avg;
                reason = "最近一个月支出波动较大，维持历史均值";
            } else if (lastMonthSpend > avg * 1.1) {
                adjusted = avg * 1.1;
                reason = "支出略有上升，预算略微提高";
            } else {
                adjusted = avg * 1.05;
                reason = "支出平稳，适度增加预算";
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

    public static void detectAbnormalBudgets(List<Transaction> transactions) {
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

        boolean warned = false;

        for (String category : currentMonthTotal.keySet()) {
            double currentSpend = currentMonthTotal.get(category);
            List<Double> history = categoryMonthlyHistory.get(category);
            double avg = history.stream().mapToDouble(d -> d).average().orElse(0);
            double std = computeStdDev(history, avg);

            if (currentSpend > avg + 1.2 * std) {
                warned = true;
                System.out.printf(" - 类别：%s  当前支出：%.2f 元，显著高于均值 %.2f 元 → 建议检查开销。%n",
                        category, currentSpend, avg);
            } else if (currentSpend > avg * 1.1) {
                System.out.printf(" - 类别：%s  当前支出：%.2f 元，略高于均值 %.2f 元。%n",
                        category, currentSpend, avg);
            }
        }

        if (!warned) {
            System.out.println(" 未检测到明显超额风险。");
        }
    }

    private static String getYearMonth(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }
}
