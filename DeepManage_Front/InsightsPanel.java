package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class InsightsPanel extends JPanel {
    public InsightsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // 更柔和的背景色

        // 创建标题
        JLabel title = new JLabel("Spending Pattern Analysis", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 消费趋势图
        JPanel chartPanel = new JPanel();
        chartPanel.add(new JLabel("Spending Trend (Last 6 Months)"));
        // 这里你可以用实际的图表库（比如 JFreeChart）来展示消费趋势
        add(chartPanel, BorderLayout.CENTER);

        // 消费模式分析
        JPanel categoryPanel = new JPanel();
        categoryPanel.add(new JLabel("Category Analysis"));
        categoryPanel.add(new JLabel("Dining: 30%, Transport: 15%, Shopping: 25%, Entertainment: 10%"));
        add(categoryPanel, BorderLayout.SOUTH);

        // 检测到的消费模式
        JPanel detectedPatternsPanel = new JPanel();
        detectedPatternsPanel.add(new JLabel("Detected Spending Patterns"));
        detectedPatternsPanel.add(new JCheckBox("Large expenses around the 10th of each month"));
        detectedPatternsPanel.add(new JCheckBox("Weekend dining expenses higher than weekdays"));
        detectedPatternsPanel.add(new JCheckBox("Shopping expenses show monthly growth trend"));
        add(detectedPatternsPanel, BorderLayout.SOUTH);
    }
}

