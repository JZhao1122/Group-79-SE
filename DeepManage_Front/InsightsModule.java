package DeepManage_Front;

public class InsightsModule extends JPanel {
    private final FinancialInsightsAIService insightsService;
    
    public InsightsModule() {
        setLayout(new BorderLayout());
        insightsService = new FinancialInsightsAIServiceImpl(); // 假设已实现
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Seasonal", createSeasonalPanel());
        tabbedPane.addTab("Regional", createRegionalPanel());
        tabbedPane.addTab("Promotions", createPromotionsPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createSeasonalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 季节性图表
        JFreeChart chart = createSeasonalChart();
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        // 获取建议按钮
        JButton adviceBtn = new JButton("Get Seasonal Advice");
        adviceBtn.addActionListener(e -> {
            String season = (String)JOptionPane.showInputDialog(
                this,
                "Select season:",
                "Season Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"CNY2024", "MidAutumn2023", "Christmas2023"},
                "CNY2024"
            );
            
            if (season != null) {
                try {
                    String advice = insightsService.getSeasonalBudgetAdvice(getCurrentUserId(), season);
                    JTextArea textArea = new JTextArea(advice);
                    textArea.setEditable(false);
                    JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
                        "Seasonal Advice", JOptionPane.INFORMATION_MESSAGE);
                } catch (AIException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        panel.add(adviceBtn, BorderLayout.SOUTH);
        return panel;
    }
    
    private JFreeChart createSeasonalChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        // 模拟数据
        dataset.addValue(1800, "Spending", "Jan");
        dataset.addValue(2500, "Spending", "Feb"); // 春节
        dataset.addValue(1800, "Spending", "Mar");
        dataset.addValue(1900, "Spending", "Apr");
        dataset.addValue(2000, "Spending", "May");
        dataset.addValue(2100, "Spending", "Jun");
        dataset.addValue(2200, "Spending", "Jul");
        dataset.addValue(2300, "Spending", "Aug");
        dataset.addValue(2500, "Spending", "Sep"); // 中秋
        dataset.addValue(2000, "Spending", "Oct");
        dataset.addValue(3000, "Spending", "Nov"); // 双11
        dataset.addValue(2800, "Spending", "Dec"); // 双12
        
        return ChartFactory.createLineChart(
            "Seasonal Spending Trends", 
            "Month", 
            "Amount (¥)", 
            dataset, 
            PlotOrientation.VERTICAL, 
            true, true, false
        );
    }
}