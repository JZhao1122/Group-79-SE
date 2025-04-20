
# 🌟 Future Work on AI Core Module (DeepManage - Group 79)

This document outlines the current status and future development directions for the AI module (`ai_core`) of the DeepManage system, as designed and implemented by Group 79 for the Software Engineering course.

---

## 🧾 1. Transaction Classification (`AITransactionClassifier.java`)

### ✅ Current:
- Implements a Naive Bayes-style classifier using word-category frequency mapping.
- Accepts user feedback via `ClassifierFeedbackManager`, enabling retraining.

### 🚀 Future Upgrades:
- Introduce confidence scores for classification decisions.
- Implement semantic similarity using offline word embeddings (e.g., Word2Vec vectors in JSON).
- Add online learning: model auto-updates after each correction.

---

## 🧠 2. Budget Recommendation (`BudgetRecommender.java`)

### ✅ Current:
- Aggregates spending data by category and month.
- Uses linear regression to forecast next month’s budget.
- Generates human-readable suggestions and justifications.

### 🚀 Future Upgrades:
- Incorporate overall budget caps and income awareness.
- Factor in user-defined financial goals or limits.
- Include sliding-window seasonal trend adjustment (e.g., EWMA).

---

## 📊 3. Spending Pattern Analyzer (`SpendingPatternAnalyzer.java`)

### ✅ Current:
- Tracks monthly and per-category spending over time.
- Uses IQR-based anomaly detection for spike identification.

### 🚀 Future Upgrades:
- Visual anomaly highlighting (e.g., calendar heatmap or line plot).
- Cluster analysis on behavior patterns using KMeans or DBSCAN (Java).
- Link spikes to specific causes or events (e.g., holidays, large purchases).

---

## 💰 4. Savings Optimizer (`SavingsOptimizer.java`)

### ✅ Current:
- Computes a saving plan based on target amount and deadline.
- Suggests weekly/monthly contributions.
- Evaluates feasibility using past spending data.

### 🚀 Future Upgrades:
- Include disposable income estimation (income - expenses).
- Simulate multiple goal scenarios with prioritization.
- Provide actionable tips: “Reduce Entertainment by 200 CNY to save 1 month.”

---

## 📅 5. Seasonal Spending Detector (`SeasonalSpendingDetector.java`)

### ✅ Current:
- Aggregates monthly spending to identify peak months.
- Uses IQR + holiday-month awareness for seasonal detection.

### 🚀 Future Upgrades:
- Annotate seasonal peaks with holiday labels (e.g., Spring Festival).
- Add monthly comparison charts for user reports.
- Use moving average smoothing to better reflect seasonality.

---

## 🧠 General Recommendations

- Use DJL (Deep Java Library) or ONNX Runtime for local LLM/ML model loading in Java.
- Integrate lightweight learned models (e.g., TinyBERT, DistilBERT) through `.onnx` or `.json` interface.
- Save model state and cache frequently used data to reduce cold-start overhead.

---

_This future plan was drafted based on the iterative improvements made during the implementation of DeepManage AI core, authored by Yuzheng Zhang (AI Module Developer)._
