package DeepManage_Front;

public class TransactionModule extends JPanel {
    private final FinancialTransactionService transactionService;
    
    public TransactionModule() {
        setLayout(new BorderLayout());
        transactionService = new FinancialTransactionServiceImpl(); // 假设已实现
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // 手动录入面板
        tabbedPane.addTab("Manual Entry", createManualEntryPanel());
        
        // 批量导入面板
        tabbedPane.addTab("Batch Import", createImportPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createManualEntryPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 金额输入
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Amount:"), gbc);
        JFormattedTextField amountField = new JFormattedTextField(NumberFormat.getNumberInstance());
        amountField.setColumns(15);
        gbc.gridx = 1;
        panel.add(amountField, gbc);
        
        // 日期选择
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date:"), gbc);
        JXDatePicker datePicker = new JXDatePicker();
        datePicker.setFormats("yyyy-MM-dd");
        gbc.gridx = 1;
        panel.add(datePicker, gbc);
        
        // 描述输入
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        JTextField descField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(descField, gbc);
        
        // 支付方式
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Payment Method:"), gbc);
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Alipay", "WeChat Pay", "Bank Card"});
        gbc.gridx = 1;
        panel.add(methodCombo, gbc);
        
        // 提交按钮
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            try {
                TransactionData data = new TransactionData();
                data.setAmount(new BigDecimal(amountField.getText()));
                data.setDate(LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(datePicker.getDate())));
                data.setDescription(descField.getText());
                data.setPaymentMethod((String)methodCombo.getSelectedItem());
                
                String transactionId = transactionService.addTransaction(data);
                JOptionPane.showMessageDialog(this, "Transaction added! ID: " + transactionId, "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields(amountField, descField);
            } catch (TransactionException | ParseException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(submitBtn, gbc);
        
        return panel;
    }
    
    private JPanel createImportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 拖拽上传区域
        DropTargetPanel dropPanel = new DropTargetPanel();
        panel.add(dropPanel, BorderLayout.CENTER);
        
        // 文件选择按钮
        JButton fileBtn = new JButton("Select File");
        fileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                if (file.length() > 10 * 1024 * 1024) {
                    JOptionPane.showMessageDialog(this, "File size exceeds 10MB limit", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try (InputStream stream = new FileInputStream(file)) {
                    int count = transactionService.importTransactions(stream);
                    JOptionPane.showMessageDialog(this, "Successfully imported " + count + " transactions", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Import failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(fileBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void clearFields(JFormattedTextField amountField, JTextField descField) {
        amountField.setValue(null);
        descField.setText("");
    }
}

