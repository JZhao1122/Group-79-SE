package DeepManage_Front;


public class BudgetModule extends JPanel {
    private final FinancialHealthAIService aiService;
    
    public BudgetModule() {
        setLayout(new BorderLayout());
        aiService = new FinancialHealthAIServiceImpl(); // 假设已实现
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Budget", createBudgetPanel());
        tabbedPane.addTab("Savings", createSavingsPanel());
        tabbedPane.addTab("Spending Patterns", createPatternsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createBudgetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 预算表格
        String[] columns = {"Category", "Recommended", "Actual", "Difference"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setDefaultRenderer(Object.class, new BudgetTableCellRenderer());
        
        // 获取AI建议
        JButton refreshBtn = new JButton("Get AI Recommendations");
        refreshBtn.addActionListener(e -> {
            try {
                Map<String, BigDecimal> recommendations = aiService.recommendBudget(getCurrentUserId());
                model.setRowCount(0); // 清空表格
                
                // 模拟实际支出数据
                Map<String, BigDecimal> actualSpending = getActualSpendingData();
                
                for (Map.Entry<String, BigDecimal> entry : recommendations.entrySet()) {
                    String category = entry.getKey();
                    BigDecimal recommended = entry.getValue();
                    BigDecimal actual = actualSpending.getOrDefault(category, BigDecimal.ZERO);
                    BigDecimal difference = actual.subtract(recommended);
                    
                    model.addRow(new Object[]{
                        category, 
                        recommended.setScale(2, RoundingMode.HALF_UP), 
                        actual.setScale(2, RoundingMode.HALF_UP),
                        difference.setScale(2, RoundingMode.HALF_UP)
                    });
                }
            } catch (AIException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // 自定义表格渲染器，用于颜色编码
    private static class BudgetTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (column == 3) { // Difference列
                BigDecimal diff = (BigDecimal)value;
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    c.setBackground(Color.RED); // 超支
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(Color.GREEN); // 结余
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }
            
            return c;
        }
    }
    
    private JPanel createSavingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 储蓄分配饼图
        JFreeChart chart = createSavingsPieChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // 刷新按钮
        JButton refreshBtn = new JButton("Get Savings Allocation");
        refreshBtn.addActionListener(e -> {
            try {
                BigDecimal availableSavings = getAvailableSavings(); // 从用户输入或服务获取
                Map<String, BigDecimal> allocation = aiService.allocateSavings(getCurrentUserId(), availableSavings);
                updatePieChart(chart, allocation);
            } catch (AIException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "AI Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(refreshBtn, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JFreeChart createSavingsPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Emergency", 30);
        dataset.setValue("Short-term", 20);
        dataset.setValue("Long-term", 50);
        
        return ChartFactory.createPieChart(
            "Savings Allocation", 
            dataset, 
            true, true, false
        );
    }
    
    private void updatePieChart(JFreeChart chart, Map<String, BigDecimal> allocation) {
        DefaultPieDataset dataset = (DefaultPieDataset)chart.getDataset();
        dataset.clear();
        
        allocation.forEach((category, amount) -> {
            dataset.setValue(category, amount.doubleValue());
        });
    }
    
    // 其他辅助方法...
    private String getCurrentUserId() {
        return "user123"; // 实际应用中从登录信息获取
    }
    
    private Map<String, BigDecimal> getActualSpendingData() {
        // 模拟数据
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("Food", new BigDecimal("1200.00"));
        data.put("Transport", new BigDecimal("500.00"));
        data.put("Entertainment", new BigDecimal("800.00"));
        return data;
    }
    
    private BigDecimal getAvailableSavings() {
        return new BigDecimal("5000.00"); // 实际应用中从用户输入获取
    }
}