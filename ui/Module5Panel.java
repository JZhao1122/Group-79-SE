package ui;

import exception.AlException;
import service.PortfolioIntelligenceAlService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class Module5Panel extends JPanel {

    private final PortfolioIntelligenceAlService portfolioIntelligenceAlService;
    private final JTextArea resultArea = new JTextArea(15, 60);

    public Module5Panel(PortfolioIntelligenceAlService service) {
        this.portfolioIntelligenceAlService = service;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton evalButton = new JButton("Evaluate Portfolio Allocation");
        JButton historyButton = new JButton("Analyze Historical Performance");
        buttonPanel.add(evalButton);
        buttonPanel.add(historyButton);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Portfolio Analysis"));

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        evalButton.addActionListener(e -> evaluateAllocation());
        historyButton.addActionListener(e -> analyzeHistory());
    }

    private void evaluateAllocation() {
        resultArea.setText("Evaluating Portfolio Allocation...\n");
        // Simulate getting current composition (Hardcoded for demo)
        Map<String, BigDecimal> currentComposition = Map.of(
                "Stocks", new BigDecimal("0.60"), "Bonds", new BigDecimal("0.25"), "Cash", new BigDecimal("0.15")
        );
        resultArea.append("Using Mock Portfolio: Stocks 60%, Bonds 25%, Cash 15%\n---------------------------------------\n");

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return portfolioIntelligenceAlService.evaluatePortfolioAllocation(currentComposition);
            }
            @Override protected void done() {
                try { resultArea.append("Evaluation Result:\n" + get() + "\n"); }
                catch (Exception ex) { handleException(ex, "portfolio evaluation"); }
            }
        }.execute();
    }

    private void analyzeHistory() {
        resultArea.setText("Analyzing Historical Performance...\n");
        // Simulate getting historical data (Hardcoded/Empty for demo)
        Map<LocalDate, BigDecimal> portfolioHistory = Map.of( LocalDate.now().minusMonths(3), new BigDecimal("10000"), LocalDate.now().minusMonths(2), new BigDecimal("10500"), LocalDate.now().minusMonths(1), new BigDecimal("10300"), LocalDate.now(), new BigDecimal("10800"));
        Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory = Map.of( "Nasdaq", Map.of(LocalDate.now().minusMonths(1), new BigDecimal("15000"), LocalDate.now(), new BigDecimal("15500")), "Shanghai 300", Map.of(LocalDate.now().minusMonths(1), new BigDecimal("4000"), LocalDate.now(), new BigDecimal("4100")));
        resultArea.append("Using Mock Historical Data...\n-----------------------------\n");

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return portfolioIntelligenceAlService.analyzeHistoricalPerformance(portfolioHistory, benchmarkHistory);
            }
            @Override protected void done() {
                try { resultArea.append("Analysis Result:\n" + get() + "\n"); }
                catch (Exception ex) { handleException(ex, "historical performance analysis"); }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.append("ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
