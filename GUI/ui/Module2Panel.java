package ui;

import exception.AlException;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import service.FinancialHealthAlService;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(DeepManageApp.MAIN_PANEL_BORDER);
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        headerPanel.setBorder(DeepManageApp.MAIN_PANEL_HEADER_BORDER);

        JLabel titleLabel = new JLabel("Financial Health Analysis");
        titleLabel.setFont(DeepManageApp.FONT_HEADER);
        titleLabel.setForeground(DeepManageApp.COLOR_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create button panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton budgetButton = createStyledButton("Budget Recommendations");
        JButton savingsButton = createStyledButton("Savings Allocation");
        JButton patternsButton = createStyledButton("Spending Patterns");

        buttonPanel.add(budgetButton);
        buttonPanel.add(savingsButton);
        buttonPanel.add(patternsButton);

        // Create results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setOpaque(true);
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "AI Analysis Results", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
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
        budgetButton.addActionListener(e -> loadBudgetRecommendations());
        savingsButton.addActionListener(e -> loadSavingsAllocation());
        patternsButton.addActionListener(e -> loadSpendingPatterns());
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

    private void loadBudgetRecommendations() {
        resultArea.setText("Loading Budget Recommendations...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws AlException {
                return financialHealthAlService.recommendBudget(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> recommendations = get();
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nAI Budget Recommendations:\n");
                    resultArea.append("---------------------------\n");
                    // Align output: category left, colon, amount right
                    int maxLen = 0;
                    for (String category : recommendations.keySet()) {
                        if (category.length() > maxLen) maxLen = category.length();
                    }
                    for (Map.Entry<String, BigDecimal> entry : recommendations.entrySet()) {
                        String category = entry.getKey();
                        BigDecimal amount = entry.getValue();
                        resultArea.append(String.format("%-" + (maxLen + 2) + "s: ¥ %,10.2f\n", category, amount));
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    resultArea.append("ERROR loading budget recommendations: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSavingsAllocation() {
        resultArea.setText("Loading Savings Allocation...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
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
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nAI Savings Allocation Suggestion:\n");
                    resultArea.append("-------------------------------\n");
                    // Align output: goal left, colon, amount right
                    int maxLen = 0;
                    for (String goal : allocation.keySet()) {
                        if (goal.length() > maxLen) maxLen = goal.length();
                    }
                    for (Map.Entry<String, BigDecimal> entry : allocation.entrySet()) {
                        String goal = entry.getKey();
                        BigDecimal amount = entry.getValue();
                        resultArea.append(String.format("%-" + (maxLen + 2) + "s: ¥ %,10.2f\n", goal, amount));
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    resultArea.append("ERROR loading savings allocation: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSpendingPatterns() {
        resultArea.setText("Detecting Spending Patterns...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return financialHealthAlService.detectSpendingPatterns(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nDetected Spending Patterns:\n");
                    resultArea.append("---------------------------\n");
                    patterns.forEach(pattern -> resultArea.append("• " + pattern + "\n"));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.setForeground(DeepManageApp.COLOR_ERROR);
                    resultArea.append("ERROR detecting spending patterns: " + cause.getMessage() + "\n");
                    JOptionPane.showMessageDialog(Module2Panel.this, "Error: " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
