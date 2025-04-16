package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    private JPanel contentPanel;  // 右侧的内容区域
    private JPanel sidebarPanel;  // 左侧的侧边栏

    public MainFrame() {
        // 设置窗口
        setTitle("AI-Driven Financial Health Management");
        setSize(1000, 600);  // 增加窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建侧边栏
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 240));  // 柔和的背景颜色

        // 创建侧边栏按钮
        JButton transactionsButton = createSidebarButton("Transactions", "icons/transactions-icon.png");
        JButton categoriesButton = createSidebarButton("Categories", "icons/categories-icon.png");
        JButton budgetsButton = createSidebarButton("Budgets", "icons/budgets-icon.png");
        JButton savingsGoalsButton = createSidebarButton("Savings Goals", "icons/savings-goals-icon.png");
        JButton insightsButton = createSidebarButton("Insights & Predictions", "icons/insights-icon.png");

        // 添加按钮到侧边栏
        sidebarPanel.add(transactionsButton);
        sidebarPanel.add(categoriesButton);
        sidebarPanel.add(budgetsButton);
        sidebarPanel.add(savingsGoalsButton);
        sidebarPanel.add(insightsButton);

        // 设置侧边栏的宽度
        sidebarPanel.setPreferredSize(new Dimension(220, 600));

        // 创建内容面板并使用 CardLayout 进行切换
        contentPanel = new JPanel();
        contentPanel.setLayout(new CardLayout());

        // 将各个模块添加到内容区域
        contentPanel.add(new TransactionsPanel(), "Transactions");
        contentPanel.add(new CategoriesPanel(), "Categories");  // 添加 Categories 面板
        contentPanel.add(new BudgetsPanel(), "Budgets");
        contentPanel.add(new SavingsGoalsPanel(), "SavingsGoals");
        contentPanel.add(new InsightsPanel(), "Insights");

        // 设置布局
        setLayout(new BorderLayout());
        add(sidebarPanel, BorderLayout.WEST);  // 左侧菜单栏
        add(contentPanel, BorderLayout.CENTER);  // 右侧内容区

        // 设置按钮点击事件，切换内容区的面板
        transactionsButton.addActionListener(e -> showPanel("Transactions"));
        categoriesButton.addActionListener(e -> showPanel("Categories"));  // 点击 Categories 时切换到 CategoriesPanel
        budgetsButton.addActionListener(e -> showPanel("Budgets"));
        savingsGoalsButton.addActionListener(e -> showPanel("SavingsGoals"));
        insightsButton.addActionListener(e -> showPanel("Insights"));

        // 默认显示 Transactions 面板
        showPanel("Transactions");

        // 显示窗口
        setVisible(true);
    }

    // 创建侧边栏按钮，包含图标和文本
    private JButton createSidebarButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setIcon(new ImageIcon(iconPath));  // 设置按钮图标
        button.setPreferredSize(new Dimension(200, 50));  // 设置统一的按钮尺寸
        button.setBackground(new Color(55, 55, 255));  // 按钮颜色
        button.setForeground(Color.WHITE);  // 按钮文本颜色
        button.setFont(new Font("Arial", Font.BOLD, 16));  // 字体
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        return button;
    }

    // 显示指定的面板
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);  // 切换面板
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}

