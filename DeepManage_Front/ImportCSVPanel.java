package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ImportCSVPanel extends JPanel {
    public ImportCSVPanel() {
        setLayout(new BorderLayout());

        // 标题
        JLabel title = new JLabel("Import CSV", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 拖拽区域
        JLabel dragLabel = new JLabel("Drag and drop CSV/Excel files here (≤10MB)", JLabel.CENTER);
        dragLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(dragLabel, BorderLayout.CENTER);

        // 支持平台选择
        JPanel platformPanel = new JPanel();
        JComboBox<String> platformCombo = new JComboBox<>(new String[] {"WeChat Pay", "Alipay", "Bank of China"});
        platformPanel.add(new JLabel("Supported Platforms"));
        platformPanel.add(platformCombo);
        add(platformPanel, BorderLayout.SOUTH);

        // 导入按钮
        JButton importButton = new JButton("Import CSV");
        platformPanel.add(importButton);

        // 导入按钮事件
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedPlatform = (String) platformCombo.getSelectedItem();
                // 这里可以加入文件选择逻辑，或通过文件选择对话框来实现
                JOptionPane.showMessageDialog(null, "Importing CSV for " + selectedPlatform);
            }
        });
    }
}

