package DeepManage_Front;

public class MainFrame extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    
    public MainFrame(String title) {
        super(title);
        setLayout(new BorderLayout());
        
        // 左侧导航栏
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(0, 51, 102)); // 深蓝色背景
        navPanel.setPreferredSize(new Dimension(200, 800));
        
        String[] modules = {"Transactions", "Budget", "Classifier", "Insights", "Portfolio"};
        for (String module : modules) {
            JButton btn = new JButton(module);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(0, 51, 102));
            btn.setBorderPainted(false);
            btn.addActionListener(e -> switchModule(module));
            navPanel.add(btn);
        }
        
        add(navPanel, BorderLayout.WEST);
        
        // 主内容区
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.LIGHT_GRAY);
        
        mainPanel.add(new TransactionModule(), "Transactions");
        mainPanel.add(new BudgetModule(), "Budget");
        mainPanel.add(new ClassifierModule(), "Classifier");
        mainPanel.add(new InsightsModule(), "Insights");
        mainPanel.add(new PortfolioModule(), "Portfolio");
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void switchModule(String module) {
        cardLayout.show(mainPanel, module);
    }

    public void setSize(int i, int j) {
    }

    public void setDefaultCloseOperation(int exitOnClose) {
    }

    public void setVisible(boolean b) {
    }
}
