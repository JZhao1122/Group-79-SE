package ui;

import exception.AlException;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import service.FinancialHealthAlService;

public class FinancialPlanningPanel extends JPanel {

    private final FinancialHealthAlService financialHealthAlService;
    private final String currentUserId;
    private final JTextArea resultArea = new JTextArea(15, 60);

    public FinancialPlanningPanel(FinancialHealthAlService service, String userId) {
        this.financialHealthAlService = service;
        this.currentUserId = userId;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Header Panel with title and description
        JPanel headerPanel = createHeaderPanel();
        
        // Combined Panel for actions and results
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Action Panel with styled buttons
        JPanel actionPanel = createActionPanel();
        
        // Results Panel with enhanced styling
        JPanel resultsPanel = createResultsPanel();

        mainPanel.add(actionPanel, BorderLayout.NORTH);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JLabel titleLabel = new JLabel("📈 Financial Planning Assistant");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(DeepManageApp.COLOR_PRIMARY);
        
        JLabel descriptionLabel = new JLabel("<html>Get intelligent budget recommendations and discover spending patterns with AI-powered financial insights.</html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(0x555555));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel mainActionPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        mainActionPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Budget Recommendations Panel
        JPanel budgetPanel = new JPanel(new BorderLayout(10, 10));
        budgetPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        budgetPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel budgetTitle = new JLabel("💰 Budget Optimization");
        budgetTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        budgetTitle.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        JLabel budgetDesc = new JLabel("<html>Get AI-generated budget recommendations based on your spending history and financial goals.</html>");
        budgetDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        budgetDesc.setForeground(new Color(0x666666));
        
        JButton budgetButton = createStyledButton("📊 Generate Budget Plan", DeepManageApp.COLOR_SUCCESS);
        budgetButton.addActionListener(e -> loadBudgetRecommendations());
        
        budgetPanel.add(budgetTitle, BorderLayout.NORTH);
        budgetPanel.add(budgetDesc, BorderLayout.CENTER);
        budgetPanel.add(budgetButton, BorderLayout.SOUTH);
        
        // Spending Patterns Panel
        JPanel patternsPanel = new JPanel(new BorderLayout(10, 10));
        patternsPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        patternsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel patternsTitle = new JLabel("🔍 Pattern Analysis");
        patternsTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        patternsTitle.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        JLabel patternsDesc = new JLabel("<html>Discover hidden spending patterns and habits to better understand your financial behavior.</html>");
        patternsDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        patternsDesc.setForeground(new Color(0x666666));
        
        JButton patternsButton = createStyledButton("🔎 Analyze Patterns", DeepManageApp.COLOR_BUTTON_PRIMARY);
        patternsButton.addActionListener(e -> loadSpendingPatterns());
        
        patternsPanel.add(patternsTitle, BorderLayout.NORTH);
        patternsPanel.add(patternsDesc, BorderLayout.CENTER);
        patternsPanel.add(patternsButton, BorderLayout.SOUTH);
        
        mainActionPanel.add(budgetPanel);
        mainActionPanel.add(patternsPanel);
        
        return mainActionPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(0xF8F9FA));
        resultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultArea.setText("🎯 Welcome to Financial Planning!\n\nChoose an analysis option above to get started:\n\n📊 Budget Optimization:\n   • Get personalized spending limits for each category\n   • Based on your income and financial goals\n   • Optimized for savings and debt reduction\n\n🔍 Pattern Analysis:\n   • Identify recurring spending behaviors\n   • Discover seasonal trends\n   • Find opportunities for cost reduction\n   • Highlight unusual spending activity");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "📋 Analysis Results & Recommendations",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        scrollPane.setMinimumSize(new Dimension(0, 200));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultsPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getModel().isPressed()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    ));
                } else if (getModel().isRollover()) {
                    g.setColor(new Color(
                        Math.max(0, bgColor.getRed() - 20),
                        Math.max(0, bgColor.getGreen() - 20),
                        Math.max(0, bgColor.getBlue() - 20)
                    ));
                } else {
                    g.setColor(bgColor);
                }
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw text
                g.setColor(Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), x, y);
            }
        };
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void loadBudgetRecommendations() {
        resultArea.setText("🔄 Loading Budget Recommendations...\n\n");
        resultArea.append("🤖 AI is analyzing your spending data to create personalized budget recommendations...\n\n");
        
        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws AlException {
                return financialHealthAlService.recommendBudget(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> recommendations = get();
                    resultArea.append("✅ AI BUDGET RECOMMENDATIONS\n");
                    resultArea.append("===========================\n\n");
                    resultArea.append("Based on your spending history and financial profile:\n\n");
                    
                    BigDecimal totalBudget = BigDecimal.ZERO;
                    for (BigDecimal amount : recommendations.values()) {
                        totalBudget = totalBudget.add(amount);
                    }
                    final BigDecimal finalTotalBudget = totalBudget;
                    
                    recommendations.forEach((category, amount) -> {
                        double percentage = amount.doubleValue() / finalTotalBudget.doubleValue() * 100;
                        resultArea.append(String.format("💰 %-20s: ¥ %,10.2f (%.1f%%)\n", category, amount, percentage));
                    });
                    
                    resultArea.append(String.format("\n📊 Total Monthly Budget: ¥ %,10.2f\n\n", totalBudget));
                    resultArea.append("💡 Tips for Success:\n");
                    resultArea.append("   • Track your spending regularly\n");
                    resultArea.append("   • Set up automatic savings transfers\n");
                    resultArea.append("   • Review and adjust monthly\n");
                    resultArea.append("   • Use the 50/30/20 rule as a guideline\n");
                    
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("❌ ERROR: " + cause.getMessage() + "\n\n");
                    resultArea.append("Please try again or contact support if the issue persists.");
                    JOptionPane.showMessageDialog(FinancialPlanningPanel.this, "❌ Error: " + cause.getMessage(), "Budget Analysis Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadSpendingPatterns() {
        resultArea.setText("🔍 Detecting Spending Patterns...\n\n");
        resultArea.append("🔎 AI is analyzing your transaction history to identify behavioral patterns...\n\n");
        
        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws AlException {
                return financialHealthAlService.detectSpendingPatterns(currentUserId);
            }
            @Override
            protected void done() {
                try {
                    List<String> patterns = get();
                    resultArea.append("✅ SPENDING PATTERN ANALYSIS\n");
                    resultArea.append("============================\n\n");
                    resultArea.append("Discovered behavioral insights:\n\n");
                    
                    int patternCount = 1;
                    for (String pattern : patterns) {
                        resultArea.append(String.format("🔹 Pattern %d: %s\n\n", patternCount++, pattern));
                    }
                    
                    resultArea.append("📈 How to Use These Insights:\n");
                    resultArea.append("   • Identify triggers for overspending\n");
                    resultArea.append("   • Plan for seasonal variations\n");
                    resultArea.append("   • Set spending alerts for high-risk periods\n");
                    resultArea.append("   • Develop strategies to break negative patterns\n");
                    resultArea.append("   • Leverage positive trends to reach goals faster\n");
                    
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("❌ ERROR: " + cause.getMessage() + "\n\n");
                    resultArea.append("Please try again or contact support if the issue persists.");
                    JOptionPane.showMessageDialog(FinancialPlanningPanel.this, "❌ Error: " + cause.getMessage(), "Pattern Analysis Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
} 