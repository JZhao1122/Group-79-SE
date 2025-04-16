package DeepManage_Front;

public class ClassifierModule extends JPanel {
    private final TransactionQueryService queryService;
    private final TransactionAnalysisAIService aiService;
    
    public ClassifierModule() {
        setLayout(new BorderLayout());
        queryService = new TransactionQueryServiceImpl(); // 假设已实现
        aiService = new TransactionAnalysisAIServiceImpl(); // 假设已实现
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Transaction Review", createReviewPanel());
        tabbedPane.addTab("Seasonal Analysis", createSeasonalPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createReviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 交易表格
        String[] columns = {"Date", "Amount", "Description", "AI Category", "Current Category", "Action"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // 只允许编辑Current Category和Action列
            }
        };
        
        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new CategoryTableCellRenderer());
        
        // 填充数据
        refreshTransactionTable(model);
        
        // 分类下拉框编辑器
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Food", "Transport", "Entertainment", "Shopping", "Utilities"
        });
        table.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(categoryCombo));
        
        // 保存按钮渲染器和编辑器
        table.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void refreshTransactionTable(DefaultTableModel model) {
        try {
            List<TransactionDisplayData> transactions = queryService.getTransactionsForReview(getCurrentUserId());
            model.setRowCount(0);
            
            for (TransactionDisplayData t : transactions) {
                model.addRow(new Object[]{
                    t.getDate(),
                    t.getAmount(),
                    t.getDescription(),
                    t.getAiSuggestedCategory(),
                    t.getCurrentCategory(),
                    "Save"
                });
            }
        } catch (QueryException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 自定义表格渲染器，高亮显示分类差异
    private static class CategoryTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String aiCategory = (String)table.getValueAt(row, 3);
            String currentCategory = (String)table.getValueAt(row, 4);
            
            if (column == 3 || column == 4) {
                if (!aiCategory.equals(currentCategory)) {
                    c.setBackground(Color.YELLOW);
                } else {
                    c.setBackground(Color.WHITE);
                }
            }
            
            return c;
        }
    }
    
    // 按钮渲染器
    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // 按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private String transactionId;
        private int editedRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.transactionId = (String)table.getValueAt(row, 0); // 假设第一列是ID
            this.editedRow = row;
            
            JButton button = new JButton("Save");
            button.addActionListener(e -> {
                String newCategory = (String)table.getValueAt(row, 4);
                try {
                    queryService.updateTransactionCategory(transactionId, newCategory);
                    fireEditingStopped();
                } catch (TransactionException ex) {
                    JOptionPane.showMessageDialog(table, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            return button;
        }
    }
}
