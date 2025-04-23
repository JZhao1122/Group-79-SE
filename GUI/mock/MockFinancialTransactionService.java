package mock;

import dto.TransactionData;
import exception.TransactionException;
import java.io.IOException;
import java.io.InputStream;
import service.FinancialTransactionService;

public class MockFinancialTransactionService implements FinancialTransactionService {
    private long transactionCounter = 0;

    @Override
    public String addTransaction(TransactionData data) throws TransactionException {
        System.out.println("[Mock] Adding transaction: " + data);
        if (data.getAmount() == null || data.getDate() == null || data.getDescription() == null || data.getDescription().isEmpty()) {
            throw new TransactionException("Mock Validation Error: Missing required fields.");
        }
        transactionCounter++;
        return "TXN" + transactionCounter;
    }

    @Override
    public int importTransactions(InputStream fileStream) throws TransactionException {
        System.out.println("[Mock] Importing transactions from stream...");
        try {
            // Simulate reading the stream to check if it's valid/empty
            if (fileStream.read() == -1) {
                System.out.println("[Mock] Warning: Input stream seems empty.");
            }
            System.out.println("[Mock] File stream seems valid.");
            // In real implementation: parse CSV/Excel, handle formats, validate, save.
            // Consume the rest of the stream to simulate processing
            while(fileStream.read() != -1);
            fileStream.close(); // Important to close stream
        } catch (IOException e) {
            throw new TransactionException("Mock IO Error during import.", e);
        }
        // Return a fixed number of imported transactions for demo
        return 5;
    }
}
