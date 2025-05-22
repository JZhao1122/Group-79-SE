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
        setLayout(new BorderLayout(15, 15));
        setBorder(DeepManageApp.MAIN_PANEL_BORDER);
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        headerPanel.setBorder(DeepManageApp.MAIN_PANEL_HEADER_BORDER);

        JLabel titleLabel = new JLabel("Portfolio Intelligence");
        titleLabel.setFont(DeepManageApp.FONT_HEADER);
        titleLabel.setForeground(DeepManageApp.COLOR_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton evalButton = createStyledButton("Evaluate Portfolio Allocation");
        JButton historyButton = createStyledButton("Analyze Historical Performance");
        buttonPanel.add(evalButton);
        buttonPanel.add(historyButton);

        // Create results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setOpaque(true);
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Portfolio Analysis", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        resultArea.setEditable(false);
        resultArea.setFont(DeepManageApp.FONT_NORMAL);
        resultArea.setBackground(Color.WHITE);
        resultArea.setForeground(Color.BLACK);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(null);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add all panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);

        // --- Action Listeners ---
        evalButton.addActionListener(e -> evaluateAllocation());
        historyButton.addActionListener(e -> analyzeHistory());
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DeepManageApp.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(DeepManageApp.COLOR_ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(DeepManageApp.COLOR_ACCENT.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(DeepManageApp.COLOR_ACCENT);
            }
        });
        
        return button;
    }

    private void evaluateAllocation() {
        resultArea.setText("Evaluating Portfolio Allocation...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        // Simulate getting current composition (Hardcoded for demo)
        Map<String, BigDecimal> currentComposition = Map.of(
                "Stocks", new BigDecimal("0.60"), "Bonds", new BigDecimal("0.25"), "Cash", new BigDecimal("0.15")
        );
        resultArea.append("Using Mock Portfolio: Stocks 60%, Bonds 25%, Cash 15%\n");
        resultArea.append("---------------------------------------\n");

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return portfolioIntelligenceAlService.evaluatePortfolioAllocation(currentComposition);
            }
            @Override protected void done() {
                try { 
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nEvaluation Result:\n" + get() + "\n"); 
                }
                catch (Exception ex) { handleException(ex, "portfolio evaluation"); }
            }
        }.execute();
    }

    private void analyzeHistory() {
        resultArea.setText("Analyzing Historical Performance...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        // Simulate getting historical data (Hardcoded/Empty for demo)
        Map<LocalDate, BigDecimal> portfolioHistory = Map.of(
            LocalDate.now().minusMonths(3), new BigDecimal("10000"),
            LocalDate.now().minusMonths(2), new BigDecimal("10500"),
            LocalDate.now().minusMonths(1), new BigDecimal("10300"),
            LocalDate.now(), new BigDecimal("10800")
        );
        Map<String, Map<LocalDate, BigDecimal>> benchmarkHistory = Map.of(
            "Nasdaq", Map.of(
                LocalDate.now().minusMonths(1), new BigDecimal("15000"),
                LocalDate.now(), new BigDecimal("15500")
            ),
            "Shanghai 300", Map.of(
                LocalDate.now().minusMonths(1), new BigDecimal("4000"),
                LocalDate.now(), new BigDecimal("4100")
            )
        );
        resultArea.append("Using Mock Historical Data...\n");
        resultArea.append("-----------------------------\n");

        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return portfolioIntelligenceAlService.analyzeHistoricalPerformance(portfolioHistory, benchmarkHistory);
            }
            @Override protected void done() {
                try { 
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nAnalysis Result:\n" + get() + "\n"); 
                }
                catch (Exception ex) { handleException(ex, "historical performance analysis"); }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.setForeground(DeepManageApp.COLOR_ERROR);
        resultArea.append("ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
