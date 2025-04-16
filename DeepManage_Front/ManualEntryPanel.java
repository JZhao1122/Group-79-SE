package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ManualEntryPanel extends JPanel {
    public ManualEntryPanel() {
        setLayout(new BorderLayout());

        // 标题
        JLabel title = new JLabel("Manual Entry", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        // 表单区域
        JPanel formPanel = new JPanel(new GridLayout(5, 2));

        formPanel.add(new JLabel("Amount:"));
        JTextField amountField = new JTextField();
        formPanel.add(amountField);

        formPanel.add(new JLabel("Date:"));
        JTextField dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Description:"));
        JTextField descriptionField = new JTextField();
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Payment Method:"));
        JComboBox<String> paymentMethodCombo = new JComboBox<>(new String[] {"WeChat Pay", "Alipay", "Bank of China"});
        formPanel.add(paymentMethodCombo);

        add(formPanel, BorderLayout.CENTER);

        // 提交按钮
        JPanel controlPanel = new JPanel();
        JButton saveButton = new JButton("Save Transaction");
        controlPanel.add(saveButton);
        add(controlPanel, BorderLayout.SOUTH);

        // 按钮事件处理
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amount = amountField.getText();
                String date = dateField.getText();
                String description = descriptionField.getText();
                String paymentMethod = (String) paymentMethodCombo.getSelectedItem();

                // 这里可以加上保存逻辑，例如提交到数据库或显示成功提示
                JOptionPane.showMessageDialog(null, "Transaction Saved: " + amount + ", " + date + ", " + description + ", " + paymentMethod);
            }
        });
    }
}

