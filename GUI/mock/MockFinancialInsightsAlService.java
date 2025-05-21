package mock;

import dto.TransactionData;
import exception.AlException;
import service.FinancialInsightsAlService;
import service.FinancialTransactionService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MockFinancialInsightsAlService implements FinancialInsightsAlService {
    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private final FinancialTransactionService financialTransactionService;

    public MockFinancialInsightsAlService(FinancialTransactionService financialTransactionService) {
        this.financialTransactionService = financialTransactionService;
    }

    private String callDeepSeekAPI(String userPromptContent) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setDoOutput(true);

            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are a helpful financial advisor providing concise budget advice."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userPromptContent.replace("\"", "\\\"").replace("\n", "\\n"));

            System.out.println("[DeepSeek API] Sending request for insights with prompt: " + userPromptContent);
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
                                System.out.print(contentChunk);
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }
            System.out.println();
            return fullResponse.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek API 调用失败 (Financial Insights): " + e.getMessage(), e);
        }
    }

    private String buildTransactionDataPromptSegment(List<TransactionData> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "No transaction data available.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Relevant transaction data highlights:\n");
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
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();
        String requestBody = buildTransactionDataPromptSegment(allTransactions);

        String apiResponse;
        try {
            apiResponse = callDeepSeekAPI(requestBody);
        } catch (Exception e) {
            throw new AlException("DeepSeek API调用失败: " + e.getMessage(), e);
        }

        List<String> advice = parseDeepSeekResponse(apiResponse);
        return String.join("\n", advice); // 🔧 修复点：转换为 String 返回
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
        return callDeepSeekAPI(prompt);
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
        return callDeepSeekAPI(prompt);
    }
}
