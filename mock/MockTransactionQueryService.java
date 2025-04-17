package mock;

import dto.TransactionDisplayData;
import exception.QueryException;
import exception.TransactionException;
import service.TransactionQueryService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Vector; // Using Vector for thread safety with Swing is debatable, but used in previous example
import java.util.stream.Collectors;

public class MockTransactionQueryService implements TransactionQueryService {
    // Use Vector for thread-safe operations if needed, or ArrayList if updates are strictly on EDT
    private final List<TransactionDisplayData> mockData;

    public MockTransactionQueryService() {
        // Create some mock data for the review table
        mockData = new Vector<>(List.of( // Use Vector or CopyOnWriteArrayList for concurrent access
                new TransactionDisplayData("T1", LocalDate.now().minusDays(5), "Starbucks Coffee", new BigDecimal("35.00"), "Alipay", "Dining", "Dining"),
                new TransactionDisplayData("T2", LocalDate.now().minusDays(4), "Shell Gas Station", new BigDecimal("200.00"), "Credit Card", "Transportation", "Transportation"),
                new TransactionDisplayData("T3", LocalDate.now().minusDays(3), "Netflix Subscription", new BigDecimal("50.00"), "Alipay", "Entertainment", "Entertainment"),
                new TransactionDisplayData("T4", LocalDate.now().minusDays(2), "Walmart Groceries", new BigDecimal("300.00"), "WeChat Pay", "Shopping", "Shopping"), // Example needing correction
                new TransactionDisplayData("T5", LocalDate.now().minusDays(1), "Uber Ride", new BigDecimal("80.00"), "Alipay", "Transportation", "Transportation"),
                new TransactionDisplayData("T6", LocalDate.now(), "Unknown Merchant Online", new BigDecimal("150.00"), "Credit Card", "Shopping", "Shopping") // Needs review
        ));
        // Simulate AI suggesting "Groceries" for Walmart, needing correction from "Shopping"
        // Find T4 and update AI suggestion (safer way)
        mockData.stream()
                .filter(tx -> "T4".equals(tx.getId()))
                .findFirst()
                .ifPresent(tx -> tx.aiSuggestedCategory = "Groceries"); // Direct modification ok for mock
    }

    @Override
    public List<TransactionDisplayData> getTransactionsForReview(String userId) throws QueryException {
        System.out.println("[Mock] Getting transactions for review for user: " + userId);
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate delay
        // Return a copy to prevent external modification if using ArrayList
        // return new ArrayList<>(mockData);
        // If using Vector, direct return is often done, but still risky if modified externally
        return new Vector<>(mockData); // Return copy of vector
    }

    @Override
    public void updateTransactionCategory(String transactionId, String correctedCategory) throws TransactionException {
        System.out.println("[Mock] Updating category for transaction ID: " + transactionId + " to: " + correctedCategory);
        boolean found = false;
        // Iterate carefully if using Vector or manage concurrency properly
        synchronized (mockData) { // Synchronize if using Vector and modifying
            for (TransactionDisplayData data : mockData) {
                if (data.getId().equals(transactionId)) {
                    data.setCurrentCategory(correctedCategory); // Update the mock data
                    System.out.println("[Mock] Internal data updated for " + transactionId);
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new TransactionException("Mock Error: Transaction ID " + transactionId + " not found.");
        }
        try { Thread.sleep(20); } catch (InterruptedException e) {} // Simulate delay/processing
        System.out.println("[Mock] Category update processed (AI learning simulation happens here).");
    }
}
