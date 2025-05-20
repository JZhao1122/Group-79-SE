package mock;

import dto.TransactionData;
import exception.AlException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        
        // Prompt for budget recommendation
        // No transaction data needed for a general recommendation as per current logic
        // String transactionDataJson = buildDeepSeekRequest(financialTransactionService.getAllTransactions()); // If needed
        
        String userPromptForBudget = String.format(
            "As a financial advisor, recommend a general monthly budget for a user (ID: %s). " +
            "Please list at least 5 common spending categories and their recommended amounts in USD. " +
            "Output each category and its amount on a new line, strictly in the format 'Category Name: AMOUNT'. " +
            "For example: 'Dining: 500.00'. " +
            "Ensure amounts are numbers only (digits and at most one decimal point). " +
            "Do not include any introductory or concluding sentences, just the list of categories and amounts.",
            userId
        );

        try {
            // callDeepSeekForBudget will internally call the streaming callDeepSeek
            String apiResponse = callDeepSeekForBudget(userPromptForBudget); 
            return parseBudgetResponse(apiResponse); // Use the new parsing logic
        } catch (AlException e) {
            System.err.println("Error recommending budget via DeepSeek: " + e.getMessage());
            throw e; // AlException from callDeepSeek or parseBudgetResponse
        } catch (Exception e) { // Catch any other unexpected exceptions
            System.err.println("Unexpected error recommending budget: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("Failed to recommend budget due to an unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException {
        System.out.println("[DeepSeek] Allocating savings for user: " + userId + ", available: " + availableSavings);
        if (availableSavings == null || availableSavings.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("[DeepSeek] No available savings to allocate for user: " + userId);
            return Map.of("Info", BigDecimal.ZERO); // Or an empty map, or throw an error
        }
        
        // Prompt for savings allocation
        String userPromptForSavings = String.format(
            "A user (ID: %s) has $%.2f available for savings. " +
            "Recommend a savings allocation plan across at least 3 categories (e.g., Emergency Fund, Short-term Goals, Long-term Investments). " +
            "The sum of allocated amounts should ideally equal the available savings. " +
            "Output each category and its allocated amount on a new line, strictly in the format 'Category Name: AMOUNT'. " +
            "For example: 'Emergency Fund: %.2f'. " +
            "Ensure amounts are numbers only. " +
            "Do not include any introductory or concluding sentences, just the list of categories and amounts.",
            userId, availableSavings, availableSavings.multiply(new BigDecimal("0.4")) // Example amount for prompt
        );

        try {
            // callDeepSeekForSavings will internally call the streaming callDeepSeek
            String apiResponse = callDeepSeekForSavings(userPromptForSavings);
            return parseSavingsResponse(apiResponse, availableSavings); // Pass availableSavings for potential validation
        } catch (AlException e) {
            System.err.println("Error allocating savings via DeepSeek: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error allocating savings: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("Failed to allocate savings due to an unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> detectSpendingPatterns(String userId) throws AlException {
        System.out.println("[DeepSeek] Detecting spending patterns for user: " + userId);
        // try { Thread.sleep(50); } catch (InterruptedException e) {} // User removed
        
        // 1. 获取所有交易数据
        List<TransactionData> allTransactions = financialTransactionService.getAllTransactions();

        // 2. 构造请求体（如JSON），这里只是示例 (This is the actual transaction data string for the prompt)
        String transactionDataJson = buildDeepSeekRequest(allTransactions);

        // 3. 调用DeepSeek API - callDeepSeek in user's code now takes the transaction data directly
        // The prompt formulation is inside user's new callDeepSeek method
        String apiResponse;
        try {
            // User's callDeepSeek now constructs the full prompt with transaction data.
            // The 'prompt' argument to callDeepSeek is effectively the 'escapedPrompt' (transactionDataJson)
            apiResponse = callDeepSeek(transactionDataJson); 
        } catch (Exception e) { // Catch AlException or other exceptions from callDeepSeek
             if (e instanceof AlException) throw (AlException)e;
            throw new AlException("DeepSeek API调用失败 (detectSpendingPatterns): " + e.getMessage(), e);
        }

        // 4. 解析API返回内容，提取季度花费报告
        List<String> report = parseDeepSeekResponse(apiResponse); 

        return report;
    }

    // 调用 DeepSeek API 的通用方法 (User Modified for Streaming)
    // Parameter 'promptInputForUserMessage' is the core data/query from the user for the "user" role message.
    // The method itself will wrap this with instructions for the AI.
    private String callDeepSeek(String promptInputForUserMessage) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream"); // 开启流式响应
            conn.setDoOutput(true);

            // Escape the input data to be safely embedded in the user prompt JSON
            String escapedDataForUserPrompt = promptInputForUserMessage
                    .replace("\\\\", "\\\\\\\\") // Must escape backslash first
                    .replace("\"", "\\\\\\\"")   // Escape double quotes
                    .replace("\n", "\\\\n")    // Escape newlines
                    .replace("\r", "\\\\r")    // Escape carriage returns
                    .replace("\t", "\\\\t");   // Escape tabs

            // System-level instruction and specific task prompt for spending patterns.
            // This part is specific to detectSpendingPatterns logic as per user's current callDeepSeek.
            // For budget/savings, the specific prompts are constructed in their respective methods and passed here.
            // To make this truly generic, the 'userMessageContent' should be passed fully formed.
            // OR, this method needs to know WHICH type of request it is.
            // Given user's current structure, callDeepSeek is tailored for detectSpendingPatterns' prompt.
            // I will adjust callDeepSeekForBudget and callDeepSeekForSavings to pass the *full* user message content.

            String userMessageContent;
            // This check is a temporary workaround. Ideally, callDeepSeek would take the full user message.
            if (promptInputForUserMessage.startsWith("As a financial advisor, recommend a general monthly budget") ||
                promptInputForUserMessage.startsWith("A user (ID: ")) { // Crude check for budget/savings prompts
                userMessageContent = escapedDataForUserPrompt; // The prompt is already fully formed and escaped
            } else { // Original spending patterns logic from user's code
                 userMessageContent = "Based on the following transaction data, generate a short 3-point financial insight summary. " +
                         "Use the following format exactly:\\n- Alert: [One specific risk or anomaly]\\n- Trend: [One meaningful recurring behavior or pattern]" +
                         "\\n- Pattern: [One deeper-level observation related to category or timing]\\n\\n" +
                         "You must only use insights supported by the data. Do not fabricate any data. Do not include anything outside of these 3 lines." +
                         "\\n\\nHere is the data:\\n" + escapedDataForUserPrompt;
            }
            
            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are a professional financial analyst."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userMessageContent); // Use the determined userMessageContent

            System.out.println("[DeepSeek] Sending stream request to API with user content: " + userMessageContent.substring(0, Math.min(100, userMessageContent.length())) + "...");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("utf-8"));
            }
            
            StringBuilder fullResponse = new StringBuilder();
            // Try-with-resources for BufferedReader
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String json = line.substring(6).trim(); 
                        if (json.equals("[DONE]")) break;

                        // Refined parsing for content within choices -> delta structure
                        int choicesIdx = json.indexOf("\"choices\":[");
                        if (choicesIdx == -1) continue; 

                        int deltaIdx = json.indexOf("\"delta\":{", choicesIdx);
                        if (deltaIdx == -1) continue; 

                        // Search for "content":" (key in JSON object)
                        int contentKeyActualIdx = json.indexOf("\"content\":\"", deltaIdx);
                        if (contentKeyActualIdx == -1) continue; 

                        // Ensure contentKeyActualIdx is actually after deltaIdx to be reasonably sure it's nested
                        if (contentKeyActualIdx > deltaIdx) {
                            // Calculate start of the actual content value (after "content":" part)
                            int contentValueStart = contentKeyActualIdx + "\"content\":\"".length(); 
                            
                            // Find the closing quote for the content value
                            int contentValueEnd = json.indexOf("\"", contentValueStart); 

                            if (contentValueEnd > contentValueStart) {
                                String rawJsonStringValue = json.substring(contentValueStart, contentValueEnd);
                                
                                // Unescape the JSON string value. 
                                // In a JSON string, newline is \n, quote is \", backslash is \\.
                                String contentChunk = rawJsonStringValue
                                        .replace("\\n", "\n")    // Replace literal \n with actual newline
                                        .replace("\\\"", "\"")   // Replace literal \" with actual quote
                                        .replace("\\\\", "\\");  // Replace literal \\ with actual backslash
                                
                                System.out.print(contentChunk); 
                                fullResponse.append(contentChunk); 
                            }
                        }
                    }
                }
            }
            System.out.println(); // Newline after streaming output
            System.out.println("[DeepSeek] Full raw streamed response: " + fullResponse.toString().substring(0, Math.min(200, fullResponse.length())) + "...");
            return fullResponse.toString(); 

        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek 流式调用失败: " + e.getMessage(), e);
        }
    }


    private String buildDeepSeekRequest(List<TransactionData> transactions) {
        // This now just builds the JSON string of transactions
        StringBuilder sb = new StringBuilder();
        sb.append("{\"transactions\":[");
        for (int i = 0; i < transactions.size(); i++) {
            TransactionData t = transactions.get(i);
            sb.append("{")
            .append("\"date\":\"").append(t.getDate()).append("\",")
            .append("\"amount\":").append(t.getAmount()).append(",")
            .append("\"category\":\"").append(t.getCategory()).append("\",")
            .append("\"description\":\"").append(t.getDescription()).append("\"")
            .append("}");
            if (i < transactions.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    private List<String> parseDeepSeekResponse(String response) {
        // This parses the 3-line spending pattern response.
        // The 'response' is the aggregated content from the streaming call.
        System.out.println("[DEBUG] parseDeepSeekResponse - Raw response: [" + response + "]"); 
        if (response == null || response.isBlank()) {
            System.err.println("Cannot parse spending patterns, API response is empty.");
            return new java.util.ArrayList<>(); 
        }
        
        List<String> result = new java.util.ArrayList<>(); 
        String[] lines = response.split("\\R"); // Split the converted string
        System.out.println("[DEBUG SPD] Number of lines after split: " + lines.length);
        for(int i=0; i < lines.length; i++) {
            System.out.println(String.format("[DEBUG SPD] Line %d: [%s]", i, lines[i]));
        }

        for (String line : lines) { 
            line = line.trim();
            if (line.startsWith("- Alert:")) {
                result.add("Detected:" + line.substring("- Alert:".length()).trim());
            } else if (line.startsWith("- Trend:")) {
                result.add("Detected:" + line.substring("- Trend:".length()).trim());
            } else if (line.startsWith("- Pattern:")) {
                result.add("Detected:" + line.substring("- Pattern:".length()).trim());
            }
        }
        if (result.isEmpty()) {
             System.err.println("Could not parse any spending patterns from the response: " + response);
        }
        return result;
    }
 
    private Map<String, BigDecimal> parseBudgetResponse(String apiTextResponse) throws AlException {
        System.out.println("[DEBUG] parseBudgetResponse - Raw apiTextResponse: [" + apiTextResponse + "]");

        Map<String, BigDecimal> budgetMap = new HashMap<>();
        if (apiTextResponse == null || apiTextResponse.isBlank()) {
            System.err.println("Cannot parse budget, API response is empty.");
            return budgetMap;
        }

        Pattern pattern = Pattern.compile("^(?:\\*\\*)?([^:]+?)(?:\\*\\*)?:\\s*(\\d+\\.?\\d+)(?:\\*\\*)?$");

        String[] lines = apiTextResponse.split("\\R"); // Split the converted string
        System.out.println("[DEBUG Budget] Number of lines after split: " + lines.length);
        for(int i=0; i < lines.length; i++) {
            System.out.println(String.format("[DEBUG Budget] Line %d: [%s]", i, lines[i]));
        }

        for (String line : lines) { 
            line = line.trim();
            if (line.isEmpty()) continue; 

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String category = matcher.group(1).trim();
                String amountStr = matcher.group(2).trim();
                try {
                    BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);
                    budgetMap.put(category, amount);
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse amount for budget category '" + category + "' from amount string '" + amountStr + "' on line: \"" + line + "\"");
                }
            } else {
                if (line.contains(":")) { 
                   System.err.println("Budget response line did not match expected format 'Category Name: AMOUNT': \"" + line + "\"");
                } else {
                   System.out.println("[DEBUG] Budget response line skipped (no colon, likely intro/outro): \"" + line + "\"");
                }
            }
        }
        if (budgetMap.isEmpty() && !apiTextResponse.isBlank()) { 
            boolean likelyContainedData = false;
            for(String l : lines) { 
                if(l.contains(":")) {
                    likelyContainedData = true;
                    break;
                }
            }
            if (likelyContainedData) {
                System.err.println("Failed to parse any budget items from the API response: " + apiTextResponse.substring(0, Math.min(apiTextResponse.length(), 200)) + "...");
            } else {
                System.out.println("[DEBUG] No budget items with ':' found to parse in API response: " + apiTextResponse.substring(0, Math.min(apiTextResponse.length(), 200)) + "...");
            }
        }
        System.out.println("[DeepSeek] Parsed budget map: " + budgetMap);
        return budgetMap;
    }
 
    private Map<String, BigDecimal> parseSavingsResponse(String apiTextResponse, BigDecimal availableSavings) throws AlException {
        System.out.println("[DEBUG] parseSavingsResponse - Raw apiTextResponse: [" + apiTextResponse + "]"); 
        Map<String, BigDecimal> savingsMap = new HashMap<>();
         if (apiTextResponse == null || apiTextResponse.isBlank()) {
            System.err.println("Cannot parse savings allocation, API response is empty.");
            return savingsMap;
        }

        Pattern pattern = Pattern.compile("^(?:\\*\\*)?([^:]+?)(?:\\*\\*)?:\\s*(\\d+\\.?\\d+)(?:\\*\\*)?$");
        String[] lines = apiTextResponse.split("\\R"); // Split the converted string
        System.out.println("[DEBUG Savings] Number of lines after split: " + lines.length);
        for(int i=0; i < lines.length; i++) {
            System.out.println(String.format("[DEBUG Savings] Line %d: [%s]", i, lines[i]));
        }

        BigDecimal totalAllocated = BigDecimal.ZERO;

        for (String line : lines) { 
            line = line.trim();
            if (line.isEmpty()) continue;

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String category = matcher.group(1).trim();
                String amountStr = matcher.group(2).trim();
                try {
                    BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);
                    savingsMap.put(category, amount);
                    totalAllocated = totalAllocated.add(amount);
                } catch (NumberFormatException e) {
                    System.err.println("Could not parse amount for savings category '" + category + "' from amount string '" + amountStr + "' on line: \"" + line + "\"");
                }
            } else {
                System.err.println("Savings response line did not match expected format 'Category Name: AMOUNT': \"" + line + "\"");
            }
        }

        // Optional: Validate if totalAllocated matches availableSavings
        if (totalAllocated.compareTo(availableSavings) != 0 && !savingsMap.isEmpty()) {
            System.err.println(String.format("[DeepSeek] Warning: Total allocated savings (%.2f) does not match available savings (%.2f).", totalAllocated, availableSavings));
        }
        
        if (savingsMap.isEmpty()) {
             System.err.println("Failed to parse any savings allocation items from the API response: " + apiTextResponse);
        }
        System.out.println("[DeepSeek] Parsed savings map: " + savingsMap);
        return savingsMap;
    }
 
    // These helper methods now simply pass the fully formed user prompt to the main callDeepSeek
    private String callDeepSeekForBudget(String userPromptForBudget) throws AlException {
        // The userPromptForBudget is already the full message content for the "user" role
        return callDeepSeek(userPromptForBudget);
    }
 
    private String callDeepSeekForSavings(String userPromptForSavings) throws AlException {
        // The userPromptForSavings is already the full message content for the "user" role
        return callDeepSeek(userPromptForSavings);
    }
}
