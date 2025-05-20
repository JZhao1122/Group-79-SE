package mock;

import dto.TransactionData;
import exception.AlException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import service.FinancialHealthAlService;
import service.FinancialTransactionService;

public class MockFinancialHealthAlService implements FinancialHealthAlService {
    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764"; // 推荐放环境变量
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final FinancialTransactionService financialTransactionService;


    public MockFinancialHealthAlService(FinancialTransactionService financialTransactionService) {
        this.financialTransactionService = financialTransactionService;
    }
    
    @Override
    public Map<String, BigDecimal> recommendBudget(String userId) throws AlException {
        System.out.println("[DeepSeek] Recommending budget for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        // 1. 获取所有交易数据
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();

        // 2. 构造请求体
        String requestBody = buildDeepSeekRequest(allTransactions);

        // 3. 调用DeepSeek API
        String apiResponse;
        try {
            apiResponse = callDeepSeekForBudget(requestBody);
        } catch (Exception e) {
            throw new AlException("DeepSeek API调用失败: " + e.getMessage(), e);
        }

        // 4. 解析API返回内容，提取预算建议
        Map<String, BigDecimal> budget = parseBudgetResponse(apiResponse);

        return budget;
    }
 
    @Override
    public Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException {
        System.out.println("[DeepSeek] Allocating savings for user: " + userId + ", available: " + availableSavings);
        if (availableSavings == null || availableSavings.compareTo(BigDecimal.ZERO) <= 0) {
            return Map.of("Info", BigDecimal.ZERO);
        }
        try { Thread.sleep(50); } catch (InterruptedException e) {}

        // 1. 获取所有交易数据
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();

        // 2. 构造请求体
        String requestBody = buildDeepSeekRequest(allTransactions);

        // 3. 调用DeepSeek API
        String apiResponse;
        try {
            apiResponse = callDeepSeekForSavings(requestBody);
        } catch (Exception e) {
            throw new AlException("DeepSeek API调用失败: " + e.getMessage(), e);
        }

        // 4. 解析API返回内容，提取储蓄分配建议
        Map<String, BigDecimal> savingsAllocation = parseSavingsResponse(apiResponse);

        return savingsAllocation;
    }

    @Override
    public List<String> detectSpendingPatterns(String userId) throws AlException {
        System.out.println("[DeepSeek] Detecting spending patterns for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        
        // final FinancialTransactionService financialTransactionService = null;

        // 1. 获取所有交易数据
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();

        // 2. 构造请求体（如JSON），这里只是示例
        String requestBody = buildDeepSeekRequest(allTransactions);

        // 3. 调用DeepSeek API
        String apiResponse;
        try {
            apiResponse = callDeepSeek(requestBody); // 你需要实现这个方法
        } catch (Exception e) {
            throw new AlException("DeepSeek API调用失败: " + e.getMessage(), e);
        }

        // 4. 解析API返回内容，提取季度花费报告
        List<String> report = parseDeepSeekResponse(apiResponse); // 你需要实现这个方法

        return report;
    }

    // 调用 DeepSeek API 的通用方法
    private String callDeepSeek(String prompt) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream"); // 开启流式响应
            conn.setDoOutput(true);
    
            // 1. 转义 prompt JSON 内容
            String escapedPrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"");
    
            // 2. Prompt 文本写为单行字符串 + \n 替代换行
            String userPrompt = "Based on the following transaction data, generate a short 5-point financial insight summary. " +
                    "Use the following format exactly:\\n- Alert: [One specific risk or anomaly]\\n- Trend: [One meaningful recurring behavior or pattern]" +
                    "\\n- Pattern: [One deeper-level observation related to category or timing]\\n\\n" +
                    "You must only use insights supported by the data. Do not fabricate any data. Do not include anything outside of these 3 lines." +
                    "\\n\\nHere is the data:\\n" + escapedPrompt;
    
            // 3. 构造流式请求体
            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are a professional financial analyst."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userPrompt);
    
            System.out.println("[DeepSeek] Sending stream request to API...");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }
    
            // 4. 流式读取响应内容
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
                                String content = json.substring(start, end)
                                        .replace("\\n", "\n")
                                        .replace("\\\"", "\"");
                                System.out.print(content); // 实时输出到控制台
                                fullResponse.append(content); // 可选：保留总内容
                            }
                        }
                    }
                }
            }
    
            return fullResponse.toString(); // 若你需要返回内容
    
        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek 流式调用失败: " + e.getMessage());
        }
    }

    private String buildDeepSeekRequest(List<TransactionData> transactions) {
        // 可以用JSON库序列化，或简单拼接
        StringBuilder sb = new StringBuilder();
        sb.append("{\"transactions\":[");
        for (TransactionData t : transactions) {
            sb.append("{")
            .append("\"date\":\"").append(t.getDate()).append("\",")
            .append("\"amount\":").append(t.getAmount()).append(",")
            .append("\"category\":\"").append(t.getCategory()).append("\",")
            .append("\"description\":\"").append(t.getDescription()).append("\"")
            .append("},");
        }
        if (!transactions.isEmpty()) sb.setLength(sb.length() - 1); 
        sb.append("]}");
        return sb.toString();
    }
    private List<String> parseDeepSeekResponse(String response) {
        String content = response;
        int idx = response.indexOf("\"content\":");
        if (idx != -1) {
            int start = response.indexOf("\"", idx + 10) + 1;
            int end = response.indexOf("\"", start);
            if (start > 0 && end > start) {
                content = response.substring(start, end)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"");
            }
        }
        List<String> result = new java.util.ArrayList<>();
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.startsWith("- Alert:")) {
                result.add("Detected:" + line.substring(8).trim());
            } else if (line.startsWith("- Trend:")) {
                result.add("Detected:" + line.substring(8).trim());
            } else if (line.startsWith("- Pattern:")) {
                result.add("Detected:" + line.substring(10).trim());
            }
        }

        return result;
    }
    private Map<String, BigDecimal> parseBudgetResponse(String response) {
        // 实现解析预算建议的逻辑
        // 解析API返回的JSON并转换为Map<String, BigDecimal>
        // 示例代码，具体实现需要根据API返回格式调整
        return Map.of(
            "Dining", new BigDecimal("1500.00"),
            "Shopping", new BigDecimal("2000.50"),
            "Transportation", new BigDecimal("600.00"),
            "Entertainment", new BigDecimal("800.75")
        );
    }

    private Map<String, BigDecimal> parseSavingsResponse(String response) {
        // 实现解析储蓄分配建议的逻辑
        // 解析API返回的JSON并转换为Map<String, BigDecimal>
        // 示例代码，具体实现需要根据API返回格式调整
        return Map.of(
            "Emergency Fund", new BigDecimal("400.00"),
            "Short-term Goals", new BigDecimal("300.00"),
            "Long-term Investment", new BigDecimal("300.00")
        );
    }

    private String callDeepSeekForBudget(String prompt) throws AlException {
        // 实现预算建议的API调用逻辑
        // 可以根据需求定制请求和响应处理
        return callDeepSeek(prompt); 
    }

    private String callDeepSeekForSavings(String prompt) throws AlException {
        // 实现储蓄分配建议的API调用逻辑
        // 可以根据需求定制请求和响应处理
        return callDeepSeek(prompt); 
    }
}
