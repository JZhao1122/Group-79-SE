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
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.math.RoundingMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MockPortfolioIntelligenceAlService implements PortfolioIntelligenceAlService {
    // API Constants (Consider a shared constants file or DI for a real app)
    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764"; // Replace with your actual key or env var
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    // Member to store the latest imported portfolio composition
    private Map<String, BigDecimal> latestImportedPortfolioComposition = new HashMap<>();

    private String callDeepSeekForPortfolioAnalysis(String userPromptContent) throws AlException {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "text/event-stream"); 
            conn.setDoOutput(true);

            // Escape the userPromptContent for JSON safety - crucial if it can contain quotes, newlines etc.
            // For this specific prompt, it might be simple enough, but good practice.
            String escapedPrompt = userPromptContent
                .replace("\\", "\\\\") // Escape backslashes first
                .replace("\"", "\\\"")   // Escape double quotes
                .replace("\n", "\\n")    // Escape newlines
                .replace("\r", "\\r")    // Escape carriage returns
                .replace("\t", "\\t");   // Escape tabs

            String requestBody = String.format("""
            {
                "model": "deepseek-chat",
                "stream": true,
                "messages": [
                    {"role": "system", "content": "You are an expert financial advisor specializing in portfolio allocation. You provide concise, actionable advice in the requested format only."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, escapedPrompt);

            System.out.println("[MockPortfolioIntelligenceAlService] Sending request to DeepSeek API for portfolio analysis...");
            // System.out.println("[MockPortfolioIntelligenceAlService] Request body (first 100 chars): " + requestBody.substring(0, Math.min(100, requestBody.length())));

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.getBytes("UTF-8"));
            }

            StringBuilder fullResponse = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"))) {
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
                                // System.out.print(contentChunk); // Live printing if desired
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }
            // System.out.println(); // Newline after streaming output
            System.out.println("[MockPortfolioIntelligenceAlService] Received full response from DeepSeek API (first 200 chars): " + fullResponse.toString().substring(0, Math.min(200, fullResponse.length())) + "...");
            return fullResponse.toString();

        } catch (IOException e) {
            System.err.println("[MockPortfolioIntelligenceAlService] IOException during DeepSeek API call: " + e.getMessage());
            throw new AlException("Failed to communicate with DeepSeek API for portfolio analysis: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("[MockPortfolioIntelligenceAlService] Unexpected error during DeepSeek API call: " + e.getMessage());
            e.printStackTrace();
            throw new AlException("An unexpected error occurred while calling DeepSeek API: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, BigDecimal> evaluatePortfolioAllocation(Map<String, BigDecimal> currentComposition) throws AlException {
        System.out.println("[MockPortfolioIntelligenceAlService] Evaluating portfolio allocation (RMB values) via DeepSeek API: " + currentComposition);
        if (currentComposition == null || currentComposition.isEmpty()){
            System.err.println("[MockPortfolioIntelligenceAlService] Current composition is empty or null. Cannot call API.");
            return new HashMap<>(); 
        }

        BigDecimal totalAssets = currentComposition.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        totalAssets = totalAssets.setScale(2, RoundingMode.HALF_UP);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(String.format("A user has the following portfolio composition with a total value of RMB %.2f:\n", totalAssets));
        currentComposition.forEach((asset, amount) -> 
            promptBuilder.append(String.format("- %s: RMB %.2f\n", asset, amount))
        );
        promptBuilder.append(String.format("Please provide a recommended OPTIMIZED portfolio allocation for these assets. " +
            "The total value of your recommended allocation MUST SUM to RMB %.2f. " +
            "Consider a balanced approach, potentially adjusting allocations based on common financial advice for diversification and risk management. " +
            "Output each recommended asset and its new RMB amount on a new line, strictly in the format 'AssetType: AMOUNT'. " +
            "For example: 'Stocks: 150000.00'. Do not include any introductory or concluding sentences, or any other text, just the list of assets and amounts.", totalAssets));
        
        String aiPrompt = promptBuilder.toString();
        // System.out.println("[MockPortfolioIntelligenceAlService] Generated AI Prompt for DeepSeek:\n" + aiPrompt); // Can be verbose

        // Call the actual DeepSeek API
        String actualAiResponse = callDeepSeekForPortfolioAnalysis(aiPrompt);
        
        System.out.println("[MockPortfolioIntelligenceAlService] Full AI Response from DeepSeek to parse:\n" + actualAiResponse);

        return parsePortfolioAllocationResponse(actualAiResponse);
    }

    private Map<String, BigDecimal> parsePortfolioAllocationResponse(String aiResponse) throws AlException {
        Map<String, BigDecimal> suggestedComposition = new HashMap<>();
        if (aiResponse == null || aiResponse.isBlank()) {
            System.out.println("[MockPortfolioIntelligenceAlService] AI response for parsing is empty.");
            return suggestedComposition; 
        }
        Pattern pattern = Pattern.compile("^(?:\\*\\*)?([^:]+?)(?:\\*\\*)?:\\s*([\\d]+\\.?[\\d]*)(?:\\*\\*)?$");
        String[] lines = aiResponse.split("\\R");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String assetType = matcher.group(1).trim();
                String amountStr = matcher.group(2).trim();
                try {
                    BigDecimal amount = new BigDecimal(amountStr).setScale(2, RoundingMode.HALF_UP);
                    suggestedComposition.put(assetType, amount);
                } catch (NumberFormatException e) {
                    System.err.println("[MockPortfolioIntelligenceAlService] Could not parse amount for asset '" + assetType + "' from amount string '" + amountStr + "' in AI response line: \"" + line + "\"");
                }
            } else {
                System.err.println("[MockPortfolioIntelligenceAlService] AI response line did not match expected format 'AssetType: AMOUNT': \"" + line + "\"");
            }
        }
        System.out.println("[MockPortfolioIntelligenceAlService] Parsed AI-suggested composition: " + suggestedComposition);
        return suggestedComposition;
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

    // 辅助方法：将投资组合构成转换为适合prompt的字符串 
    private String formatPortfolioCompositionForPrompt(Map<String, BigDecimal> portfolioComposition) {
        if (portfolioComposition == null || portfolioComposition.isEmpty()) {
            return "Portfolio composition data is missing or empty.";
        }
        return "Current portfolio composition (Asset: Percentage):\n" +
                portfolioComposition.entrySet().stream()
                        .map(entry -> String.format("- %s: %.2f%%", entry.getKey(), entry.getValue().multiply(new BigDecimal("100"))))
                        .collect(Collectors.joining("\n"));
    }

    // 辅助方法：将历史业绩数据转换为适合prompt的字符串 
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
            transactions = this.financialTransactionService.getAllTransactions(); 
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
        String userTransactionsContext = formatUserTransactionsForPrompt(); 

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
        analysis += "- Overall return seems stable, potentially benefiting from diversification.\n";
        analysis += "- Periods of volatility correlate with benchmark fluctuations (e.g., Nasdaq).\n";
        analysis += "- Performance relative to Shanghai 300 needs further review for specific periods.";
        return analysis;
    }

    @Override
    public void importPortfolioFromCSV(InputStream csvStream) throws AlException {
        System.out.println("[MockPortfolioIntelligenceAlService] Attempting to import portfolio from CSV stream (3-column format: Account,AssetType,Amount)...");
        this.latestImportedPortfolioComposition.clear(); 
        if (csvStream == null) {
            System.err.println("[MockPortfolioIntelligenceAlService] CSV stream is null. Import cannot proceed.");
            throw new AlException("CSV input stream was null.");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream, "UTF-8"))) {
            String line;
            List<String> headers = null;
            int lineCount = 0;
            int accountNumberIndex = -1;
            int assetTypeIndex = -1;
            int amountIndex = -1;
            List<String> expectedHeaders = Arrays.asList("Account", "AssetType", "Amount");
            while ((line = reader.readLine()) != null) {
                lineCount++;
                String[] parts = line.split(","); 
                if (lineCount == 1) { 
                    headers = new ArrayList<>(Arrays.asList(parts));
                    System.out.println("[MockPortfolioIntelligenceAlService] CSV Headers: " + headers);
                    if (headers.size() != 3) {
                        System.err.println("[MockPortfolioIntelligenceAlService] CSV headers do not match expected 3-column format. Found: " + headers.size() + " columns.");
                        throw new AlException("CSV header format error: Expected 3 columns (Account, AssetType, Amount).");
                    }
                    accountNumberIndex = headers.indexOf("Account");
                    assetTypeIndex = headers.indexOf("AssetType");
                    amountIndex = headers.indexOf("Amount");
                    if (accountNumberIndex == -1 || assetTypeIndex == -1 || amountIndex == -1) {
                        System.err.println("[MockPortfolioIntelligenceAlService] CSV headers missing one or more required columns. Expected: 'Account', 'AssetType', 'Amount'. Found: " + headers);
                        throw new AlException("CSV header format error: Missing required columns (Account, AssetType, Amount).");
                    }
                } else { 
                    if (headers == null) {
                        System.err.println("[MockPortfolioIntelligenceAlService] CSV data found before header row. Aborting.");
                        throw new AlException("CSV format error: Data before header.");
                    }
                    if (parts.length != headers.size()) { 
                        System.err.println("[MockPortfolioIntelligenceAlService] Line " + lineCount + ": Number of columns (" + parts.length + ") does not match header count (3). Skipping line: " + line);
                        continue; 
                    }
                    String accountNumber = parts[accountNumberIndex].trim();
                    String assetType = parts[assetTypeIndex].trim();
                    String amountStr = parts[amountIndex].trim();
                    System.out.println("  [CSV Data Row " + (lineCount -1) + "]: Account=" + accountNumber + ", Type=" + assetType + ", AmountStr=" + amountStr);
                    if (assetType.isEmpty()) {
                        System.err.println("[MockPortfolioIntelligenceAlService] Line " + lineCount + ": Asset type is empty. Skipping entry.");
                        continue;
                    }
                    try {
                        BigDecimal amount = new BigDecimal(amountStr);
                        this.latestImportedPortfolioComposition.put(assetType, 
                            this.latestImportedPortfolioComposition.getOrDefault(assetType, BigDecimal.ZERO).add(amount));
                    } catch (NumberFormatException e) {
                        System.err.println("[MockPortfolioIntelligenceAlService] Line " + lineCount + ", Asset '" + assetType + "': Invalid number format for value '" + amountStr + "'. Skipping value.");
                    }
                }
            }
            if (lineCount == 0) {
                System.out.println("[MockPortfolioIntelligenceAlService] CSV stream was empty.");
            } else if (lineCount == 1 && headers != null) {
                System.out.println("[MockPortfolioIntelligenceAlService] CSV contained only a header row.");
            } else {
                System.out.println("[MockPortfolioIntelligenceAlService] Successfully processed " + lineCount + " lines from CSV.");
                System.out.println("[MockPortfolioIntelligenceAlService] Final Imported Portfolio Composition (RMB):");
                if (this.latestImportedPortfolioComposition.isEmpty() && lineCount > 1) {
                    System.out.println("  No valid data rows found to aggregate or all asset types were empty.");
                }
                this.latestImportedPortfolioComposition.forEach((asset, amount) -> 
                    System.out.println("  - " + asset + ": " + amount.setScale(2, RoundingMode.HALF_UP))
                );
            }
        } catch (IOException e) {
            System.err.println("[MockPortfolioIntelligenceAlService] IOException while reading CSV stream: " + e.getMessage());
            throw new AlException("Failed to read data from CSV stream: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("[MockPortfolioIntelligenceAlService] Unexpected error during CSV import: " + e.getMessage());
            if (e instanceof AlException) throw (AlException)e;
            throw new AlException("An unexpected error occurred during CSV import: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, BigDecimal> getLatestImportedPortfolio() {
        System.out.println("[MockPortfolioIntelligenceAlService] getLatestImportedPortfolio called. Returning: " + this.latestImportedPortfolioComposition);
        return this.latestImportedPortfolioComposition;
    }
}
