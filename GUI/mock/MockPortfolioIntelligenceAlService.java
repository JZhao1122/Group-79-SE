package mock;

import dto.TransactionData; // 确保 dto.TransactionData 类存在
import exception.AlException;
import service.FinancialTransactionService;
import service.PortfolioIntelligenceAlService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MockPortfolioIntelligenceAlService implements PortfolioIntelligenceAlService {
    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764"; // 推荐从环境变量读取
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final int MAX_TRANSACTIONS_IN_PROMPT = 20; // 限制放入prompt的交易数量

    private final FinancialTransactionService financialTransactionService;
    private final String currentUserId; // 用于存储当前服务实例对应的用户ID

    /**
     * 构造函数
     * @param financialTransactionService 用于获取用户交易数据的服务 (如果投资分析需要)
     * @param userId 此服务实例对应的用户ID/用户上下文标识
     * @throws IllegalArgumentException 如果userId为null或空, 或financialTransactionService为null (如果需要)
     */
    public MockPortfolioIntelligenceAlService(FinancialTransactionService financialTransactionService, String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID context cannot be null or empty for PortfolioIntelligenceAlService instance.");
        }
        // 如果 PortfolioIntelligenceAlService 明确需要 financialTransactionService，则取消注释下面的检查
        if (financialTransactionService == null) {
             throw new IllegalArgumentException("FinancialTransactionService cannot be null if portfolio intelligence requires transaction context.");
        }
        this.financialTransactionService = financialTransactionService;
        this.currentUserId = userId;
    }

    // 核心API调用方法 (与之前的版本相同)
    private String callDeepSeekAPI(String userPromptContent) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setDoOutput(true);

            // System prompt 调整为投资分析
            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are an expert investment analyst providing personalized portfolio insights based on user data and market context."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userPromptContent.replace("\"", "\\\"").replace("\n", "\\n"));

            System.out.println("[DeepSeek API] Sending request for portfolio intelligence for user context '" + this.currentUserId + "' with prompt snippet: " + userPromptContent.substring(0, Math.min(userPromptContent.length(), 100)) + "...");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            StringBuilder fullResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6);
                        if (json.equals("[DONE]")) break;

                        int idx = json.indexOf("\"content\":\"");
                        if (idx != -1) {
                            int start = idx + 11;
                            int end = json.indexOf("\"", start);
                            if (end > start) {
                                String contentChunk = json.substring(start, end)
                                        .replace("\\n", "\n")
                                        .replace("\\\"", "\"");
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }
            System.out.println("[DeepSeek API] Received response for user context '" + this.currentUserId + "'.");
            return fullResponse.toString().trim();

        } catch (Exception e) {
            System.err.println("DeepSeek API call failed for portfolio intelligence for user context '" + this.currentUserId + "': " + e.getMessage());
            e.printStackTrace();
            throw new AlException("DeepSeek API 调用失败 (Portfolio Intelligence for user context " + this.currentUserId + "): " + e.getMessage(), e);
        }
    }

    // 辅助方法：将投资组合构成转换为适合prompt的字符串 (保持不变)
    private String formatPortfolioCompositionForPrompt(Map<String, BigDecimal> portfolioComposition) {
        if (portfolioComposition == null || portfolioComposition.isEmpty()) {
            return "Portfolio composition data is missing or empty.";
        }
        return "Current portfolio composition (Asset: Percentage):\n" +
                portfolioComposition.entrySet().stream()
                        .map(entry -> String.format("- %s: %.2f%%", entry.getKey(), entry.getValue().multiply(new BigDecimal("100"))))
                        .collect(Collectors.joining("\n"));
    }

    // 辅助方法：将历史业绩数据转换为适合prompt的字符串 (保持不变)
    private String formatHistoricalPerformanceForPrompt(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) {
        StringBuilder sb = new StringBuilder();
        if (portfolioHistory != null && !portfolioHistory.isEmpty()) {
            sb.append("Portfolio Historical Values (Date: Value):\n");
            portfolioHistory.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> sb.append(String.format("- %s: %.2f\n", entry.getKey().toString(), entry.getValue())));
        } else {
            sb.append("No portfolio history data provided for the portfolio itself.\n");
        }

        if (benchmarkHistory != null && !benchmarkHistory.isEmpty()) {
            sb.append("\nBenchmark Historical Values:\n");
            benchmarkHistory.forEach((benchmarkName, history) -> {
                sb.append(String.format("Benchmark '%s' (Date: Value):\n", benchmarkName));
                if (history != null && !history.isEmpty()) {
                    history.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(entry -> sb.append(String.format("- %s: %.2f\n", entry.getKey().toString(), entry.getValue())));
                } else {
                     sb.append("No historical data provided for this benchmark.\n");
                }
            });
        } else {
            sb.append("No benchmark history data provided.\n");
        }
        return sb.toString();
    }

    // "粗暴"地将用户交易数据格式化为字符串，用于prompt
    // 假设 financialTransactionService.getAllTransactions() 返回的是当前这个唯一用户的交易
    private String formatUserTransactionsForPrompt() {
        List<TransactionData> transactions;
        try {
            // 因为只有一个用户上下文，直接获取所有交易即可
            // 如果 FinancialTransactionService 仍然需要一个 userId 参数，即使是形式上的，
            // 你也可以传入 this.currentUserId。
            // transactions = this.financialTransactionService.getTransactionsByUserId(this.currentUserId); // 理想情况
            transactions = this.financialTransactionService.getAllTransactions(); // 假设这返回当前用户的交易
            System.out.println("Fetching all transactions (assumed for user context '" + this.currentUserId + "') for portfolio intelligence context.");
        } catch (Exception e) {
            System.err.println("Error fetching transactions (assumed for user context '" + this.currentUserId + "'): " + e.getMessage());
            e.printStackTrace();
            return "Could not retrieve transaction history to provide as context due to an error.";
        }

        if (transactions == null || transactions.isEmpty()) {
            return "No transaction history available (for user context '" + this.currentUserId + "') to provide as context.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("User (Context ID: %s) Transaction History (most recent %d transactions, if available, for context):\n", this.currentUserId, MAX_TRANSACTIONS_IN_PROMPT));
        sb.append("Date | Amount | Category | Description\n");
        sb.append("---------------------------------------\n");

        transactions.stream()
            .sorted((t1, t2) -> {
                if (t1.getDate() == null && t2.getDate() == null) return 0;
                if (t1.getDate() == null) return 1;
                if (t2.getDate() == null) return -1;
                try {
                    LocalDate d1 = (t1.getDate() instanceof LocalDate) ? (LocalDate) t1.getDate() : LocalDate.parse(t1.getDate().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalDate d2 = (t2.getDate() instanceof LocalDate) ? (LocalDate) t2.getDate() : LocalDate.parse(t2.getDate().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
                    return d2.compareTo(d1);
                } catch (Exception e) {
                    System.err.println("Warning: Could not parse date for sorting transaction: " + t1.getDate() + " or " + t2.getDate());
                    return 0;
                }
            })
            .limit(MAX_TRANSACTIONS_IN_PROMPT)
            .forEach(t -> sb.append(String.format("%s | %s | %s | %s\n",
                    t.getDate() != null ? t.getDate().toString() : "N/A",
                    t.getAmount() != null ? t.getAmount().toString() : "N/A",
                    t.getCategory() != null ? t.getCategory() : "N/A",
                    t.getDescription() != null ? t.getDescription() : "N/A"
            )));
        
        if (transactions.size() > MAX_TRANSACTIONS_IN_PROMPT) {
            sb.append(String.format("... and %d more older transactions not listed.\n", transactions.size() - MAX_TRANSACTIONS_IN_PROMPT));
        }
        return sb.toString();
    }

    // 接口方法签名保持不变
    @Override
    public String evaluatePortfolioAllocation(Map<String, BigDecimal> portfolioComposition) throws AlException {
        System.out.println("[API Call] Evaluating portfolio allocation for user context '" + this.currentUserId + "': " + portfolioComposition);

        if (portfolioComposition == null || portfolioComposition.isEmpty()){
            return "[Evaluation] Portfolio composition data is missing or empty. Cannot evaluate for user context '" + this.currentUserId + "'.";
        }

        // 1. 获取并格式化用户交易数据作为上下文
        String userTransactionsContext = formatUserTransactionsForPrompt(); // 使用 this.currentUserId

        // 2. 将投资组合数据格式化为prompt的一部分
        String compositionDetails = formatPortfolioCompositionForPrompt(portfolioComposition);

        // 3. 构建Prompt，加入用户交易数据上下文
        String prompt = String.format(
            "Please evaluate the following investment portfolio allocation for a user (context ID: %s). " +
            "Consider the user's provided transaction history below for context on their financial behavior and risk tolerance. " +
            "Provide a concise analysis (2-3 key points) on its risk profile, potential for growth, and any notable concentrations or lack of diversification. " +
            "Do not give specific buy/sell recommendations, but rather an objective assessment.\n\n" +
            "User's Transaction History Context:\n%s\n\n" +
            "Portfolio Details:\n%s",
            this.currentUserId,
            userTransactionsContext,
            compositionDetails
        );

        // 4. 调用API
        return callDeepSeekAPI(prompt);
    }

    // 接口方法签名保持不变
    @Override
    public String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException {
        System.out.println("[API Call] Analyzing historical performance for user context '" + this.currentUserId + "'...");

        if (portfolioHistory == null || portfolioHistory.isEmpty()) {
            return "[Analysis] No portfolio history data provided. Cannot analyze performance for user context '" + this.currentUserId + "'.";
        }

        // 1. 获取并格式化用户交易数据作为上下文
        String userTransactionsContext = formatUserTransactionsForPrompt(); // 使用 this.currentUserId

        // 2. 将历史业绩数据格式化为prompt的一部分
        String performanceDetails = formatHistoricalPerformanceForPrompt(portfolioHistory, benchmarkHistory);

        // 3. 构建Prompt，加入用户交易数据上下文
        String prompt = String.format(
            "Analyze the historical performance of the given investment portfolio for a user (context ID: %s). " +
            "Consider the user's provided transaction history below for context on their financial behavior, which might influence risk tolerance or investment goals. " +
            "If benchmark data is provided, compare the portfolio's performance against these benchmarks. " +
            "Identify any significant trends, periods of outperformance or underperformance. " +
            "Provide a summary of 2-4 bullet points.\n\n" +
            "User's Transaction History Context:\n%s\n\n" +
            "Performance Data:\n%s",
            this.currentUserId,
            userTransactionsContext,
            performanceDetails
        );

        // 4. 调用API
        return callDeepSeekAPI(prompt);
    }
}