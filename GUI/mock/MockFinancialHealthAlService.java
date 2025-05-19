package mock;

import exception.AlException;
import service.FinancialHealthAlService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.math.RoundingMode;

public class MockFinancialHealthAlService implements FinancialHealthAlService {
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
        System.out.println("[Mock] Detecting spending patterns for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate delay
        return List.of(
                "Detected: Monthly subscription costs increased by 10% last month.",
                "Detected: Weekend dining expenses significantly higher than weekdays.",
                "Detected: Large one-off expense on Electronics category around the 15th."
        );
    }
}
