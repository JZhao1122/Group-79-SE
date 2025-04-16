// === SavingsOptimizer.java ===
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
            return String.format(" Savings Goal: $%.2f, Period: %s → %s\n→ Monthly Saving: $%.2f\n Weekly Saving: $%.2f\n Recommendation: %s",
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
            advice = "Consider reducing non-essential expenses, such as entertainment or shopping, to meet your savings goal.";
        } else if (monthlySave < recentAvgSpend * 0.3) {
            advice = "You can easily achieve this goal. Consider increasing your target or shortening the deadline.";
        } else {
            advice = "The savings plan is reasonable. Maintain your current spending habits.";
        }

        return new SavingsPlan(totalGoal, today, deadline, monthlySave, weeklySave, advice);
    }

    // === Multi-goal enhancement ===
    public static class Goal {
        public String name;
        public double amount;
        public LocalDate deadline;
        public int priority; // 1 = high, 2 = medium, 3 = low

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
            return String.format(" Goal: %s | Amount: %.2f CNY | Deadline: %s\n → Monthly Saving: %.2f CNY, Weekly Saving: %.2f CNY\n  Recommendation: %s\n",
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
                advice = " The saving burden is high. Consider adjusting the deadline or reducing the target.";
            } else if (goal.priority == 1) {
                advice = " High priority goal. Recommended to execute with priority.";
            } else if (goal.priority == 3) {
                advice = " Low priority goal. Consider postponing if budget is tight.";
            } else {
                advice = " Reasonable goal. Recommended to follow the plan.";
            }

            plans.add(new GoalPlan(goal, monthly, weekly, advice));
        }
        return plans;
    }
}
