// === SeasonalSpendingDetector.java ===
import java.time.Month;
import java.util.*;

public class SeasonalSpendingDetector {

    public static Map<Month, Double> detectSeasonalSpending(List<Transaction> transactions) {
        Map<Month, Double> spendingByMonth = new EnumMap<>(Month.class);
        for (Transaction t : transactions) {
            Month m = t.date.getMonth();
            spendingByMonth.put(m, spendingByMonth.getOrDefault(m, 0.0) + t.amount);
        }

        List<Double> values = new ArrayList<>(spendingByMonth.values());
        double avg = values.stream().mapToDouble(d -> d).average().orElse(0);
        double std = Math.sqrt(values.stream().mapToDouble(d -> Math.pow(d - avg, 2)).sum() / values.size());

        Map<Month, Double> seasonalPeaks = new LinkedHashMap<>();
        for (Map.Entry<Month, Double> entry : spendingByMonth.entrySet()) {
            if (entry.getValue() > avg + 1.2 * std) {
                seasonalPeaks.put(entry.getKey(), entry.getValue());
            }
        }
        return seasonalPeaks;
    }

    public static void printSeasonalSummary(List<Transaction> transactions) {
        Map<Month, Double> all = new EnumMap<>(Month.class);
        for (Transaction t : transactions) {
            Month m = t.date.getMonth();
            all.put(m, all.getOrDefault(m, 0.0) + t.amount);
        }
        Map<Month, Double> peaks = detectSeasonalSpending(transactions);

        System.out.println("\n >>> 月份支出分布 <<<");
        all.keySet().stream().sorted(Comparator.comparingInt(Month::getValue)).forEach(m -> {
            double amt = all.get(m);
            String mark = peaks.containsKey(m) ? "高峰" : "";
            System.out.printf(" - %s: $%.2f%s%n", m.toString().substring(0, 3), amt, mark);
        });

        if (peaks.isEmpty()) {
            System.out.println("\n 本年度暂无明显季节性支出高峰。");
        } else {
            System.out.println("\n 检测到以下季节性高消费月份：");
            peaks.forEach((month, amount) -> System.out.printf(" - %s: $%.2f%n", month, amount));
        }
    }
}