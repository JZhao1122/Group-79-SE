package DeepManage_Front;

import javax.swing.*;
import java.awt.*;

public class SeasonalAnalysisPanel extends JPanel {
    public SeasonalAnalysisPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 240, 240));  // 背景色

        // 季节性分析标题
        JLabel seasonalTitle = new JLabel("Seasonal Analysis");
        seasonalTitle.setFont(new Font("Arial", Font.BOLD, 16));
        add(seasonalTitle);

        // 季节性分析内容
        JTextArea seasonalText = new JTextArea("Detected Seasonal Spending Patterns:\n" +
                "- Higher expenses during Chinese New Year (Jan-Feb).\n" +
                "- Increased shopping during Double 11 (November).\n" +
                "- Summer travel expenses peak in July-August.");
        seasonalText.setEditable(false);
        seasonalText.setLineWrap(true);
        seasonalText.setWrapStyleWord(true);
        add(seasonalText);
    }
}

