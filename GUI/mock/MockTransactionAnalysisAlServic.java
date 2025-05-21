package mock;

import dto.TransactionDetails;
import exception.AlException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import service.TransactionAnalysisAlService;

public class MockTransactionAnalysisAlServic implements TransactionAnalysisAlService {
    @Override
    public String categorizeTransaction(TransactionDetails details) throws AlException {
        System.out.println("[Mock] Categorizing transaction (internal use): " + details.getDescription());
        if (details == null || details.getDescription() == null) {
            throw new AlException("Mock Error: Transaction details or description is null.");
        }
        String desc = details.getDescription().toLowerCase();
        if (desc.contains("coffee") || desc.contains("restaurant")) return "Dining";
        if (desc.contains("gas") || desc.contains("uber") || desc.contains("metro")) return "Transportation";
        if (desc.contains("netflix") || desc.contains("spotify")) return "Entertainment";
        if (desc.contains("grocery") || desc.contains("walmart")) return "Groceries";
        return "Shopping"; // Default
    }

    @Override
    public List<String> analyzeSeasonalSpending(String userId) throws AlException {
        System.out.println("[DeepSeek] Analyzing seasonal spending for user: " + userId);

        // 1. 构造 prompt
        String userPrompt = String.format(
                "As a financial analyst, analyze the user's (ID: %s) seasonal spending patterns based on their transaction history. " +
                        "Summarize at least 3 key seasonal trends or anomalies, each on a new line, strictly in the format: " +
                        "'Alert: ...', 'Trend: ...', or 'Pattern: ...'. Do not include any introduction or conclusion.",
                userId
        );

        // 2. 调用 DeepSeek
        String apiResponse = callDeepSeekForSeasonal(userPrompt);

        // 3. 解析返回内容
        return parseSeasonalResponse(apiResponse);
    }

    // DeepSeek API调用（可根据healthaiservice的实现调整）
    private String callDeepSeekForSeasonal(String userPrompt) throws AlException {
        try {
            URL url = new URL("https://api.deepseek.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer sk-796b1e6471a54f8a9e5a0165c97fd764");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream");
            conn.setDoOutput(true);

            String escapedPrompt = userPrompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are a professional financial analyst."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, escapedPrompt);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }

            StringBuilder fullResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6).trim();
                        if (json.equals("[DONE]")) break;
                        int choicesIdx = json.indexOf("\"choices\":[");
                        if (choicesIdx == -1) continue;
                        int deltaIdx = json.indexOf("\"delta\":{", choicesIdx);
                        if (deltaIdx == -1) continue;
                        int contentKeyActualIdx = json.indexOf("\"content\":\"", deltaIdx);
                        if (contentKeyActualIdx == -1) continue;
                        if (contentKeyActualIdx > deltaIdx) {
                            int contentValueStart = contentKeyActualIdx + "\"content\":\"".length();
                            int contentValueEnd = json.indexOf("\"", contentValueStart);
                            if (contentValueEnd > contentValueStart) {
                                String rawJsonStringValue = json.substring(contentValueStart, contentValueEnd);
                                String contentChunk = rawJsonStringValue
                                        .replace("\\n", "\n")
                                        .replace("\\\"", "\"")
                                        .replace("\\\\", "\\");
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }
            return fullResponse.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek 流式调用失败: " + e.getMessage(), e);
        }
    }

    // 解析返回内容
    private List<String> parseSeasonalResponse(String response) {
        List<String> result = new java.util.ArrayList<>();
        if (response == null || response.isBlank()) return result;
        String[] lines = response.split("\\R");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Alert:") || line.startsWith("Trend:") || line.startsWith("Pattern:")) {
                result.add(line);
            }
        }
        return result;
    }
}
