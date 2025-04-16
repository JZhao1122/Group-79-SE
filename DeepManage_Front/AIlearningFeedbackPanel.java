package DeepManage_Front;

import javax.swing.*;
import java.awt.*;

public class AIlearningFeedbackPanel extends JPanel {
    public AIlearningFeedbackPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 240, 240));  // 背景色

        // AI 学习反馈标题
        JLabel feedbackTitle = new JLabel("AI Learning Feedback");
        feedbackTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(feedbackTitle);

        // AI 学习反馈内容
        JTextArea feedbackText = new JTextArea("The system learns from your manual corrections to improve future classifications.\n" +
                "Recent Corrections: 2 corrections made in the last 7 days.\n" +
                "Accuracy Improvement: AI accuracy improved by 5% based on your corrections.");
        feedbackText.setEditable(false);
        feedbackText.setLineWrap(true);
        feedbackText.setWrapStyleWord(true);
        add(feedbackText);
    }
}

