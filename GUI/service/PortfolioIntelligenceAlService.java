package service;

import exception.AlException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.io.InputStream;

public interface PortfolioIntelligenceAlService {
    Map<String, BigDecimal> evaluatePortfolioAllocation(Map<String, BigDecimal> currentComposition) throws AlException;
    String analyzeHistoricalPerformance(Map<LocalDate, BigDecimal> portfolioHistory, Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory) throws AlException;
    void importPortfolioFromCSV(InputStream csvStream) throws AlException;
    Map<String, BigDecimal> getLatestImportedPortfolio();
}
