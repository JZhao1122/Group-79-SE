# 💰 DeepManage - Group 79 Project for Software Engineering

**DeepManage** is an intelligent personal finance management system designed and developed by Group 79 for the _Software Engineering_ course. The system helps users record, classify, analyze, and optimize their financial behaviors via a modular GUI and a series of AI-powered services.

---

## 🧠 Core Features

| Module            | Functionality                                                |
| ----------------- | ------------------------------------------------------------ |
| 🧾 **Module 1**    | Manual & File-based Transaction Entry (WeChat, Alipay, Bank CSV Parsing) |
| 📊 **Module 2**    | Budget Recommendation Based on Spending History & Goals      |
| 💹 **Module 3**    | Savings Optimization using AI algorithms                     |
| 📈 **Module 4**    | Spending Pattern Analysis and Financial Health Insights      |
| 🕒 **Module 5**    | Seasonal Spending Detection for Planning                     |
| 🧠 **AI Core**     | Learning-based Transaction Classification, Feedback Loop for Customization |
| 📤 **Backend API** | Secure, scalable APIs supporting UI operations and data persistence |

---

## 🧱 Tech Stack

- **Language:** Java (Swing GUI + Backend logic)
- **Architecture:** Modular MVC-like structure
- **Libraries:** Standard Java SDK, Custom AI Classifiers
- **Design Tools:** Figma / PDF Prototypes

---

## 🚀 How to Run

> ⚠️ Java 17+ is recommended

```bash
# Compile all Java files (from project root)
mkdir bin
find . -name "*.java" > sources.txt
# Or this statement in windows system: Get-ChildItem -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName > sources.txt
javac -d bin @sources.txt -Xlint:deprecation 
# Or this statement in windows system: javac -d bin (Get-Content sources.txt) -Xlint:deprecation
rm sources.txt

# run project
java -cp bin ui.DeepManageApp
```

No external dependencies are needed; all models are implemented from scratch and run locally.

---

## 👥 Team Members & Responsibilities

| Name                             | Role                 | Contributions                                                |
| -------------------------------- | -------------------- | ------------------------------------------------------------ |
| **Yixian Tian**                  | Data Handling        | Manual entry, CSV import (WeChat/Alipay/Bank), data validation & privacy |
| **Yuzheng Zhang**                | AI Algorithms        | Classification, budget/savings recommendations, seasonal analysis, investment strategy |
| **Yifeng Wang** & **Xingyu Li**  | Frontend & UI Design | GUI layout, color schemes, charts, interactive forms         |
| **Jian Zhao** & **Boyuan Zhang** | Backend & Systems    | API development, permission control, system architecture     |

---

## 📂 Project Structure (Partial)

```
Group-79-SE-main/
├── GUI/                    # Graphical user interface
│   ├── ui/                 # Main application window and panels (Module1Panel, etc.)
│   ├── service/            # Core business logic
│   ├── mock/               # Mock services for testing
│   ├── dto/                # Data Transfer Objects
│   └── exception/          # Custom exception classes
├── ai_core/                # AI-driven financial intelligence modules
│   ├── AITransactionClassifier.java
│   ├── BudgetRecommender.java
│   ├── SavingsOptimizer.java
│   ├── SpendingPatternAnalyzer.java
│   ├── SeasonalSpendingDetector.java
│   └── ClassifierFeedbackManager.java
├── Prototype_group79.pdf   # UI design prototype
├── Productbacklog_group79.xlsx # Product backlog (user stories & features)
└── README.md               # Project description
```

---

## 📸 Snapshots

> See `Prototype_group79.pdf` for interface mockups and layout planning.

---

## 💡 Highlights

- ✨ End-to-end AI integration with no third-party ML frameworks
- 🔁 Adaptive transaction classifier with feedback tuning
- 🧩 Modular design: easily extendable for future fintech features
- 📊 Visual-first UX with user-friendly budget insights

---

## 📚 References & Acknowledgments

- Inspired by real-world budgeting tools and research on personal finance AI.
- Built with passion and teamwork under the guidance of the Software Engineering curriculum.

---

_DeepManage: Because smart money management starts with smart software._ 💸