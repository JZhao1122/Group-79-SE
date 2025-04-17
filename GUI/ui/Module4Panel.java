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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Get AI Insights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JButton seasonalButton = new JButton("Get Seasonal Advice");
        JButton regionalButton = new JButton("Get Regional Advice");
        JButton promotionButton = new JButton("Get Promotion Advice");

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Season ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(seasonIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; inputPanel.add(seasonalButton, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Region ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(regionIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 1; inputPanel.add(regionalButton, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Promotion ID:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(promotionIdField, gbc);
        gbc.gridx = 2; gbc.gridy = 2; inputPanel.add(promotionButton, gbc);

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI Advice"));

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        seasonalButton.addActionListener(e -> getSeasonalAdvice());
        regionalButton.addActionListener(e -> getRegionalAdvice());
        promotionButton.addActionListener(e -> getPromotionAdvice());
    }

    private void getSeasonalAdvice() {
        String seasonId = seasonIdField.getText();
        resultArea.setText("Getting seasonal advice for " + seasonId + "...\n");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getSeasonalBudgetAdvice(currentUserId, seasonId);
            }
            @Override protected void done() {
                try { resultArea.append("Result:\n" + get() + "\n"); }
                catch (Exception ex) { handleException(ex, "seasonal budget advice"); }
            }
        }.execute();
    }

    private void getRegionalAdvice() {
        String regionId = regionIdField.getText();
        resultArea.setText("Getting regional advice for " + regionId + "...\n");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getRegionalBudgetAdvice(currentUserId, regionId);
            }
            @Override protected void done() {
                try { resultArea.append("Result:\n" + get() + "\n"); }
                catch (Exception ex) { handleException(ex, "regional budget advice"); }
            }
        }.execute();
    }

    private void getPromotionAdvice() {
        String promotionId = promotionIdField.getText();
        resultArea.setText("Getting promotion advice for " + promotionId + "...\n");
        new SwingWorker<String, Void>() {
            @Override protected String doInBackground() throws AlException {
                return financialInsightsAlService.getPromotionBudgetAdvice(currentUserId, promotionId);
            }
            @Override protected void done() {
                try { resultArea.append("Result:\n" + get() + "\n"); }
                catch (Exception ex) { handleException(ex, "promotion budget advice"); }
            }
        }.execute();
    }

    private void handleException(Exception ex, String context) {
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        resultArea.append("ERROR getting " + context + ": " + cause.getMessage() + "\n");
        JOptionPane.showMessageDialog(this, "Error getting " + context + ": " + cause.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
    }
}
