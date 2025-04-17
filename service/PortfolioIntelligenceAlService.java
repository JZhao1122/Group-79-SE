package service;

import exception.AlException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface PortfolioIntelligenceAlService {
    String evaluatePortfolioAllocation(Map<String, BigDecimal> portfolioComposition) throws AlException;
    String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException;
}
