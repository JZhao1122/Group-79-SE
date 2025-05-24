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
    private final JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[]{"💳 Alipay", "💚 WeChat Pay", "🏦 Credit Card", "💵 Cash", "🏛️ Bank Transfer"});
    private final JTextArea resultArea = new JTextArea(5, 50);
    private final DeepManageApp appReference; // Reference to the main application

    public Module1Panel(FinancialTransactionService service, DeepManageApp app) {
        this.financialTransactionService = service;
        this.appReference = app; // Store the reference
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);

        // Manual Entry Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                "💰 Manual Transaction Entry",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 16),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Style form components
        styleTextField(amountField);
        styleTextField(dateField);
        styleTextField(descriptionField);
        styleComboBox(paymentMethodCombo);

        // Create styled labels
        JLabel amountLabel = createStyledLabel("💵 Amount:");
        JLabel dateLabel = createStyledLabel("📅 Date (YYYY-MM-DD):");
        JLabel descLabel = createStyledLabel("📝 Description:");
        JLabel paymentLabel = createStyledLabel("💳 Payment Method:");

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(amountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(amountField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(dateLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(dateField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(descriptionField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; formPanel.add(paymentLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; formPanel.add(paymentMethodCombo, gbc);

        JButton saveButton = createStyledButton("💾 Save Transaction", DeepManageApp.COLOR_SUCCESS);
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; 
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(saveButton, gbc);

        // Import Section
        JPanel importPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        importPanel.setBackground(DeepManageApp.COLOR_PANEL_BACKGROUND);
        importPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                "📂 Import Transactions",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 16),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));
        
        JButton importButton = createStyledButton("📁 Import CSV/Excel File", DeepManageApp.COLOR_BUTTON_PRIMARY);
        importPanel.add(importButton);

        // Results Area
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        resultArea.setBackground(new Color(0xF8F9FA));
        resultArea.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10),
                "📋 Activity Log",
                0,
                0,
                new Font("SansSerif", Font.BOLD, 14),
                DeepManageApp.COLOR_TEXT_PRIMARY
            )
        ));

        JPanel bottomArea = new JPanel(new BorderLayout(0, 15));
        bottomArea.setBackground(DeepManageApp.COLOR_MAIN_BACKGROUND);
        bottomArea.add(importPanel, BorderLayout.NORTH);
        bottomArea.add(scrollPane, BorderLayout.CENTER);

        add(formPanel, BorderLayout.NORTH);
        add(bottomArea, BorderLayout.CENTER);

        // Attach listeners
        saveButton.addActionListener(this::saveManualTransaction);
        importButton.addActionListener(this::importFile);
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
    
    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(DeepManageApp.COLOR_BORDER, 1));
    }
    
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(DeepManageApp.COLOR_TEXT_PRIMARY);
        return label;
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

    private void saveManualTransaction(ActionEvent e) {
        try {
            TransactionData data = new TransactionData();
            data.setAmount(new BigDecimal(amountField.getText()));
            data.setDate(LocalDate.parse(dateField.getText()));
            data.setDescription(descriptionField.getText());
            data.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());

            String newId = financialTransactionService.addTransaction(data);
            resultArea.append("✅ SUCCESS: Transaction added with ID: " + newId + "\n");
            JOptionPane.showMessageDialog(this, "✅ Transaction added successfully!\nID: " + newId, "Success", JOptionPane.INFORMATION_MESSAGE);
            amountField.setText(""); dateField.setText(""); descriptionField.setText(""); paymentMethodCombo.setSelectedIndex(0);
            
            // Refresh dashboard after manual entry too
            if (appReference != null) {
                appReference.refreshDashboardData();
            }

        } catch (NumberFormatException | DateTimeParseException ex) {
            resultArea.append("❌ ERROR: Invalid input format.\n");
            JOptionPane.showMessageDialog(this, "❌ Invalid input format:\n• Amount must be a number\n• Date must be YYYY-MM-DD", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (TransactionException ex) {
            resultArea.append("❌ ERROR: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, "❌ Error saving transaction:\n" + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV and Excel files", "csv", "xlsx", "xls"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            resultArea.append("📂 INFO: Importing file: " + selectedFile.getName() + "\n");
            try (InputStream fileStream = new FileInputStream(selectedFile)) {
                int count = financialTransactionService.importTransactions(fileStream);
                resultArea.append("✅ SUCCESS: Imported " + count + " transactions.\n");
                JOptionPane.showMessageDialog(this, "✅ Import successful!\n" + count + " transactions imported.", "Import Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh dashboard data after successful import
                if (appReference != null) {
                    appReference.refreshDashboardData();
                }

            } catch (FileNotFoundException ex) { 
                resultArea.append("❌ ERROR: File not found.\n"); 
                JOptionPane.showMessageDialog(this, "❌ File not found.", "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (TransactionException ex) { 
                resultArea.append("❌ ERROR: Import failed - " + ex.getMessage() + "\n"); 
                JOptionPane.showMessageDialog(this, "❌ Import Failed:\n" + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) { 
                resultArea.append("❌ ERROR: Could not read file.\n"); 
                JOptionPane.showMessageDialog(this, "❌ Error reading file:\n" + ex.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE); 
            }
        } else { 
            resultArea.append("ℹ️ INFO: File import cancelled.\n"); 
        }
    }
}
