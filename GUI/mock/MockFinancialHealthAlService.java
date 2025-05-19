package mock;

import dto.TransactionData;
import exception.AlException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        System.out.println("[Mock] Recommending budget for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate delay
        return Map.of(
                "Dining", new BigDecimal("1500.00"),
                "Shopping", new BigDecimal("2000.50"),
                "Transportation", new BigDecimal("600.00"),
                "Entertainment", new BigDecimal("800.75")
        );
    }
 
    @Override
    public Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException {
        System.out.println("[Mock] Allocating savings for user: " + userId + ", available: " + availableSavings);
        if (availableSavings == null || availableSavings.compareTo(BigDecimal.ZERO) <= 0) {
            return Map.of("Info", BigDecimal.ZERO); // Or throw exception?
        }
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate delay
        BigDecimal emergency = availableSavings.multiply(new BigDecimal("0.4"));
        BigDecimal shortTerm = availableSavings.multiply(new BigDecimal("0.3"));
        BigDecimal longTerm = availableSavings.subtract(emergency).subtract(shortTerm);
        return Map.of(
                "Emergency Fund", emergency.setScale(2, RoundingMode.HALF_UP),
                "Short-term Goals", shortTerm.setScale(2, RoundingMode.HALF_UP),
                "Long-term Investment", longTerm.setScale(2, RoundingMode.HALF_UP)
        );
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
            conn.setDoOutput(true);
            String requestBody = """
            {
            "model": "deepseek-chat",
            "messages": [
                {"role": "system", "content": "You are a helpful financial advisor."},
                {"role": "user", "content": "Based on the following transaction data, strictly output 3 lines in this format:\\n- Alert: ...\\n- Trend: ...\\n- Pattern: ...\\nDo not output anything else. Here is the data:\\n%s"}
            ]
            }
            """.formatted(prompt);
            System.out.println("[DeepSeek] Sending request to DeepSeek API: " + requestBody);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            InputStream inputStream = conn.getResponseCode() < 400 ? conn.getInputStream() : conn.getErrorStream();
            String response = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .reduce("", (acc, line) -> acc + line);
            System.out.println("[DeepSeek] Received response: " + response);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek 调用失败: " + e.getMessage());
        }
    }


    private String buildDeepSeekRequest(List<TransactionData> transactions) {
        // 可以用JSON库序列化，或简单拼接
        // 这里只是伪代码
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
        if (!transactions.isEmpty()) sb.setLength(sb.length() - 1); // 去掉最后逗号
        sb.append("]}");
        return sb.toString();
    }
    private List<String> parseDeepSeekResponse(String response) {
        // if (response == null || response.isBlank()) {
        //     return List.of(
        //         "Detected: Monthly subscription costs increased by 10% last month.",
        //         "Detected: Weekend dining expenses significantly higher than weekdays.",
        //         "Detected: Large one-off expense on Electronics category around the 15th."
        //     );
        // }
        // 尝试提取内容字段
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
        // // 如果没有检测到，返回默认三条
        // if (result.isEmpty()) {
        //     return List.of(
        //         "Detected: Monthly subscription costs increased by 10% last month.",
        //         "Detected: Weekend dining expenses significantly higher than weekdays.",
        //         "Detected: Large one-off expense on Electronics category around the 15th."
        //     );
        // }
        return result;
    }
}
