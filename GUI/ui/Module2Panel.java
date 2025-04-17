package ui;

import exception.AlException;
import service.FinancialHealthAlService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class Module2Panel extends JPanel {

    private final FinancialHealthAlService financialHealthAlService;
    private final String currentUserId; // Assuming user ID is needed
    private final JTextArea resultArea = new JTextArea(15, 60);

    public Module2Panel(FinancialHealthAlService service, String userId) {
        this.financialHealthAlService = service;
        this.currentUserId = userId;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton budgetButton = new JButton("Get Budget Recommendations");
        JButton savingsButton = new JButton("Get Savings Allocation");
        JButton patternsButton = new JButton("Detect Spending Patterns");
        buttonPanel.add(budgetButton);
        buttonPanel.add(savingsButton);
        buttonPanel.add(patternsButton);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI Analysis Results"));

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        budgetButton.addActionListener(e -> loadBudgetRecommendations());
        savingsButton.addActionListener(e -> loadSavingsAllocation());
        patternsButton.addActionListener(e -> loadSpendingPatterns());
    }

    private void loadBudgetRecommendations() {
        resultArea.setText("Loading Budget Recommendations...\n");
        // Use SwingWorker for potentially long-running tasks to avoid blocking EDT
        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws AlException {
                return financialHealthAlService.recommendBudget(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> recommendations = get();
                    resultArea.append("AI Budget Recommendations:\n---------------------------\n");
                    recommendations.forEach((category, amount) ->
                            resultArea.append(String.format("%-20s: ¥ %,10.2f\n", category, amount))
                    );
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("ERROR loading budget recommendations: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSavingsAllocation() {
        resultArea.setText("Loading Savings Allocation...\n");
        BigDecimal availableSavings = new BigDecimal("5000.00"); // Mock value
        resultArea.append("Assuming available savings: ¥" + availableSavings + "\n");

        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws AlException {
                return financialHealthAlService.allocateSavings(currentUserId, availableSavings);
            }
            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> allocation = get();
                    resultArea.append("AI Savings Allocation Suggestion:\n-------------------------------\n");
                    allocation.forEach((goal, amount) ->
                            resultArea.append(String.format("%-25s: ¥ %,10.2f\n", goal, amount))
                    );
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("ERROR loading savings allocation: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSpendingPatterns() {
        resultArea.setText("Detecting Spending Patterns...\n");
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return financialHealthAlService.detectSpendingPatterns(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    resultArea.append("Detected Spending Patterns:\n---------------------------\n");
                    patterns.forEach(pattern -> resultArea.append("- " + pattern + "\n"));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("ERROR detecting spending patterns: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
