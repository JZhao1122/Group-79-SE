package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TransactionsPanel extends JPanel {
    public TransactionsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // 更柔和的背景色

        // 创建标题
        JLabel title = new JLabel("Mode Selection", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 创建模式选择按钮
        JPanel buttonPanel = new JPanel();
        JButton manualEntryButton = new JButton("Manual Entry");
        JButton importCSVButton = new JButton("Import CSV");

        // 设置按钮样式
        manualEntryButton.setBackground(new Color(55, 55, 255));
        manualEntryButton.setForeground(Color.WHITE);
        importCSVButton.setBackground(new Color(55, 55, 255));
        importCSVButton.setForeground(Color.WHITE);

        // 设置按钮尺寸
        manualEntryButton.setPreferredSize(new Dimension(150, 40));
        importCSVButton.setPreferredSize(new Dimension(150, 40));

        // 将按钮添加到面板
        buttonPanel.add(manualEntryButton);
        buttonPanel.add(importCSVButton);
        add(buttonPanel, BorderLayout.CENTER);

        // 创建内容区域，使用 CardLayout 切换显示
        JPanel modePanel = new JPanel();
        modePanel.setLayout(new CardLayout());

        // 创建两个模式：手动录入和导入 CSV
        modePanel.add(new ManualEntryPanel(), "ManualEntry");
        modePanel.add(new ImportCSVPanel(), "ImportCSV");

        add(modePanel, BorderLayout.SOUTH);

        // 设置按钮点击事件切换模式
        manualEntryButton.addActionListener(e -> showMode(modePanel, "ManualEntry"));
        importCSVButton.addActionListener(e -> showMode(modePanel, "ImportCSV"));

        // 默认显示手动录入模式
        showMode(modePanel, "ManualEntry");
    }

    // 切换显示模式
    private void showMode(JPanel modePanel, String mode) {
        CardLayout cardLayout = (CardLayout) modePanel.getLayout();
        cardLayout.show(modePanel, mode);
    }
}


