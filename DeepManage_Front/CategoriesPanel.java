package DeepManage_Front;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class CategoriesPanel extends JPanel {
    public CategoriesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // 更柔和的背景色

        // 创建标题
        JLabel title = new JLabel("Expense Categorisation (AI + Manual Correction)", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 表格数据
        String[] columns = {"Date", "Description", "Amount", "AI Category", "User Category", "Actions"};
        Object[][] data = {
                {"2025-03-01", "Starbucks Coffee", "¥35", "Dining", "Dining", "Edit"},
                {"2025-03-02", "Shell Gas Station", "¥200", "Transportation", "Transportation", "Edit"},
                {"2025-03-03", "Netflix Subscription", "¥500", "Entertainment", "Shopping", "Edit"},
                {"2025-03-04", "Walmart Groceries", "¥300", "Shopping", "Groceries", "Edit"},
                {"2025-03-05", "Uber Ride", "¥80", "Transportation", "Transportation", "Edit"},
                {"2025-03-06", "Apple Store", "¥6,000", "Shopping", "Electronics", "Edit"}
        };

        // 表格模型
        DefaultTableModel tableModel = new DefaultTableModel(data, columns);
        JTable transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 14));

        // 设置表格中的按钮渲染器和编辑器
        transactionsTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        transactionsTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox(), transactionsTable));  // 将表格传递给 ButtonEditor

        // 创建表格面板并添加到界面
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        add(scrollPane, BorderLayout.CENTER);

        // AI 学习反馈
        add(new AIlearningFeedbackPanel(), BorderLayout.SOUTH);

        // 季节性分析
        add(new SeasonalAnalysisPanel(), BorderLayout.EAST);
    }
}

