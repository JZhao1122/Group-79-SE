package mock;

import dto.TransactionDetails;
import exception.AlException;
import service.TransactionAnalysisAlService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
        System.out.println("[Mock] Analyzing seasonal spending for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate delay
        return List.of(
                "Alert: Higher expenses typically detected during Chinese New Year (Jan/Feb).",
                "Trend: Increased shopping activity observed around Double 11 (November).",
                "Pattern: Travel-related costs tend to peak in July/August."
        );
    }
}
