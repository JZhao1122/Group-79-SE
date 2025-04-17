package service;

import exception.AlException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface FinancialHealthAlService {
    Map<String, BigDecimal> recommendBudget(String userId) throws AlException;
    Map<String, BigDecimal> allocateSavings(String userId, BigDecimal availableSavings) throws AlException;
    List<String> detectSpendingPatterns(String userId) throws AlException;
}
