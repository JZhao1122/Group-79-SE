package ui;

import exception.AlException;
import service.FinancialInsightsAlService;

import javax.swing.*;
import java.awt.*;
import java.awt.AlphaComposite;

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
        setLayout(new BorderLayout(15, 15));
        setBorder(DeepManageApp.MAIN_PANEL_BORDER);
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        headerPanel.setBorder(DeepManageApp.MAIN_PANEL_HEADER_BORDER);

        JLabel titleLabel = new JLabel("Financial Insights");
        titleLabel.setFont(DeepManageApp.FONT_HEADER);
        titleLabel.setForeground(DeepManageApp.COLOR_ACCENT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Create input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Get AI Insights", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Style input fields
        styleInputField(seasonIdField);
        styleInputField(regionIdField);
        styleInputField(promotionIdField);

        // Create buttons
        JButton seasonalButton = createStyledButton("Get Seasonal Advice");
        JButton regionalButton = createStyledButton("Get Regional Advice");
        JButton promotionButton = createStyledButton("Get Promotion Advice");

        // Add components to input panel
        gbc.gridx = 0; gbc.gridy = 0; 
        inputPanel.add(createLabel("Season ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; 
        inputPanel.add(seasonIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; 
        inputPanel.add(seasonalButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; 
        inputPanel.add(createLabel("Region ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; 
        inputPanel.add(regionIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; 
        inputPanel.add(regionalButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; 
        inputPanel.add(createLabel("Promotion ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; 
        inputPanel.add(promotionIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 2; 
        inputPanel.add(promotionButton, gbc);

        // Create results panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "AI Advice", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, DeepManageApp.FONT_SUBHEADER, DeepManageApp.COLOR_ACCENT));

        styleTextArea(resultArea);
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(null);
        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Add all panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);
        add(resultsPanel, BorderLayout.SOUTH);

        // Action Listeners
        seasonalButton.addActionListener(e -> getSeasonalAdvice());
        regionalButton.addActionListener(e -> getRegionalAdvice());
        promotionButton.addActionListener(e -> getPromotionAdvice());
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(DeepManageApp.FONT_NORMAL);
        return label;
    }

    private void styleInputField(JTextField field) {
        field.setFont(DeepManageApp.FONT_NORMAL);
        field.setBorder(DeepManageApp.ROUNDED_BORDER);
        field.setBackground(Color.WHITE);
    }

    private void styleTextArea(JTextArea area) {
        area.setEditable(false);
        area.setFont(DeepManageApp.FONT_NORMAL);
        area.setBackground(Color.WHITE);
        area.setForeground(Color.BLACK);
        area.setMargin(new Insets(12, 12, 12, 12));
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(DeepManageApp.FONT_BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(DeepManageApp.COLOR_ACCENT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        
        // Hover effect
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

    private void getSeasonalAdvice() {
        String seasonId = seasonIdField.getText();
        resultArea.setText("Getting seasonal advice for " + seasonId + "...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getSeasonalBudgetAdvice(currentUserId, seasonId);
            }
            @Override protected void done() {
                try { 
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nResult:\n" + get() + "\n"); 
                }
                catch (Exception ex) { handleException(ex, "seasonal budget advice"); }
            }
        }.execute();
    }

    private void getRegionalAdvice() {
        String regionId = regionIdField.getText();
        resultArea.setText("Getting regional advice for " + regionId + "...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getRegionalBudgetAdvice(currentUserId, regionId);
            }
            @Override protected void done() {
                try { 
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nResult:\n" + get() + "\n"); 
                }
                catch (Exception ex) { handleException(ex, "regional budget advice"); }
            }
        }.execute();
    }

    private void getPromotionAdvice() {
        String promotionId = promotionIdField.getText();
        resultArea.setText("Getting promotion advice for " + promotionId + "...\n");
        resultArea.setForeground(DeepManageApp.COLOR_INFO);
        
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getPromotionBudgetAdvice(currentUserId, promotionId);
            }
            @Override protected void done() {
                try { 
                    resultArea.setForeground(Color.BLACK);
                    resultArea.append("\nResult:\n" + get() + "\n"); 
                }
                catch (Exception ex) { handleException(ex, "promotion budget advice"); }
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
