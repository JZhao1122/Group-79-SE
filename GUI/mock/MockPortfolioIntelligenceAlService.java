package mock;

import exception.AlException;
import service.PortfolioIntelligenceAlService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class MockPortfolioIntelligenceAlService implements PortfolioIntelligenceAlService {
    @Override
    public String evaluatePortfolioAllocation(Map<String, BigDecimal> portfolioComposition) throws AlException {
        System.out.println("[Mock] Evaluating portfolio allocation: " + portfolioComposition);
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate analysis
        String analysis = "[Mock Evaluation] Based on the composition: ";
        if (portfolioComposition == null || portfolioComposition.isEmpty()){
            return analysis + "Portfolio data missing.";
        }
        if (portfolioComposition.getOrDefault("Cash", BigDecimal.ZERO).compareTo(new BigDecimal("0.3")) > 0) {
            analysis += "The current allocation appears conservative with a high cash proportion. ";
        } else {
            analysis += "The cash level seems reasonable. ";
        }
        if (portfolioComposition.getOrDefault("Stocks", BigDecimal.ZERO).compareTo(new BigDecimal("0.6")) > 0) {
            analysis += "High exposure to stocks suggests a growth-oriented but potentially higher-risk strategy.";
        }
        return analysis;
    }

    @Override
    public String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException {
        System.out.println("[Mock] Analyzing historical performance...");
        try { Thread.sleep(50); } catch (InterruptedException e) {} // Simulate analysis
        String analysis = "[Mock Analysis] Historical performance analysis:\n";
        if (portfolioHistory == null || portfolioHistory.isEmpty()) {
            return analysis + "No portfolio history data provided.";
        }
        // Dummy analysis points
        analysis += "- Overall return seems stable, potentially benefiting from diversification.\n";
        analysis += "- Periods of volatility correlate with benchmark fluctuations (e.g., Nasdaq).\n";
        analysis += "- Performance relative to Shanghai 300 needs further review for specific periods.";
        return analysis;
    }
}
