package mock;

import exception.AlException;
import service.PortfolioIntelligenceAlService;
import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Override
    public String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException {
        System.out.println("[Mock] Analyzing historical performance...");
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate analysis
        String analysis = "[Mock Analysis] Historical performance analysis:\n";
        if (portfolioHistory == null || portfolioHistory.isEmpty()) {
            return analysis + "No portfolio history data provided.";
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
