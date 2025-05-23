package ui;

import dto.TransactionData;
import exception.TransactionException;
import service.FinancialTransactionService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Module1Panel extends JPanel {

    private final FinancialTransactionService financialTransactionService;
    private final JTextField amountField = new JTextField(15);
    private final JTextField dateField = new JTextField(10); // Simplified
    private final JTextField descriptionField = new JTextField(25);
    private final JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"Alipay", "WeChat Pay", "Credit Card", "Cash", "Bank Transfer"});
    private final JTextArea resultArea = new JTextArea(5, 50);
    private final DeepManageApp appReference; // Reference to the main application

    public Module1Panel(FinancialTransactionService service, DeepManageApp app) {
        this.financialTransactionService = service;
        this.appReference = app; // Store the reference
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND); // Use color from Main App

        // Manual Entry Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        formPanel.setBorder(BorderFactory.createTitledBorder("Manual Transaction Entry"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        paymentMethodCombo.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND); // Style combo box

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(amountField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(descriptionField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Payment Method:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(paymentMethodCombo, gbc);

        JButton saveButton = new JButton("Save Manual Transaction");
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; formPanel.add(saveButton, gbc);

        // Import Section
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importPanel.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        importPanel.setBorder(BorderFactory.createTitledBorder("Import Transactions"));
        JButton importButton = new JButton("Import CSV/Excel File...");
        importPanel.add(importButton);

        // Results Area
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log"));

        JPanel bottomArea = new JPanel(new BorderLayout());
        bottomArea.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        bottomArea.add(importPanel, BorderLayout.NORTH);
        bottomArea.add(scrollPane, BorderLayout.CENTER);

        add(formPanel, BorderLayout.NORTH);
        add(bottomArea, BorderLayout.CENTER);

        // Attach listeners here or call a separate method
        saveButton.addActionListener(this::saveManualTransaction);
        importButton.addActionListener(this::importFile);
    }

    // Separate listener attachment logic if preferred
    private void attachListeners() {
        // Listeners are attached during initComponents in this version
    }

    private void saveManualTransaction(ActionEvent e) {
        try {
            TransactionData data = new TransactionData();
            data.setAmount(new BigDecimal(amountField.getText()));
            data.setDate(LocalDate.parse(dateField.getText()));
            data.setDescription(descriptionField.getText());
            data.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());

            String newId = financialTransactionService.addTransaction(data);
            resultArea.append("SUCCESS: Transaction added with ID: " + newId + "\n");
            JOptionPane.showMessageDialog(this, "Transaction added with ID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
            amountField.setText(""); dateField.setText(""); descriptionField.setText(""); paymentMethodCombo.setSelectedIndex(0);
            
            // Refresh dashboard after manual entry too
            if (appReference != null) {
                appReference.refreshDashboardData();
            }

        } catch (NumberFormatException | DateTimeParseException ex) {
            resultArea.append("ERROR: Invalid input format.\n");
            JOptionPane.showMessageDialog(this, "Invalid input format:\nAmount must be a number.\nDate must be YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (TransactionException ex) {
            resultArea.append("ERROR: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "Error saving transaction: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.append("INFO: Importing file: " + selectedFile.getName() + "\n");
            try (InputStream fileStream = new FileInputStream(selectedFile)) {
                int count = financialTransactionService.importTransactions(fileStream);
                resultArea.append("SUCCESS: Imported " + count + " transactions.\n");
                JOptionPane.showMessageDialog(this, "Successfully imported " + count + " transactions.", "Import Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh dashboard data after successful import
                if (appReference != null) {
                    appReference.refreshDashboardData();
                }

            } catch (FileNotFoundException ex) { resultArea.append("ERROR: File not found.\n"); JOptionPane.showMessageDialog(this, "File not found.", "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (TransactionException ex) { resultArea.append("ERROR: Import failed - " + ex.getMessage() + "\n"); JOptionPane.showMessageDialog(this, "Import Failed: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) { resultArea.append("ERROR: Could not read file.\n"); JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE); }
        } else { resultArea.append("INFO: File import cancelled.\n"); }
    }
}
