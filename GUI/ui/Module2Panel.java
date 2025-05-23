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
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        JPanel topPanel = new JPanel(new BorderLayout(0, 5)); 
        topPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JButton savingsButton = new JButton("Get Savings Allocation & Update Goal"); // Clarified button text
        buttonPanel.add(savingsButton);

        topPanel.add(buttonPanel, BorderLayout.NORTH); 

        JPanel savingsInputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        savingsInputPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        JLabel savingsLabel = new JLabel("Enter Total Savings Goal (¥):"); // Changed label to reflect it's a goal
        savingsAmountField = new JTextField(10); 
        savingsAmountField.setText("0.00"); // Default to 0, or load from appReference.getCurrentGlobalSavingsGoal()
        savingsInputPanel.add(savingsLabel);
        savingsInputPanel.add(savingsAmountField);

        topPanel.add(savingsInputPanel, BorderLayout.CENTER); 

        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("AI Analysis Results"));

        add(topPanel, BorderLayout.NORTH); 
        add(scrollPane, BorderLayout.CENTER);

        savingsButton.addActionListener(e -> {
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
    }

    private void loadSavingsAllocation() {
        resultArea.setText("Loading Savings Allocation...\n");

        String savingsText = savingsAmountField.getText();
        BigDecimal availableSavings; // This is interpreted as the total savings goal now
        try {
            availableSavings = new BigDecimal(savingsText);
            if (availableSavings.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Savings goal amount cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                resultArea.append("ERROR: Savings goal amount cannot be negative.\n");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid savings goal amount entered. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            resultArea.append("ERROR: Invalid savings goal amount format. Please enter a number (e.g., 5000.00).\n");
            return;
        }
        
        // Update the global savings goal in DeepManageApp
        // This is now also done in the action listener directly for immediate feedback after input, 
        // but can be kept here if the AI call is also based on this specific input amount.
        // appReference.updateUserSavingsGoal(availableSavings); 

        resultArea.append("Processing with available savings goal: ¥" + availableSavings.setScale(2, BigDecimal.ROUND_HALF_UP) + "\n");

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
                    resultArea.append("AI Savings Allocation Suggestion (based on input amount):\n----------------------------------------------------\n");
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
}
