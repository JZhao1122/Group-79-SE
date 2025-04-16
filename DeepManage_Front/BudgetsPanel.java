package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class BudgetsPanel extends JPanel {
    public BudgetsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // 更柔和的背景色

        // 创建标题
        JLabel title = new JLabel("AI Budget Suggestions", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // AI 预算建议表格
        String[] columns = {"Category", "Recommended", "Current Actual"};
        Object[][] data = {
                {"Dining", "¥1,500/month", "¥1,800/month"},
                {"Transportation", "¥600/month", "¥550/month"},
                {"Shopping", "¥2,000/month", "¥2,500/month"},
                {"Entertainment", "¥1,000/month", "¥1,200/month"}
        };
        JTable budgetTable = new JTable(data, columns);
        add(new JScrollPane(budgetTable), BorderLayout.CENTER);

        // 节省机会
        JPanel savingPanel = new JPanel();
        savingPanel.setLayout(new BoxLayout(savingPanel, BoxLayout.Y_AXIS));
        savingPanel.add(new JLabel("Saving Opportunities"));
        savingPanel.add(new JCheckBox("Reduce takeout frequency to save ~¥300/month"));
        savingPanel.add(new JCheckBox("Consider consolidating your 3 streaming services"));
        savingPanel.add(new JCheckBox("Bring lunch on workdays to save ~¥500/month"));
        add(savingPanel, BorderLayout.SOUTH);

        // 按钮
        JButton moreSuggestionsButton = new JButton("See More Suggestions");
        savingPanel.add(moreSuggestionsButton);
    }
}

