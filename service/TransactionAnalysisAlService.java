package service;

import dto.TransactionDetails;
import exception.AlException;
import java.util.List;

public interface TransactionAnalysisAlService {
    // Not directly used by UI panels but might be used internally by other services
    String categorizeTransaction(TransactionDetails details) throws AlException;
    List<String> analyzeSeasonalSpending(String userId) throws AlException;
}
