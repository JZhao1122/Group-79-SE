package mock;

import dto.TransactionData;
import dto.TransactionDetails;
import exception.AlException;
import exception.TransactionException;
import service.FinancialTransactionService;
import service.TransactionAnalysisAlService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MockTransactionAnalysisAlServic implements TransactionAnalysisAlService {
    
    // 添加对FinancialTransactionService的引用
    private FinancialTransactionService transactionService;
    
    // 默认构造函数
    public MockTransactionAnalysisAlServic() {
        this.transactionService = null;
    }
    
    // 带参数构造函数
    public MockTransactionAnalysisAlServic(FinancialTransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    /**
     * Set the transaction service
     * This method allows for setting the transaction service after construction
     * to resolve circular dependency issues
     * 
     * @param transactionService The transaction service to set
     */
    public void setTransactionService(FinancialTransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @Override
    public String categorizeTransaction(TransactionDetails details) throws AlException {
        System.out.println("[Mock] Categorizing transaction (internal use): " + details.getDescription());
        if (details == null || details.getDescription() == null) {
            throw new AlException("Mock Error: Transaction details or description is null.");
        }
        String desc = details.getDescription().toLowerCase();
// 1. Dining
        if (desc.contains("coffee") || desc.contains("restaurant") || desc.contains("kfc") ||
                desc.contains("donuts") || desc.contains("dessert") || desc.contains("sandwich") || desc.contains("burger") ||
                desc.contains("delivery") || desc.contains("grubhub") || desc.contains("doordash"))
            return "Dining";

// 2. Transportation
        if (desc.contains("uber") || desc.contains("metro") || desc.contains("train") || desc.contains("railway") ||
                desc.contains("ticket") || desc.contains("taxi") || desc.contains("lyft") || desc.contains("parking") ||
                desc.contains("toll") || desc.contains("bike") || desc.contains("cycle"))
            return "Transportation";

// 3. Entertainment & Subscriptions
        if (desc.contains("netflix") || desc.contains("spotify") || desc.contains("movie") || desc.contains("cinema") ||
                desc.contains("amc") || desc.contains("concert") || desc.contains("eventbrite") || desc.contains("subscription") ||
                desc.contains("membership") || desc.contains("hulu"))
            return "Entertainment & Subscriptions";

// 4. Shopping
        if (desc.contains("amazon") || desc.contains("aliexpress") || desc.contains("shopify") ||
                desc.contains("zara") || desc.contains("uniqlo") || desc.contains("hm") ||
                desc.contains("clothing") || desc.contains("fashion") || desc.contains("gift") || desc.contains("present"))
            return "Shopping";

// 5. Home & Furnishing
        if (desc.contains("ikea") || desc.contains("furniture") || desc.contains("decor") || desc.contains("home accents") ||
                desc.contains("garden") || desc.contains("landscaping") || desc.contains("hardware") || desc.contains("tools") ||
                desc.contains("lowes"))
            return "Home & Furnishing";

// 6. Healthcare
        if (desc.contains("hospital") || desc.contains("clinic") || desc.contains("pharmacy") ||
                desc.contains("dentist") || desc.contains("dental") || desc.contains("vision") || desc.contains("optical") ||
                desc.contains("glasses") || desc.contains("vitamin") || desc.contains("supplement"))
            return "Healthcare";

// 7. Wellness & Fitness
        if (desc.contains("gym") || desc.contains("fitness") || desc.contains("yoga") ||
                desc.contains("spa") || desc.contains("salon") || desc.contains("barber") ||
                desc.contains("massage") || desc.contains("therapy"))
            return "Wellness & Fitness";

// 8. Groceries
        if (desc.contains("walmart") || desc.contains("grocery") || desc.contains("target") || desc.contains("costco"))
            return "Groceries";

// 9. Education
        if (desc.contains("school") || desc.contains("university") || desc.contains("tuition") ||
                desc.contains("bookstore") || desc.contains("kindle") || desc.contains("language") || desc.contains("duolingo"))
            return "Education";

// 10. Digital & Internet Services
        if (desc.contains("internet") || desc.contains("broadband") || desc.contains("vpn") || desc.contains("proxy") ||
                desc.contains("itunes") || desc.contains("apple store") || desc.contains("app store") || desc.contains("license") ||
                desc.contains("software") || desc.contains("domain") || desc.contains("hosting"))
            return "Digital & Internet Services";

// 11. Banking & Cash
        if (desc.contains("bank fee") || desc.contains("atm") || desc.contains("overdraft") || desc.contains("cash withdrawal"))
            return "Banking & Cash";

// 12. Investments & Savings
        if (desc.contains("investment") || desc.contains("stock") || desc.contains("dividend") ||
                desc.contains("interest") || desc.contains("crypto") || desc.contains("bitcoin"))
            return "Investments & Savings";

// 13. Housing
        if (desc.contains("rent") || desc.contains("lease") || desc.contains("mortgage") || desc.contains("housing"))
            return "Housing";

// 14. Family & Pets
        if (desc.contains("pet") || desc.contains("vet") || desc.contains("baby") || desc.contains("kids") || desc.contains("toy"))
            return "Family & Pets";

// 15. Taxes
        if (desc.contains("tax") || desc.contains("irs") || desc.contains("gov"))
            return "Taxes";

// 16. Insurance
        if (desc.contains("insurance") || desc.contains("premium"))
            return "Insurance";

// 17. Travel
        if (desc.contains("flight") || desc.contains("airline") || desc.contains("airport") ||
                desc.contains("hotel") || desc.contains("airbnb") || desc.contains("passport") || desc.contains("visa"))
            return "Travel";

// 18. Utilities
        if (desc.contains("power") || desc.contains("electric") || desc.contains("utility") || desc.contains("sewer") ||
                desc.contains("mobile") || desc.contains("cellular") || desc.contains("verizon"))
            return "Utilities";

// 19. Professional Services
        if (desc.contains("consulting") || desc.contains("freelance") || desc.contains("lawyer") ||
                desc.contains("legal") || desc.contains("advisor"))
            return "Professional Services";

// 20. Other
        return "Other";

    }

    @Override
    public List<String> analyzeSeasonalSpending(String userId) throws AlException {
        System.out.println("[DeepSeek] Analyzing seasonal spending for user: " + userId);
        
        // 获取用户的实际交易数据
        List<TransactionData> transactions = null;
        if (transactionService != null) {
            try {
                transactions = transactionService.getAllTransactions(userId);
                if (transactions != null && !transactions.isEmpty()) {
                    System.out.println("[DeepSeek] Retrieved " + transactions.size() + " transactions for analysis");
                } else {
                    System.out.println("[DeepSeek] No transactions found for user: " + userId);
                    throw new AlException("No transaction data available for analysis. Please import transaction data first.");
                }
            } catch (TransactionException e) {
                throw new AlException("Failed to get transaction data: " + e.getMessage(), e);
            }
        } else {
            System.out.println("[DeepSeek] Transaction service not available, using general prompt");
            throw new AlException("No transaction data available for analysis. Please import transaction data first.");
        }
        
        // 将交易数据转换为格式化的字符串，以包含在API请求中
        String transactionDataText = formatTransactionsForAnalysis(transactions);
        
        // 1. 构造 prompt
        String userPrompt = String.format(
                "As a financial analyst, analyze the user's (ID: %s) seasonal spending patterns based on the following transaction data:\n\n" +
                "%s\n\n" +
                "Summarize at least 3 key seasonal trends or anomalies, each on a new line, strictly in the format: " +
                "'Alert: ...', 'Trend: ...', or 'Pattern: ...'. Do not include any introduction or conclusion.",
                userId, transactionDataText
        );

        // 2. 调用 DeepSeek
        String apiResponse = callDeepSeekForSeasonal(userPrompt);

        // 3. 解析返回内容
        return parseSeasonalResponse(apiResponse);
    }
    
    // 将交易数据格式化为文本
    private String formatTransactionsForAnalysis(List<TransactionData> transactions) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        sb.append("Date, Amount, Category, Description\n");
        for (TransactionData transaction : transactions) {
            sb.append(transaction.getDate().format(formatter)).append(", ");
            sb.append("¥").append(transaction.getAmount()).append(", ");
            sb.append(transaction.getCategory() != null ? transaction.getCategory() : "Uncategorized").append(", ");
            sb.append(transaction.getDescription()).append("\n");
        }
        
        return sb.toString();
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
            throw new AlException("DeepSeek API call failed: " + e.getMessage(), e);
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
