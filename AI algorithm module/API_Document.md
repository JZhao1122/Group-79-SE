# 🔌 DeepManage 项目AI算法部分接口文档（Java版）

> 文档版本：v1.0  
> 编写日期：2025-04  
> 说明：本接口文档基于 Java 本地调用方式，适用于 UI 层直接调用 Java 类的场景，所有模块输入为 `List<Transaction>` 数据对象。

---

## 通用定义

```java
public class Transaction {
    public LocalDate date;           // 交易日期
    public double amount;            // 金额
    public String category;          // 类别（可为空）
    public String description;       // 描述（如“麦当劳”）
    public String paymentMethod;     // 支付方式（如 Bank / WeChat / Alipay）
}
```

---

## Module 1: 智能交易分类器  
**接口类名：`AITransactionClassifier`**

```java
void autoClassify(List<Transaction> transactions, String trainingCsvPath);
String classify(String description);
```

| 方法 | 描述 |
|------|------|
| `autoClassify` | 对未分类交易进行自动分类，结果写回到原始对象的 `category` 字段。训练数据从指定 CSV 读取。 |
| `classify` | 针对一条描述信息预测类别，适用于快速试探分类效果。 |

---

## Module 2: 智能预算推荐器  
**接口类名：`BudgetRecommender`**

```java
List<BudgetSuggestion> recommendBudget(List<Transaction> transactions);
void detectAbnormalBudgets(List<Transaction> transactions);
```

### DTO: `BudgetSuggestion`

```java
public class BudgetSuggestion {
    public String category;
    public double recommendedAmount;
    public String explanation;
}
```

| 方法 | 描述 |
|------|------|
| `recommendBudget` | 基于用户消费历史，返回各分类推荐预算及说明。 |
| `detectAbnormalBudgets` | 检测当前月的消费是否超过历史平均并输出警告（控制台打印） |

---

## Module 3: 用户反馈学习模块  
**接口类名：`ClassifierFeedbackManager`**

```java
void recordCorrection(String desc, String correctCategory);
void updateTrainingData();
List<String[]> loadCorrectionHistory();
```

| 方法 | 描述 |
|------|------|
| `recordCorrection` | 用户修正分类后调用，记录在 `feedback_log.csv` |
| `updateTrainingData` | 将反馈数据写入训练集文件 `train.csv` 并清除反馈记录 |
| `loadCorrectionHistory` | 加载历史反馈，前端可调用展示在列表中 |

---

## Module 4: 储蓄目标优化器  
**接口类名：`SavingsOptimizer`**

```java
SavingsPlan generatePlan(double totalGoal, LocalDate deadline, List<Transaction> transactions);
List<GoalPlan> planMultiGoal(List<Goal> goals, List<Transaction> transactions);
```

### DTOs: `Goal` 与 `GoalPlan`

```java
public class Goal {
    public String name;
    public double amount;
    public LocalDate deadline;
    public int priority; // 1: 高，2: 中，3: 低
}

public class GoalPlan {
    public Goal goal;
    public double monthly;
    public double weekly;
    public String remark;
}
```

---

## Module 5: 消费趋势分析器  
**接口类名：`SpendingPatternAnalyzer`**

```java
void printSpendingTrendSummary(List<Transaction> transactions);
Map<YearMonth, Double> getMonthlySpendingTrend(...);
List<YearMonth> detectSpendingSpikes(...);
double predictNextMonthSpending(...);
```

| 方法 | 描述 |
|------|------|
| `printSpendingTrendSummary` | 打印月支出、异常月份和预测值，适合控制台展示 |
| 其他方法 | 可供 UI 获取数据用于绘图或统计展示 |

---

## Module 6: 季节性消费检测  
**接口类名：`SeasonalSpendingDetector`**

```java
Map<Month, Double> detectSeasonalSpending(List<Transaction> transactions);
void printSeasonalSummary(List<Transaction> transactions);
```

| 方法 | 描述 |
|------|------|
| `detectSeasonalSpending` | 返回季节性高支出月份及金额（适合可视化） |
| `printSeasonalSummary` | 控制台输出详细月份分布和峰值警告 |

---

## ⚠ 错误处理建议

- 所有模块调用建议捕获标准 Java 异常（如 I/O、日期格式等）
- 后续版本可加入自定义异常 `AIException extends Exception` 作为统一抛出类型

---

## 📦 文件结构建议

```
├── data/
│   ├── sample_transactions.csv
│   ├── train.csv
│   └── feedback_log.csv
├── src/
│   ├── AITransactionClassifier.java
│   ├── BudgetRecommender.java
│   ├── SavingsOptimizer.java
│   ├── SpendingPatternAnalyzer.java
│   ├── SeasonalSpendingDetector.java
│   ├── ClassifierFeedbackManager.java
│   ├── Transaction.java
│   └── ...
```
