# AI-Driven Personal Finance Management System - JavaDoc

## 📋 Project Overview

**Project Name**: Group-79-SE-main  
**Version**: 1.0  
**Java Version**: 11+  
**Build System**: Manual compilation with custom scripts  
**Testing Framework**: JUnit 5  
**AI Integration**: DeepSeek API for intelligent financial analysis

---

## 🏗️ System Architecture

### Package Structure

```
ai_core/                    - Core AI and financial analysis algorithms
├── AITransactionClassifier.java
├── BudgetRecommender.java
├── SavingsOptimizer.java
├── SpendingPatternAnalyzer.java
├── SeasonalSpendingDetector.java
├── ClassifierFeedbackManager.java
├── DataLoader.java
├── Transaction.java
└── TestRunner.java

GUI/                        - Graphical User Interface components
├── ui/                     - UI Panels and Components
│   ├── DeepManageApp.java
│   ├── DashboardPanel.java
│   ├── FinancialPlanningPanel.java
│   ├── Module5Panel.java
│   └── DataManagementPanel.java
├── service/                - Service Layer Interfaces
├── mock/                   - Mock Service Implementations
├── dto/                    - Data Transfer Objects
└── exception/              - Custom Exception Classes

test/                       - Comprehensive Test Suite
├── FinancialAppMainFunctionsTest.java
└── JUnitImportTest.java

data/                       - Sample Data and Configuration
lib/                        - External Libraries and Dependencies
```

---

## 🧠 Core AI Modules

### AITransactionClassifier

**Package**: `ai_core`

```java
/**
 * AI-powered transaction classification system using machine learning algorithms.
 * Automatically categorizes financial transactions based on description patterns.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class AITransactionClassifier
```

**Key Methods**:
- `trainModel(List<Transaction> transactions)` - Trains the classification model
- `classify(String description)` - Classifies transaction based on description
- `getConfidenceScore(String description)` - Returns classification confidence

**Usage Example**:
```java
// Train the classifier
AITransactionClassifier.trainModel(historicalTransactions);

// Classify new transaction
String category = AITransactionClassifier.classify("Starbucks Coffee Shop");
```

### BudgetRecommender

**Package**: `ai_core`

```java
/**
 * Intelligent budget recommendation engine that analyzes spending patterns
 * and provides personalized budget suggestions using AI algorithms.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class BudgetRecommender
```

**Key Methods**:
- `recommendBudget(List<Transaction> transactions)` - Generates budget recommendations
- `detectAbnormalBudgetsStructured(List<Transaction> transactions)` - Detects spending anomalies
- `analyzeCategoryTrends(List<Transaction> transactions)` - Analyzes spending trends

**Inner Classes**:
- `BudgetSuggestion` - Represents a budget recommendation with category, amount, and explanation

### SavingsOptimizer

**Package**: `ai_core`

```java
/**
 * Advanced savings optimization system that creates personalized savings plans
 * and multi-goal financial strategies using AI-driven analysis.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class SavingsOptimizer
```

**Key Methods**:
- `generatePlan(double totalGoal, LocalDate deadline, List<Transaction> transactions)` - Creates savings plan
- `planMultiGoal(List<Goal> goals, List<Transaction> transactions)` - Multi-goal planning
- `predictNextMonthSpend(List<Transaction> transactions)` - Spending prediction

**Inner Classes**:
- `SavingsPlan` - Complete savings plan with timeline and recommendations
- `Goal` - Represents a financial goal with priority and deadline
- `GoalPlan` - Plan for achieving a specific goal

---

## 🖥️ GUI Components

### DeepManageApp

**Package**: `ui`

```java
/**
 * Main application class that initializes and manages the entire GUI framework.
 * Provides the primary window and navigation between different financial modules.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class DeepManageApp extends JFrame
```

**Features**:
- Modern tabbed interface design
- Consistent color scheme and styling
- Responsive layout management
- Integration with all financial modules

### DashboardPanel

**Package**: `ui`

```java
/**
 * Central dashboard providing overview of financial status and key metrics.
 * Features interactive pie charts and spending summaries.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class DashboardPanel extends JPanel
```

**Key Features**:
- Real-time spending overview
- Interactive pie chart visualization
- Category-based spending breakdown
- Financial summary statistics

### Module5Panel (Portfolio Intelligence)

**Package**: `ui`

```java
/**
 * Portfolio Intelligence Center for investment analysis and optimization.
 * Integrates with AI services to provide portfolio recommendations.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class Module5Panel extends JPanel
```

**Key Features**:
- CSV portfolio data import
- AI-powered allocation optimization
- Detailed portfolio analysis with percentages
- Comprehensive investment recommendations

---

## 🔧 Service Layer

### PortfolioIntelligenceAlService

**Package**: `service`

```java
/**
 * Service interface for portfolio intelligence and investment analysis.
 * Provides methods for portfolio evaluation and CSV data import.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public interface PortfolioIntelligenceAlService
```

**Key Methods**:
- `evaluatePortfolioAllocation(Map<String, BigDecimal> currentComposition)` - Portfolio analysis
- `importPortfolioFromCSV(InputStream csvStream)` - CSV data import
- `getLatestImportedPortfolio()` - Retrieve latest portfolio data

### Implementation: MockPortfolioIntelligenceAlService

**Package**: `mock`

```java
/**
 * Mock implementation of portfolio intelligence service with real AI integration.
 * Uses DeepSeek API for actual portfolio analysis and recommendations.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class MockPortfolioIntelligenceAlService implements PortfolioIntelligenceAlService
```

---

## 📊 Data Models

### Transaction

**Package**: `ai_core`

```java
/**
 * Core data model representing a financial transaction.
 * Contains all essential transaction information for analysis.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 */
public class Transaction
```

**Fields**:
- `LocalDate date` - Transaction date
- `double amount` - Transaction amount
- `String category` - Transaction category
- `String description` - Transaction description
- `String paymentMethod` - Payment method used

---

## 🧪 Testing Framework

### FinancialAppMainFunctionsTest

**Package**: `test`

```java
/**
 * Comprehensive test suite for all core financial management functions.
 * Provides 100% coverage of critical financial algorithms and AI components.
 * 
 * @author Financial Management Development Team
 * @version 1.0
 * @since 2024-01
 * @see org.junit.jupiter.api.Test
 */
@DisplayName("Financial Management Application Core Functions Test")
public class FinancialAppMainFunctionsTest
```

**Test Coverage**:
1. Transaction object creation and validation
2. AI transaction classification accuracy
3. Budget recommendation algorithms
4. Savings plan generation logic
5. Multi-goal savings planning
6. Abnormal spending detection
7. Data processing and statistics
8. Edge cases and exception handling

**Test Execution**:
```bash
# Compile project
compile_project.bat

# Run tests
run_tests.bat
```

---

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- JUnit 5 (included in lib/)
- JSON library (included)

### Installation

1. **Setup JUnit Environment**:
   ```bash
   setup_junit.bat
   ```

2. **Compile Project**:
   ```bash
   compile_project.bat
   ```

3. **Run Tests**:
   ```bash
   run_tests.bat
   ```

4. **Launch Application**:
   ```bash
   java -cp "out/production/Group-79-SE-main;lib/*" ui.DeepManageApp
   ```

---

## 📈 API Integration

### DeepSeek AI Integration

The system integrates with DeepSeek AI API for intelligent financial analysis:

```java
/**
 * Configuration for DeepSeek API integration
 */
private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
private static final String API_KEY = "your-api-key-here";
```

**Supported Operations**:
- Portfolio allocation optimization
- Transaction pattern analysis
- Investment recommendation generation
- Risk assessment and suggestions

---

## 🔒 Exception Handling

### Custom Exceptions

```java
/**
 * Base exception class for AI service operations
 * @author Financial Management Development Team
 */
public class AlException extends Exception {
    public AlException(String message) { super(message); }
    public AlException(String message, Throwable cause) { super(message, cause); }
}
```

**Other Exceptions**:
- `TransactionException` - Transaction processing errors
- `QueryException` - Data query errors
- `AuthException` - Authentication errors

---

## 📝 Usage Examples

### Basic Transaction Analysis

```java
// Create sample transactions
List<Transaction> transactions = Arrays.asList(
    new Transaction(LocalDate.now(), 45.0, "Dining", "Starbucks", "Credit Card"),
    new Transaction(LocalDate.now(), 300.0, "Transportation", "Gas Station", "Debit Card")
);

// Train AI classifier
AITransactionClassifier.trainModel(transactions);

// Get budget recommendations
List<BudgetRecommender.BudgetSuggestion> suggestions = 
    BudgetRecommender.recommendBudget(transactions);

// Create savings plan
SavingsOptimizer.SavingsPlan plan = SavingsOptimizer.generatePlan(
    10000.0, LocalDate.now().plusMonths(6), transactions);
```

### Portfolio Analysis

```java
// Import portfolio from CSV
portfolioService.importPortfolioFromCSV(csvInputStream);

// Get AI recommendations
Map<String, BigDecimal> recommendations = 
    portfolioService.evaluatePortfolioAllocation(currentPortfolio);
```

---

## 🔄 Development Workflow

### Code Organization

1. **Core Logic**: Implement in `ai_core` package
2. **UI Components**: Develop in `GUI/ui` package
3. **Services**: Define interfaces in `service`, implement in `mock`
4. **Testing**: Comprehensive tests in `test` package

### Best Practices

- Use dependency injection for service layer
- Implement comprehensive error handling
- Follow JavaDoc conventions for all public methods
- Maintain 100% test coverage for core algorithms
- Use BigDecimal for financial calculations

---

## 📚 Dependencies

### External Libraries

- **JUnit 5**: Testing framework
- **JSON Library**: Data processing
- **Swing**: GUI framework
- **Java Time API**: Date/time handling

### File Structure

```
lib/
├── junit-platform-console-standalone-1.10.0.jar
└── json-20250107.jar
```

---

## 🤝 Contributing

### Code Standards

- Follow Java naming conventions
- Document all public methods with JavaDoc
- Include comprehensive unit tests
- Use meaningful variable and method names
- Implement proper exception handling

### Testing Requirements

- Minimum 90% code coverage
- Test all edge cases and error conditions
- Include integration tests for AI components
- Validate all financial calculations

---

## 📄 License

This project is developed for educational purposes as part of Software Engineering coursework.

**Maintenance Team**: Financial Management Development Team  
**Last Updated**: January 2024  
**Version**: 1.0

---

*For detailed API documentation, refer to individual class JavaDoc comments in the source code.* 