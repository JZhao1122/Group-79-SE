package mock;

import dto.TransactionData; // 假设 FinancialTransactionService 会用到
import exception.AlException;
import service.FinancialInsightsAlService;
import service.FinancialTransactionService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List; // 假设 FinancialTransactionService 会用到

public class MockFinancialInsightsAlService implements FinancialInsightsAlService {
    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764"; // 推荐放环境变量
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final FinancialTransactionService financialTransactionService; // 保留以便未来扩展或如果某些洞察需要交易数据

    public MockFinancialInsightsAlService(FinancialTransactionService financialTransactionService) {
        this.financialTransactionService = financialTransactionService;
    }

    // 提取的核心API调用方法
    private String callDeepSeekAPI(String userPromptContent) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream"); // 开启流式响应
            conn.setDoOutput(true);

            // 构造流式请求体
            // 注意：这里的 system_prompt 可以根据 FinancialInsightsAlService 的特性进行调整
            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are a helpful financial advisor providing concise budget advice."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userPromptContent.replace("\"", "\\\"").replace("\n", "\\n")); // 对用户输入内容进行转义

            System.out.println("[DeepSeek API] Sending request for insights with prompt: " + userPromptContent);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            // 流式读取响应内容
            StringBuilder fullResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6);
                        if (json.equals("[DONE]")) break;

                        // 提取 content 字段
                        // 这是一个简化的解析，实际中可能需要更健壮的JSON库
                        int idx = json.indexOf("\"content\":\"");
                        if (idx != -1) {
                            int start = idx + 11; // length of "\"content\":\""
                            int end = json.indexOf("\"", start);
                            if (end > start) {
                                String contentChunk = json.substring(start, end)
                                        .replace("\\n", "\n")
                                        .replace("\\\"", "\"");
                                System.out.print(contentChunk); // 实时输出到控制台（可选）
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }
            System.out.println(); // 换行，因为上面是 print
            return fullResponse.toString().trim(); // 返回拼接后的完整内容

        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek API 调用失败 (Financial Insights): " + e.getMessage(), e);
        }
    }

    // 辅助方法：用于构建基于交易数据的prompt（如果某些洞察需要）
    // 这个方法可能需要根据 FinancialInsightsAlService 的具体需求调整或移除
    private String buildTransactionDataPromptSegment(List<TransactionData> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "No transaction data available.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Relevant transaction data highlights:\n");
        // 示例：只取最新的几条或汇总信息，避免prompt过长
        int count = 0;
        for (int i = transactions.size() - 1; i >= 0 && count < 5; i--, count++) {
            TransactionData t = transactions.get(i);
            sb.append(String.format("- Date: %s, Category: %s, Amount: %.2f, Desc: %s\n",
                    t.getDate(), t.getCategory(), t.getAmount(), t.getDescription()));
        }
        if (transactions.size() > 5) {
            sb.append("... and more older transactions.\n");
        }
        return sb.toString();
    }


    @Override
    public String getSeasonalBudgetAdvice(String userId, String seasonIdentifier) throws AlException {
        System.out.println("[API Call] Getting seasonal advice for user: " + userId + ", season: " + seasonIdentifier);
        // 1. 构建针对季节性预算建议的Prompt
        // List<TransactionData> userTransactions = financialTransactionService.getAllTransactionsByUserId(userId); // 假设有此方法
        // String transactionContext = buildTransactionDataPromptSegment(userTransactions);

        String prompt = String.format(
            "Provide concise budget advice for a user for the '%s' season. " +
            "Focus on typical seasonal spending changes. Keep the advice to 1-2 sentences. " +
            // "User's recent transaction context: \n%s", // 如果需要交易数据上下文
            "For example: 'For %s, consider adjusting your 'Gifts' budget and 'Travel' expenses.'",
            seasonIdentifier, seasonIdentifier
        );

        // 2. 调用API
        String advice = callDeepSeekAPI(prompt);

        // 3. （可选）对返回结果进行简单处理或直接返回
        // 这里的解析比较简单，因为API应该直接返回建议文本
        return advice;
    }

    @Override
    public String getRegionalBudgetAdvice(String userId, String regionIdentifier) throws AlException {
        System.out.println("[API Call] Getting regional advice for user: " + userId + ", region: " + regionIdentifier);
        String prompt = String.format(
            "Give specific budget advice for a user living in a '%s' region. " +
            "Highlight 1-2 key budget categories typically affected by this region type. Provide a concrete suggestion if possible. " +
            "Keep the advice to 1-2 sentences. " +
            "For example: 'In %s areas, transportation costs might be higher; consider allocating X for it.'",
            regionIdentifier, regionIdentifier
        );
        String advice = callDeepSeekAPI(prompt);
        return advice;
    }

    @Override
    public String getPromotionBudgetAdvice(String userId, String promotionIdentifier) throws AlException {
        System.out.println("[API Call] Getting promotion advice for user: " + userId + ", promotion: " + promotionIdentifier);
        String prompt = String.format(
            "A user is considering a promotion event: '%s'. " +
            "Provide advice on how to budget for this promotion to avoid overspending, focusing on 1-2 relevant spending categories and suggesting a general strategy. " +
            "Keep the advice to 1-2 sentences. " +
            "For example: 'For the %s event, set a clear spending limit for electronics and apparel beforehand.'",
            promotionIdentifier, promotionIdentifier
        );
        String advice = callDeepSeekAPI(prompt);
        return advice;
    }
}