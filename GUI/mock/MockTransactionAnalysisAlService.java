package mock;

import dto.TransactionData;
import dto.TransactionDetails;
import exception.AlException;
import service.FinancialTransactionService;
import service.TransactionAnalysisAlService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MockTransactionAnalysisAlService implements TransactionAnalysisAlService {

    private static final String API_KEY = "sk-796b1e6471a54f8a9e5a0165c97fd764";
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    private FinancialTransactionService financialTransactionService;

    public MockTransactionAnalysisAlService() {
        // 无参构造，后续通过 setFinancialTransactionService 注入依赖
    }

    public void setFinancialTransactionService(FinancialTransactionService service) {
        this.financialTransactionService = service;
    }

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
        if (desc.contains("apple store") || desc.contains("app store") || desc.contains("itunes")) return "Digital Services";
        if (desc.contains("amazon") || desc.contains("aliexpress") || desc.contains("shopify")) return "Online Shopping";
        if (desc.contains("ikea") || desc.contains("furniture")) return "Home Furnishing";
        if (desc.contains("power") || desc.contains("electric") || desc.contains("utility")) return "Utilities";
        if (desc.contains("water bill") || desc.contains("sewer")) return "Utilities";
        if (desc.contains("insurance") || desc.contains("premium")) return "Insurance";
        if (desc.contains("gym") || desc.contains("fitness") || desc.contains("yoga")) return "Health & Fitness";
        if (desc.contains("hospital") || desc.contains("clinic") || desc.contains("pharmacy")) return "Healthcare";
        if (desc.contains("tuition") || desc.contains("university") || desc.contains("school")) return "Education";
        if (desc.contains("bookstore") || desc.contains("kindle") || desc.contains("textbook")) return "Education";
        if (desc.contains("movie") || desc.contains("cinema") || desc.contains("amc")) return "Entertainment";
        if (desc.contains("hotel") || desc.contains("airbnb") || desc.contains("motel")) return "Travel";
        if (desc.contains("flight") || desc.contains("airline") || desc.contains("airport")) return "Travel";
        if (desc.contains("train") || desc.contains("railway") || desc.contains("ticket")) return "Transportation";
        if (desc.contains("taxi") || desc.contains("lyft")) return "Transportation";
        if (desc.contains("delivery") || desc.contains("doordash") || desc.contains("grubhub")) return "Dining";
        if (desc.contains("target") || desc.contains("costco")) return "Groceries";
        if (desc.contains("clothing") || desc.contains("apparel") || desc.contains("fashion")) return "Clothing";
        if (desc.contains("zara") || desc.contains("uniqlo") || desc.contains("hm")) return "Clothing";
        if (desc.contains("baby") || desc.contains("diaper")) return "Family & Children";
        if (desc.contains("pet") || desc.contains("vet") || desc.contains("petco")) return "Pet Care";
        if (desc.contains("donation") || desc.contains("charity")) return "Charity";
        if (desc.contains("bank fee") || desc.contains("overdraft") || desc.contains("atm fee")) return "Banking Fees";
        if (desc.contains("atm withdrawal") || desc.contains("cash withdrawal")) return "Cash Withdrawal";
        if (desc.contains("income") || desc.contains("salary") || desc.contains("payroll")) return "Income";
        if (desc.contains("investment") || desc.contains("stock") || desc.contains("robinhood")) return "Investments";
        if (desc.contains("dividend") || desc.contains("interest")) return "Investment Income";
        if (desc.contains("mortgage") || desc.contains("loan payment")) return "Loan Payment";
        if (desc.contains("rent") || desc.contains("lease")) return "Housing";
        if (desc.contains("maintenance") || desc.contains("repair")) return "Home Maintenance";
        if (desc.contains("cleaning") || desc.contains("maid service")) return "Home Services";
        if (desc.contains("security") || desc.contains("alarm")) return "Home Security";
        if (desc.contains("garden") || desc.contains("landscaping")) return "Home & Garden";
        if (desc.contains("hardware") || desc.contains("tools") || desc.contains("lowes")) return "DIY & Tools";
        if (desc.contains("makeup") || desc.contains("cosmetic") || desc.contains("sephora")) return "Personal Care";
        if (desc.contains("salon") || desc.contains("spa") || desc.contains("barber")) return "Personal Care";
        if (desc.contains("mobile") || desc.contains("cellular") || desc.contains("verizon")) return "Mobile Services";
        if (desc.contains("internet") || desc.contains("broadband") || desc.contains("wifi")) return "Internet Services";
        if (desc.contains("tv") || desc.contains("cable") || desc.contains("hulu")) return "TV & Streaming";
        if (desc.contains("parking") || desc.contains("toll")) return "Transportation";
        if (desc.contains("lottery") || desc.contains("gambling")) return "Gambling";
        if (desc.contains("gift") || desc.contains("present")) return "Gifts";
        if (desc.contains("wedding") || desc.contains("event")) return "Events";
        if (desc.contains("photography") || desc.contains("photos")) return "Photography";
        if (desc.contains("consulting") || desc.contains("freelance")) return "Professional Services";
        if (desc.contains("legal") || desc.contains("lawyer")) return "Legal Services";
        if (desc.contains("tax") || desc.contains("irs") || desc.contains("gov")) return "Taxes";
        if (desc.contains("subscription") || desc.contains("membership")) return "Subscriptions";
        if (desc.contains("software") || desc.contains("license")) return "Software";
        if (desc.contains("hosting") || desc.contains("domain")) return "Web Services";
        if (desc.contains("printing") || desc.contains("stationery")) return "Office Supplies";
        if (desc.contains("conference") || desc.contains("webinar")) return "Professional Development";
        if (desc.contains("coach") || desc.contains("training")) return "Self Improvement";
        if (desc.contains("hobby") || desc.contains("craft")) return "Hobbies";
        if (desc.contains("music") || desc.contains("instrument")) return "Music";
        if (desc.contains("bike") || desc.contains("cycle")) return "Transportation";
        if (desc.contains("electronics") || desc.contains("gadgets") || desc.contains("bestbuy")) return "Electronics";
        if (desc.contains("repair") || desc.contains("fix")) return "Maintenance & Repairs";
        if (desc.contains("glasses") || desc.contains("optical") || desc.contains("lens")) return "Vision Care";
        if (desc.contains("dentist") || desc.contains("dental")) return "Dental Care";
        if (desc.contains("vitamin") || desc.contains("supplement")) return "Health Products";
        if (desc.contains("laundry") || desc.contains("dry clean")) return "Laundry";
        if (desc.contains("recycle") || desc.contains("trash")) return "Waste Management";
        if (desc.contains("storage") || desc.contains("locker")) return "Storage Services";
        if (desc.contains("shipping") || desc.contains("courier")) return "Logistics";
        if (desc.contains("crafts") || desc.contains("etsy")) return "Handmade & Crafts";
        if (desc.contains("museum") || desc.contains("gallery")) return "Cultural Activities";
        if (desc.contains("park") || desc.contains("zoo")) return "Outdoor & Recreation";
        if (desc.contains("sports") || desc.contains("stadium")) return "Sports & Games";
        if (desc.contains("lotto") || desc.contains("casino")) return "Gambling";
        if (desc.contains("crypto") || desc.contains("bitcoin")) return "Crypto";
        if (desc.contains("vpn") || desc.contains("proxy")) return "Internet Security";
        if (desc.contains("anti virus") || desc.contains("norton") || desc.contains("mcafee")) return "Internet Security";
        if (desc.contains("language") || desc.contains("duolingo")) return "Language Learning";
        if (desc.contains("kids") || desc.contains("toy")) return "Children";
        if (desc.contains("decor") || desc.contains("home accents")) return "Home Decor";
        if (desc.contains("flowers") || desc.contains("bouquet")) return "Gifts";
        if (desc.contains("tools") || desc.contains("hardware")) return "Hardware & Tools";
        if (desc.contains("contractor") || desc.contains("plumber") || desc.contains("electrician")) return "Home Improvement";
        if (desc.contains("donuts") || desc.contains("dessert")) return "Dining";
        if (desc.contains("sandwich") || desc.contains("burger")) return "Dining";
        if (desc.contains("beer") || desc.contains("wine") || desc.contains("alcohol")) return "Alcohol & Bars";
        if (desc.contains("bar") || desc.contains("pub")) return "Alcohol & Bars";
        if (desc.contains("cigarette") || desc.contains("tobacco")) return "Tobacco";
        if (desc.contains("massage") || desc.contains("therapy")) return "Wellness";
        if (desc.contains("consult") || desc.contains("advisor")) return "Consulting";
        if (desc.contains("rental") || desc.contains("lease fee")) return "Rentals";
        if (desc.contains("moving") || desc.contains("relocation")) return "Relocation";
        if (desc.contains("coworking") || desc.contains("wework")) return "Workspaces";
        if (desc.contains("printer") || desc.contains("ink")) return "Office Equipment";
        if (desc.contains("driving school") || desc.contains("lessons")) return "Driving Lessons";
        if (desc.contains("passport") || desc.contains("visa")) return "Travel Documents";
        if (desc.contains("eventbrite") || desc.contains("concert")) return "Events";
        return "Shopping"; // Default fallback
    }

    @Override
    public List<String> analyzeSeasonalSpending(String userId) throws AlException {
        if (financialTransactionService == null) {
            throw new AlException("financialTransactionService is not initialized.");
        }

        System.out.println("[API Call] Analyzing seasonal spending for user: " + userId);

        List<TransactionData> transactions = financialTransactionService.getAllTransactions();
        String prompt = buildTransactionDataPromptSegment(transactions);
        String apiResponse = callDeepSeekAPI(prompt);

        List<String> result = new ArrayList<>();
        for (String line : apiResponse.split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                result.add(line);
            }
        }
        return result;
    }

    private String buildTransactionDataPromptSegment(List<TransactionData> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return "No transaction data is available for seasonal spending analysis.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Analyze the following transaction history to identify seasonal spending insights. Return 3 points:\n")
          .append("- Alert: ...\n- Trend: ...\n- Pattern: ...\n\n");

        int count = 0;
        for (int i = transactions.size() - 1; i >= 0 && count < 10; i--, count++) {
            TransactionData t = transactions.get(i);
            sb.append(String.format("- Date: %s, Category: %s, Amount: %.2f, Desc: %s\n",
                    t.getDate(), t.getCategory(), t.getAmount(), t.getDescription()));
        }

        if (transactions.size() > 10) {
            sb.append("...and more transactions omitted.\n");
        }

        return sb.toString();
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
                    {"role": "system", "content": "You are a helpful financial assistant."},
                    {"role": "user", "content": "%s"}
                ]
            }
            """, userPromptContent.replace("\"", "\\\"").replace("\n", "\\n"));

            System.out.println("[DeepSeek API] Sending seasonal analysis request...");
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
                                System.out.print(contentChunk); // 实时打印
                                fullResponse.append(contentChunk);
                            }
                        }
                    }
                }
            }

            return fullResponse.toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            throw new AlException("DeepSeek API 调用失败: " + e.getMessage(), e);
        }
    }
}
