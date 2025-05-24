package ui;

import exception.AlException;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;
import javax.swing.*;
import service.FinancialHealthAlService;
// No direct import of DeepManageApp needed if passed as a more generic interface or type,
// but for direct callback, it's simpler for this example.

public class Module2Panel extends JPanel {

    private final FinancialHealthAlService financialHealthAlService;
    private final String currentUserId; 
    private final JTextArea resultArea = new JTextArea(15, 60);
    private JTextField savingsAmountField; 
    private final DeepManageApp appReference; // Reference to the main application

    public Module2Panel(FinancialHealthAlService service, String userId, DeepManageApp app) { // Added DeepManageApp app
        this.financialHealthAlService = service;
        this.currentUserId = userId;
        this.appReference = app; // Store the reference
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Header Panel with title and description
        JPanel headerPanel = createHeaderPanel();
        
        // Combined Panel for input and results
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Input Panel with styled components
        JPanel inputPanel = createInputPanel();
        
        // Results Panel with enhanced styling
        JPanel resultsPanel = createResultsPanel();

        mainPanel.add(inputPanel, BorderLayout.NORTH);
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
        
        JLabel titleLabel = new JLabel("❤️ Financial Health Analysis");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(DeepManageApp.COLOR_PRIMARY);
        
        JLabel descriptionLabel = new JLabel("<html>Get AI-powered savings allocation recommendations based on your financial goals and spending patterns.</html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(0x555555));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel mainInputPanel = new JPanel(new BorderLayout(15, 15));
        mainInputPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Savings Goal Input Section
        JPanel savingsPanel = new JPanel(new GridBagLayout());
        savingsPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        savingsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                "🎯 Savings Goal Configuration",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 16),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel savingsLabel = new JLabel("💰 Total Savings Goal (¥):");
        savingsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        savingsLabel.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        savingsAmountField = new JTextField(15);
        savingsAmountField.setText("0.00");
        styleTextField(savingsAmountField);
        
        JButton analyzeButton = createStyledButton("🔍 Analyze & Generate Recommendations", new Color(0x1B4F72));
        
        gbc.gridx = 0; gbc.gridy = 0;
        savingsPanel.add(savingsLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        savingsPanel.add(savingsAmountField, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        savingsPanel.add(analyzeButton, gbc);
        
        mainInputPanel.add(savingsPanel, BorderLayout.NORTH);
        
        // Action listener
        analyzeButton.addActionListener(e -> {
            loadSavingsAllocation();
            // Also update the global savings goal via appReference
            try {
                BigDecimal goalAmount = new BigDecimal(savingsAmountField.getText());
                if (goalAmount.compareTo(BigDecimal.ZERO) >= 0) {
                    appReference.updateUserSavingsGoal(goalAmount);
                }
            } catch (NumberFormatException ex) {
                // Error already handled in loadSavingsAllocation, but good to be safe
                System.err.println("Invalid number format in savings field when trying to update global goal.");
            }
        });
        
        return mainInputPanel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(0xF8F9FA));
        resultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultArea.setText("💡 Enter your savings goal above and click 'Analyze' to get AI-powered recommendations.\n\nThe system will provide personalized allocation suggestions based on:\n• Your current spending patterns\n• Financial best practices\n• Risk assessment\n• Goal prioritization");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "📊 AI Analysis Results",
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
    
    private void styleTextField(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
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
        button.setOpaque(false); // 设为false让我们的paintComponent生效
        button.setContentAreaFilled(false); // 设为false让我们的paintComponent生效
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void loadSavingsAllocation() {
        resultArea.setText("🔄 Loading Savings Allocation Analysis...\n\n");

        String savingsText = savingsAmountField.getText();
        BigDecimal availableSavings; // This is interpreted as the total savings goal now
        try {
            availableSavings = new BigDecimal(savingsText);
            if (availableSavings.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "❌ Savings goal amount cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                resultArea.append("❌ ERROR: Savings goal amount cannot be negative.\n");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Invalid savings goal amount entered.\nPlease enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            resultArea.append("❌ ERROR: Invalid savings goal amount format.\nPlease enter a number (e.g., 5000.00).\n");
            return;
        }
        
        // Update the global savings goal in DeepManageApp
        // This is now also done in the action listener directly for immediate feedback after input, 
        // but can be kept here if the AI call is also based on this specific input amount.
        // appReference.updateUserSavingsGoal(availableSavings); 

        resultArea.append("💰 Processing with savings goal: ¥" + availableSavings.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");
        resultArea.append("🤖 AI is analyzing your financial data...\n\n");

        final BigDecimal finalAvailableSavings = availableSavings; 

        new SwingWorker<Map<String, BigDecimal>, Void>() {
            @Override
            protected Map<String, BigDecimal> doInBackground() throws AlException {
                // The AI service might interpret this as amount *to be allocated*
                // If the intent is for the AI to work with a *total goal* and suggest how to reach it,
                // the prompt to the AI service might need adjustment.
                // For now, it's passed as `availableSavings` to the existing method.
                return financialHealthAlService.allocateSavings(currentUserId, finalAvailableSavings);
            }
            @Override
            protected void done() {
                try {
                    Map<String, BigDecimal> allocation = get();
                    resultArea.append("✅ AI SAVINGS ALLOCATION RECOMMENDATIONS\n");
                    resultArea.append("=====================================\n\n");
                    resultArea.append("Based on your financial profile and goal of ¥" + finalAvailableSavings.setScale(2, BigDecimal.ROUND_HALF_UP) + ":\n\n");
                    
                    allocation.forEach((goal, amount) ->
                            resultArea.append(String.format("💎 %-25s: ¥ %,10.2f\n", goal, amount))
                    );
                    
                    resultArea.append("\n📝 This allocation is optimized based on:\n");
                    resultArea.append("   • Your current spending patterns\n");
                    resultArea.append("   • Financial stability requirements\n");
                    resultArea.append("   • Growth potential analysis\n");
                    resultArea.append("   • Risk management principles\n");
                    
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    resultArea.append("❌ ERROR: " + cause.getMessage() + "\n\n");
                    resultArea.append("Please try again or contact support if the issue persists.");
                    JOptionPane.showMessageDialog(Module2Panel.this, "❌ Error: " + cause.getMessage(), "AI Analysis Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}
