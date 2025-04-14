// === SavingsOptimizer.java===
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class SavingsOptimizer {

    public static class SavingsPlan {
        public double totalGoal;
        public LocalDate startDate;
        public LocalDate endDate;
        public double monthlyAmount;
        public double weeklyAmount;
        public String recommendation;

        public SavingsPlan(double totalGoal, LocalDate startDate, LocalDate endDate,
                           double monthlyAmount, double weeklyAmount, String recommendation) {
            this.totalGoal = totalGoal;
            this.startDate = startDate;
            this.endDate = endDate;
            this.monthlyAmount = monthlyAmount;
            this.weeklyAmount = weeklyAmount;
            this.recommendation = recommendation;
        }

        @Override
        public String toString() {
            return String.format(" 储蓄目标：$%.2f, 期限：%s → %s\n→ 每月储蓄：$%.2f\n 建议：%s",
                    totalGoal, startDate, endDate, monthlyAmount, weeklyAmount, recommendation);
        }
    }

    public static SavingsPlan generatePlan(double totalGoal, LocalDate deadline, List<Transaction> transactions) {
        LocalDate today = LocalDate.now();
        long totalDays = ChronoUnit.DAYS.between(today, deadline);
        long totalWeeks = Math.max(1, totalDays / 7);
        long totalMonths = Math.max(1, ChronoUnit.MONTHS.between(today.withDayOfMonth(1), deadline.withDayOfMonth(1)));

        double monthlySave = totalGoal / totalMonths;
        double weeklySave = totalGoal / totalWeeks;

        LocalDate threeMonthsAgo = today.minusMonths(3);
        double recentAvgSpend = transactions.stream()
                .filter(t -> t.date.isAfter(threeMonthsAgo))
                .mapToDouble(t -> t.amount).sum() / 3.0;

        String advice;
        if (monthlySave > recentAvgSpend * 0.7) {
            advice = "建议减少非必要消费，如娱乐或购物类，以保证储蓄计划执行。";
        } else if (monthlySave < recentAvgSpend * 0.3) {
            advice = "你可以轻松达成储蓄目标，建议适当提高目标或缩短期限。";
        } else {
            advice = "储蓄计划合理，建议保持当前消费水平。";
        }

        return new SavingsPlan(totalGoal, today, deadline, monthlySave, weeklySave, advice);
    }

    // === 多目标增强部分 ===
    public static class Goal {
        public String name;
        public double amount;
        public LocalDate deadline;
        public int priority; // 1高 2中 3低

        public Goal(String name, double amount, LocalDate deadline, int priority) {
            this.name = name;
            this.amount = amount;
            this.deadline = deadline;
            this.priority = priority;
        }
    }

    public static class GoalPlan {
        public Goal goal;
        public double monthly;
        public double weekly;
        public String remark;

        public GoalPlan(Goal goal, double monthly, double weekly, String remark) {
            this.goal = goal;
            this.monthly = monthly;
            this.weekly = weekly;
            this.remark = remark;
        }

        @Override
        public String toString() {
            return String.format(" 目标：%s | 金额：%.2f 元 | 截止：%s\n → 每月需存：%.2f 元，每周需存：%.2f 元\n  建议：%s\n",
                    goal.name, goal.amount, goal.deadline.toString(), monthly, weekly, remark);
        }
    }

    public static List<GoalPlan> planMultiGoal(List<Goal> goals, List<Transaction> transactions) {
        List<GoalPlan> plans = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate threeMonthsAgo = today.minusMonths(3);

        double recentSpending = transactions.stream()
                .filter(t -> t.date.isAfter(threeMonthsAgo))
                .mapToDouble(t -> t.amount).sum();
        double avgMonthSpend = recentSpending / 3.0;

        for (Goal goal : goals) {
            long months = Math.max(1, ChronoUnit.MONTHS.between(today.withDayOfMonth(1), goal.deadline.withDayOfMonth(1)));
            long weeks = Math.max(1, ChronoUnit.WEEKS.between(today, goal.deadline));
            double monthly = goal.amount / months;
            double weekly = goal.amount / weeks;

            String advice;
            if (monthly > avgMonthSpend * 0.5) {
                advice = " 金额压力较大，请考虑调整期限或缩减预算";
            } else if (goal.priority == 1) {
                advice = " 高优先级目标，建议优先执行";
            } else if (goal.priority == 3) {
                advice = " 低优先级目标，如预算紧张可延期";
            } else {
                advice = " 合理目标，建议按计划执行";
            }

            plans.add(new GoalPlan(goal, monthly, weekly, advice));
        }
        return plans;
    }
}
