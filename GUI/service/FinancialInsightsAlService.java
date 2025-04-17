package service;

import exception.AlException;

public interface FinancialInsightsAlService {
    String getSeasonalBudgetAdvice(String userId, String seasonIdentifier) throws AlException;
    String getRegionalBudgetAdvice(String userId, String regionIdentifier) throws AlException;
    String getPromotionBudgetAdvice(String userId, String promotionIdentifier) throws AlException;
}
