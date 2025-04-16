package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class SavingsGoalsPanel extends JPanel {
    public SavingsGoalsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // 更柔和的背景色

        // 创建标题
        JLabel title = new JLabel("Savings Goals", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 储蓄目标
        JPanel goalPanel = new JPanel();
        goalPanel.setLayout(new BoxLayout(goalPanel, BoxLayout.Y_AXIS));
        goalPanel.add(new JLabel("Summer Vacation - ¥8,000 / ¥10,000 (80% Complete)"));
        goalPanel.add(new JLabel("New Laptop - ¥2,000 / ¥6,000 (33% Complete)"));
        add(goalPanel, BorderLayout.CENTER);

        // AI 推荐的储蓄分配
        JPanel savingRecommendationsPanel = new JPanel();
        savingRecommendationsPanel.setLayout(new BoxLayout(savingRecommendationsPanel, BoxLayout.Y_AXIS));
        savingRecommendationsPanel.add(new JLabel("AI Saving Recommendations"));
        savingRecommendationsPanel.add(new JTextArea("Recommended Allocation: Emergency Fund: ¥800, Short-term Goals: ¥600"));
        add(savingRecommendationsPanel, BorderLayout.SOUTH);

        // 按钮
        JButton applyRecommendationButton = new JButton("Apply This Suggestion");
        savingRecommendationsPanel.add(applyRecommendationButton);
    }
}

