package ui;

import exception.AlException;
import service.FinancialInsightsAlService;

import javax.swing.*;
import java.awt.*;

public class Module4Panel extends JPanel {

    private final FinancialInsightsAlService financialInsightsAlService;
    private final String currentUserId;

    private final JTextField seasonIdField = new JTextField("CNY2024", 15);
    private final JTextField regionIdField = new JTextField("Urban", 15);
    private final JTextField promotionIdField = new JTextField("Double11_2023", 15);
    private final JTextArea resultArea = new JTextArea(10, 60);

    public Module4Panel(FinancialInsightsAlService service, String userId) {
        this.financialInsightsAlService = service;
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
        
        // Input Panel with styled cards
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
        
        JLabel titleLabel = new JLabel("💡 Financial Insights & AI Advice");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(DeepManageApp.COLOR_PRIMARY);
        
        JLabel descriptionLabel = new JLabel("<html>Get personalized financial advice based on seasonal trends, regional patterns, and promotional opportunities.</html>");
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(0x555555));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    private JPanel createInputPanel() {
        JPanel mainInputPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        mainInputPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        // Seasonal Advice Panel
        JPanel seasonalPanel = createAdvicePanel(
            "🌦️ Seasonal Intelligence",
            "Get budget advice tailored to seasonal spending patterns and market conditions.",
            "Season ID:",
            seasonIdField,
            "🍂 Get Seasonal Advice",
            DeepManageApp.COLOR_ACCENT,
            this::getSeasonalAdvice
        );
        
        // Regional Advice Panel
        JPanel regionalPanel = createAdvicePanel(
            "🏙️ Regional Insights",
            "Receive location-specific financial recommendations based on regional economic data.",
            "Region ID:",
            regionIdField,
            "🗺️ Get Regional Advice",
            DeepManageApp.COLOR_SUCCESS,
            this::getRegionalAdvice
        );
        
        // Promotion Advice Panel
        JPanel promotionPanel = createAdvicePanel(
            "🎯 Promotion Analytics",
            "Optimize your spending during promotional periods with smart budget strategies.",
            "Promotion ID:",
            promotionIdField,
            "🛍️ Get Promotion Advice",
            DeepManageApp.COLOR_WARNING,
            this::getPromotionAdvice
        );
        
        mainInputPanel.add(seasonalPanel);
        mainInputPanel.add(regionalPanel);
        mainInputPanel.add(promotionPanel);
        
        return mainInputPanel;
    }
    
    private JPanel createAdvicePanel(String title, String description, String fieldLabel, 
                                   JTextField field, String buttonText, Color buttonColor, 
                                   Runnable action) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLbl.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        
        // Description
        JLabel descLbl = new JLabel("<html>" + description + "</html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(new Color(0x666666));
        
        // Input section
        JPanel inputSection = new JPanel(new BorderLayout(5, 5));
        inputSection.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        
        JLabel fieldLbl = new JLabel(fieldLabel);
        fieldLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fieldLbl.setForeground(DeepManageApp.COLOR_TEXT_SECONDARY);
        
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(Color.WHITE);
        
        inputSection.add(fieldLbl, BorderLayout.NORTH);
        inputSection.add(field, BorderLayout.CENTER);
        
        // Button
        JButton button = createStyledButton(buttonText, buttonColor);
        button.addActionListener(e -> action.run());
        
        // Text section (title + description)
        JPanel textSection = new JPanel();
        textSection.setLayout(new BoxLayout(textSection, BoxLayout.Y_AXIS));
        textSection.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        textSection.add(titleLbl);
        textSection.add(Box.createVerticalStrut(8));
        textSection.add(descLbl);
        
        panel.add(textSection, BorderLayout.NORTH);
        panel.add(inputSection, BorderLayout.CENTER);
        panel.add(button, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createResultsPanel() {
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(0xF8F9FA));
        resultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        resultArea.setText("🎯 Welcome to Financial Insights!\n\nChoose an insight type above to get started:\n\n🌦️ Seasonal Intelligence:\n   • Seasonal spending pattern analysis\n   • Holiday and event budget recommendations\n   • Weather-based financial planning\n   • Quarterly budget optimization\n\n🏙️ Regional Insights:\n   • Location-specific cost of living advice\n   • Regional market trend analysis\n   • Local economic condition insights\n   • Area-based investment opportunities\n\n🎯 Promotion Analytics:\n   • Sale event budget strategies\n   • Discount optimization recommendations\n   • Promotional spending guidelines\n   • Deal timing and value analysis");
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "🤖 AI-Generated Financial Insights & Recommendations",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        scrollPane.setMinimumSize(new Dimension(0, 250));
        
        resultsPanel.add(scrollPane, BorderLayout.CENTER);
        
        return resultsPanel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color buttonColor = bgColor;
                if (getModel().isPressed()) {
                    buttonColor = new Color(
                        Math.max(0, bgColor.getRed() - 40),
                        Math.max(0, bgColor.getGreen() - 40),
                        Math.max(0, bgColor.getBlue() - 40)
                    );
                } else if (getModel().isRollover()) {
                    buttonColor = new Color(
                        Math.max(0, bgColor.getRed() - 20),
                        Math.max(0, bgColor.getGreen() - 20),
                        Math.max(0, bgColor.getBlue() - 20)
                    );
                }
                
                g2.setColor(buttonColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Draw text
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void getSeasonalAdvice() {
        String seasonId = seasonIdField.getText().trim();
        if (seasonId.isEmpty()) {
            resultArea.setText("⚠️ Please enter a Season ID before requesting advice.\n");
            return;
        }
        
        resultArea.setText("🔄 Getting seasonal advice for: " + seasonId + "\n\n");
        resultArea.append("🤖 AI is analyzing seasonal spending patterns and market trends...\n\n");
        
        new SwingWorker<String, Void>() {
            @Override 
            protected String doInBackground() throws AlException {
                return financialInsightsAlService.getSeasonalBudgetAdvice(currentUserId, seasonId);
            }
            
            @Override 
            protected void done() {
                try { 
                    String advice = get();
                    resultArea.append("🌦️ Seasonal Financial Insights:\n");
                    resultArea.append("═══════════════════════════════════════════════════════\n");
                    resultArea.append(advice + "\n\n");
                    resultArea.append("✨ Apply these seasonal recommendations to optimize your budget!\n");
                } catch (Exception ex) { 
                    handleException(ex, "seasonal budget advice"); 
                }
            }
        }.execute();
    }

    private void getRegionalAdvice() {
        String regionId = regionIdField.getText().trim();
        if (regionId.isEmpty()) {
            resultArea.setText("⚠️ Please enter a Region ID before requesting advice.\n");
            return;
        }
        
        resultArea.setText("🔄 Getting regional advice for: " + regionId + "\n\n");
        resultArea.append("🤖 AI is analyzing regional economic data and cost patterns...\n\n");
        
        new SwingWorker<String, Void>() {
            @Override 
            protected String doInBackground() throws AlException {
                return financialInsightsAlService.getRegionalBudgetAdvice(currentUserId, regionId);
            }
            
            @Override 
            protected void done() {
                try { 
                    String advice = get();
                    resultArea.append("🏙️ Regional Financial Insights:\n");
                    resultArea.append("═══════════════════════════════════════════════════════\n");
                    resultArea.append(advice + "\n\n");
                    resultArea.append("🗺️ Use these regional insights for location-optimized financial planning!\n");
                } catch (Exception ex) { 
                    handleException(ex, "regional budget advice"); 
                }
            }
        }.execute();
    }

    private void getPromotionAdvice() {
        String promotionId = promotionIdField.getText().trim();
        if (promotionId.isEmpty()) {
            resultArea.setText("⚠️ Please enter a Promotion ID before requesting advice.\n");
            return;
        }
        
        resultArea.setText("🔄 Getting promotion advice for: " + promotionId + "\n\n");
        resultArea.append("🤖 AI is analyzing promotional patterns and spending opportunities...\n\n");
        
        new SwingWorker<String, Void>() {
            @Override 
            protected String doInBackground() throws AlException {
                return financialInsightsAlService.getPromotionBudgetAdvice(currentUserId, promotionId);
            }
            
            @Override 
            protected void done() {
                try { 
                    String advice = get();
                    resultArea.append("🎯 Promotion Strategy Insights:\n");
                    resultArea.append("═══════════════════════════════════════════════════════\n");
                    resultArea.append(advice + "\n\n");
                    resultArea.append("🛍️ Implement these strategies to maximize your promotional savings!\n");
                } catch (Exception ex) { 
                    handleException(ex, "promotion budget advice"); 
                }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.append("❌ ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
