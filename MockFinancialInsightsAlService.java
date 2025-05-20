package mock;

import exception.AlException;
import service.FinancialInsightsAlService;

public class MockFinancialInsightsAlService implements FinancialInsightsAlService {
    @Override
    public String getSeasonalBudgetAdvice(String userId, String seasonIdentifier) throws AlException {
        System.out.println("[Mock] Getting seasonal advice for user: " + userId + ", season: " + seasonIdentifier);
        return "[Mock Advice] For " + seasonIdentifier + ", consider increasing your 'Gifts' budget by 15% and 'Travel' by 10%.";
    }

    @Override
    public String getRegionalBudgetAdvice(String userId, String regionIdentifier) throws AlException {
        System.out.println("[Mock] Getting regional advice for user: " + userId + ", region: " + regionIdentifier);
        if ("Urban".equalsIgnoreCase(regionIdentifier)) {
            return "[Mock Advice] In Urban areas, transportation costs are higher. Suggest allocating ¥800/month.";
        } else {
            return "[Mock Advice] For " + regionIdentifier + ", standard budget recommendations apply.";
        }
    }

    @Override
    public String getPromotionBudgetAdvice(String userId, String promotionIdentifier) throws AlException {
        System.out.println("[Mock] Getting promotion advice for user: " + userId + ", promotion: " + promotionIdentifier);
        return "[Mock Advice] For " + promotionIdentifier + ", set a spending limit of ¥2000 for electronics and ¥500 for apparel to avoid overspending.";
    }
}
