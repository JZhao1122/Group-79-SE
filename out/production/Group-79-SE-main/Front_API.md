# 接口文档

## 1. DTOs (Data Transfer Objects)

### TransactionData

* **文件:** `com/deepmanage/dto/TransactionData.java`
* **描述:** 用于传输交易数据。
* **属性:**
    * `amount` (BigDecimal): 交易金额 [cite: 654, 655, 656]
    * `date` (LocalDate): 交易日期 [cite: 654, 657, 658]
    * `description` (String): 交易描述 [cite: 654, 659, 660]
    * `paymentMethod` (String): 支付方式 [cite: 654, 661, 662]
* **构造函数:**
    * `TransactionData()`: 默认构造函数 [cite: 655]
* **Getters 和 Setters:**
    * `getAmount()`: 获取交易金额 [cite: 655, 656]
    * `setAmount(BigDecimal amount)`: 设置交易金额 [cite: 656]
    * `getDate()`: 获取交易日期 [cite: 657, 658]
    * `setDate(LocalDate date)`: 设置交易日期 [cite: 658]
    * `getDescription()`: 获取交易描述 [cite: 659, 660]
    * `setDescription(String description)`: 设置交易描述 [cite: 660]
    * `getPaymentMethod()`: 获取支付方式 [cite: 661, 662]
    * `setPaymentMethod(String paymentMethod)`: 设置支付方式 [cite: 662]
* `toString()`: 返回 `TransactionData` 对象的字符串表示形式 [cite: 663, 664]

### TransactionDetails

* **文件:** `com/deepmanage/dto/TransactionDetails.java`
* **描述:** 用于传输交易详情。
* **属性:**
    * `description` (String): 交易描述 (必填) [cite: 666, 667, 670, 671]
    * `merchant` (String): 商户名称 (可选) [cite: 667, 668, 672, 673]
    * `amount` (BigDecimal): 金额 (可选,可能影响分类) [cite: 668, 669, 674, 675]
    * `date` (LocalDate): 日期 (可选,可能影响分类) [cite: 669, 670, 676, 677]
* **Getters 和 Setters:**
    * `getDescription()`: 获取交易描述 [cite: 670, 671]
    * `setDescription(String description)`: 设置交易描述 [cite: 671, 672]
    * `getMerchant()`: 获取商户名称 [cite: 672, 673]
    * `setMerchant(String merchant)`: 设置商户名称 [cite: 673, 674]
    * `getAmount()`: 获取金额 [cite: 674, 675]
    * `setAmount(BigDecimal amount)`: 设置金额 [cite: 675, 676]
    * `getDate()`: 获取日期 [cite: 676, 677]
    * `setDate(LocalDate date)`: 设置日期 [cite: 677, 678]

### TransactionDisplayData

* **文件:** `com/deepmanage/dto/TransactionDisplayData.java`
* **描述:** 用于在UI上展示交易数据。
* **属性:**
    * `id` (String): 交易ID [cite: 679, 684]
    * `date` (LocalDate): 交易日期 [cite: 679, 680, 685]
    * `description` (String): 交易描述 [cite: 680, 686]
    * `amount` (BigDecimal): 交易金额 [cite: 680, 687]
    * `paymentMethod` (String): 支付方式 [cite: 680, 688]
    * `aiSuggestedCategory` (String): AI建议的分类 (来自 `TransactionAnalysisAlService`) [cite: 680, 681, 689]
    * `currentCategory` (String): 已确认或现有的分类 [cite: 681, 682, 690, 691]
* **构造函数:**
    * `TransactionDisplayData(String id, LocalDate date, String description, BigDecimal amount, String paymentMethod, String aiSuggestedCategory, String currentCategory)` [cite: 682, 683, 684]
* **Getters:**
    * `getId()`: 获取交易ID [cite: 684, 685]
    * `getDate()`: 获取交易日期 [cite: 685, 686]
    * `getDescription()`: 获取交易描述 [cite: 686, 687]
    * `getAmount()`: 获取交易金额 [cite: 687, 688]
    * `getPaymentMethod()`: 获取支付方式 [cite: 688, 689]
    * `getAiSuggestedCategory()`: 获取AI建议的分类 [cite: 689, 690]
    * `getCurrentCategory()`: 获取当前分类 [cite: 690, 691]
* **Setter:**
    * `setCurrentCategory(String currentCategory)`: 设置当前分类 [cite: 691, 692]

## 2. Exceptions

### TransactionException

* **文件:** `com/deepmanage/exception/TransactionException.java`
* **描述:** 处理交易相关的异常。
* **构造函数:**
    * `TransactionException(String message)` [cite: 693, 694]
    * `TransactionException(String message, Throwable cause)` [cite: 694, 695]

### AlException

* **文件:** `com/deepmanage/exception/AlException.java`
* **描述:** 处理AI相关的异常。
* **构造函数:**
    * `AlException(String message)` [cite: 696, 697]
    * `AlException(String message, Throwable cause)` [cite: 697, 698]

### QueryException

* **文件:** `com/deepmanage/exception/QueryException.java`
* **描述:** 处理查询相关的异常。
* **构造函数:**
    * `QueryException(String message)` [cite: 699, 700]
    * `QueryException(String message, Throwable cause)` [cite: 700, 701]

## 3. Service Interfaces

### FinancialTransactionService

* **文件:** `com/deepmanage/service/FinancialTransactionService.java`
* **描述:** 定义财务交易服务接口。
* **方法:**
    * `String addTransaction(TransactionData data) throws TransactionException`: 添加交易 [cite: 702, 703]
    * `int importTransactions(InputStream fileStream) throws TransactionException`: 导入交易 [cite: 703]

### FinancialHealthAlService

* **文件:** `com/deepmanage/service/FinancialHealthAlService.java`
* **描述:** 定义财务健康AI服务接口。
* **方法:**
    * `Map<String, BigDecimal> recommendBudget(String userId) throws AlException`: 推荐预算 [cite: 704]
    * `Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException`: 分配储蓄 [cite: 704, 705]
    * `List<String> detectSpendingPatterns(String userId) throws AlException`: 检测消费模式 [cite: 705, 706]

### TransactionAnalysisAlService

* **文件:** `com/deepmanage/service/TransactionAnalysisAlService.java`
* **描述:** 定义交易分析AI服务接口。
* **方法:**
    * `String categorizeTransaction(TransactionDetails details) throws AlException`:  对交易进行分类 (不直接被UI面板使用，可能被其他服务内部使用) [cite: 707, 708]
    * `List<String> analyzeSeasonalSpending(String userId) throws AlException`: 分析季节性消费 [cite: 708]

### TransactionQueryService

* **文件:** `com/deepmanage/service/TransactionQueryService.java`
* **描述:** 定义交易查询服务接口。
* **方法:**
    * `List<TransactionDisplayData> getTransactionsForReview(String userId) throws QueryException`: 获取用于审核的交易列表 [cite: 709, 710]
    * `void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException`: 更新交易分类 [cite: 710]

### FinancialInsightsAlService

* **文件:** `com/deepmanage/service/FinancialInsightsAlService.java`
* **描述:** 定义财务洞察AI服务接口。
* **方法:**
    * `String getSeasonalBudgetAdvice(String userId, String seasonIdentifier) throws AlException`: 获取季节性预算建议 [cite: 711, 712]
    * `String getRegionalBudgetAdvice(String userId, String regionIdentifier) throws AlException`: 获取区域性预算建议 [cite: 712]
    * `String getPromotionBudgetAdvice(String userId, String promotionIdentifier) throws AlException`: 获取促销预算建议 [cite: 712, 713]

### PortfolioIntelligenceAlService

* **文件:** `com/deepmanage/service/PortfolioIntelligenceAlService.java`
* **描述:** 定义投资组合智能AI服务接口。
* **方法:**
    * `String evaluatePortfolioAllocation(Map<String, BigDecimal> portfolioComposition) throws AlException`: 评估投资组合配置 [cite: 714, 715]
    * `String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException`: 分析历史表现 [cite: 715]

## 4. Mock Service Implementations

### MockFinancialTransactionService

* **文件:** `com/deepmanage/mock/MockFinancialTransactionService.java`
* **描述:** `FinancialTransactionService` 接口的模拟实现。
* **方法:**
    * `String addTransaction(TransactionData data) throws TransactionException`: 模拟添加交易 [cite: 719, 720, 721, 722]
    * `int importTransactions(InputStream fileStream) throws TransactionException`: 模拟导入交易 [cite: 722, 723, 724, 725, 726, 727, 728, 729]

### MockFinancialHealthAlService

* **文件:** `com/deepmanage/mock/MockFinancialHealthAlService.java`
* **描述:** `FinancialHealthAlService` 接口的模拟实现。
* **方法:**
    * `Map<String, BigDecimal> recommendBudget(String userId) throws AlException`: 模拟推荐预算 [cite: 731, 732, 733, 734]
    * `Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException`: 模拟分配储蓄 [cite: 734, 735, 736, 737, 738, 739]
    * `List<String> detectSpendingPatterns(String userId) throws AlException`: 模拟检测消费模式 [cite: 739, 740, 741, 742]

### MockTransactionAnalysisAlService

* **文件:** `com/deepmanage/mock/MockTransactionAnalysisAlService.java`
* **描述:** `TransactionAnalysisAlService` 接口的模拟实现。
* **方法:**
    * `String categorizeTransaction(TransactionDetails details) throws AlException`: 模拟对交易进行分类 [cite: 744, 745, 746, 747, 748]
    * `List<String> analyzeSeasonalSpending(String userId) throws AlException`: 模拟分析季节性消费 [cite: 748, 749, 750, 751]

### MockTransactionQueryService

* **文件:** `com/deepmanage/mock/MockTransactionQueryService.java`
* **描述:** `TransactionQueryService` 接口的模拟实现。
* **方法:**
    * `List<TransactionDisplayData> getTransactionsForReview(String userId) throws QueryException`: 模拟获取用于审核的交易列表 [cite: 759, 760, 761, 762]
    * `void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException`: 模拟更新交易分类 [cite: 762, 763, 764, 765, 766, 767, 768]

### MockFinancialInsightsAlService

* **文件:** `com/deepmanage/mock/MockFinancialInsightsAlService.java`
* **描述:** `FinancialInsightsAlService` 接口的模拟实现。
* **方法:**
    * `String getSeasonalBudgetAdvice(String userId, String seasonIdentifier) throws AlException`: 模拟获取季节性预算建议 [cite: 769, 770, 771]
    * `String getRegionalBudgetAdvice(String userId, String regionIdentifier) throws AlException`: 模拟获取区域性预算建议 [cite: 771, 772, 773, 774]
    * `String getPromotionBudgetAdvice(String userId, String promotionIdentifier) throws AlException`: 模拟获取促销预算建议 [cite: 774, 775, 776]

### MockPortfolioIntelligenceAlService

* **文件:** `com/deepmanage/mock/MockPortfolioIntelligenceAlService.java`
* **描述:** `PortfolioIntelligenceAlService` 接口的模拟实现。
* **方法:**
    * `String evaluatePortfolioAllocation(Map<String, BigDecimal> portfolioComposition) throws AlException`: 模拟评估投资组合配置 [cite: 778, 779, 780, 781, 782, 783, 784, 785]
    * `String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException`: 模拟分析历史表现 [cite: 785, 786, 787, 788, 789, 790, 791]

## 5. UI Panels

### Module1Panel

* **文件:** `com/deepmanage/ui/Module1Panel.java`
* **描述:** 用于处理交易的手动输入和文件导入。
* **属性:**
    * `financialTransactionService` (FinancialTransactionService):  用于处理财务交易的服务 [cite: 793, 794]
    * `amountField` (JTextField):  用于输入交易金额的文本框 [cite: 794, 795]
    * `dateField` (JTextField):  用于输入交易日期的文本框 [cite: 794, 795]
    * `descriptionField` (JTextField):  用于输入交易描述的文本框 [cite: 795, 796]
    * `paymentMethodCombo` (JComboBox):  用于选择支付方式的下拉框 [cite: 796, 800, 801]
    * `resultArea` (JTextArea):  用于显示结果的文本区域 [cite: 797, 809, 810]
* **构造函数:**
    * `Module1Panel(FinancialTransactionService service)` [cite: 797, 798]
* **方法:**
    * `initComponents()`:  初始化UI组件 [cite: 798, 799, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 812]
    * `attachListeners()`:  添加事件监听器 [cite: 798, 811, 812]
    * `saveManualTransaction(ActionEvent e)`:  保存手动输入的交易 [cite: 812, 813, 814, 815, 816, 817, 818, 819]
    * `importFile(ActionEvent e)`:  导入交易文件 [cite: 819, 820, 821, 822, 823, 824, 825, 826, 827, 828]

### Module2Panel

* **文件:** `com/deepmanage/ui/Module2Panel.java`
* **描述:** 用于显示财务健康AI的分析结果，包括预算推荐、储蓄分配和消费模式检测。
* **属性:**
    * `financialHealthAlService` (FinancialHealthAlService):  用于处理财务健康AI的服务 [cite: 830, 831, 832, 833]
    * `currentUserId` (String):  当前用户ID [cite: 831, 832, 833]
    * `resultArea` (JTextArea):  用于显示结果的文本区域 [cite: 831, 832, 835, 836]
* **构造函数:**
    * `Module2Panel(FinancialHealthAlService service, String userId)` [cite: 832, 833]
* **方法:**
    * `initComponents()`:  初始化UI组件 [cite: 833, 834, 835, 836, 837, 838]
    * `loadBudgetRecommendations()`:  加载预算推荐 [cite: 837, 838, 839, 840, 841, 842, 843, 844, 845]
    * `loadSavingsAllocation()`:  加载储蓄分配 [cite: 838, 845, 846, 847, 848, 849, 850, 851, 852, 853]
    * `loadSpendingPatterns()`:  加载消费模式 [cite: 838, 853, 854, 855, 856, 857, 858]

### Module3Panel

* **文件:** `com/deepmanage/ui/Module3Panel.java`
* **描述:** 用于显示和编辑交易数据，以及显示季节性消费分析。
* **属性:**
    * `transactionQueryService` (TransactionQueryService):  用于处理交易查询的服务 [cite: 529, 530, 532]
    * `transactionAnalysisAlService` (TransactionAnalysisAlService):  用于处理交易分析的服务 [cite: 530, 532]
    * `currentUserId` (String):  当前用户ID [cite: 530, 532]
    * `tableModel` (DefaultTableModel):  用于存储交易数据的表格模型 [cite: 531, 532, 533, 534, 535]
    * `reviewTable` (JTable):  用于显示和编辑交易数据的表格 [cite: 531, 534, 535, 537, 538, 539, 540, 550, 551]
    * `seasonalResultArea` (JTextArea):  用于显示季节性消费分析的文本区域 [cite: 531, 532, 541, 544, 545, 546, 547, 548]
* **构造函数:**
    * `Module3Panel(TransactionQueryService tqService, TransactionAnalysisAlService taService, String userId)` [cite: 532, 533, 534, 535]
* **方法:**
    * `initComponents()`:  初始化UI组件 [cite: 535, 536, 537, 538, 539, 540, 541, 542, 543]
    * `loadTransactionsForReview()`:  加载用于审核的交易 [cite: 543, 544, 545, 546, 547, 548, 549]
    * `saveCategoryCorrection()`:  保存分类更正 [cite: 543, 549, 550]