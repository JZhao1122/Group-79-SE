package DeepManage_Front;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private JTable transactionsTable;

    // 构造函数：将 transactionsTable 作为参数传递
    public ButtonEditor(JCheckBox checkBox, JTable transactionsTable) {
        super(checkBox);
        this.transactionsTable = transactionsTable;  // 保存对表格的引用
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = transactionsTable.getSelectedRow();  // 获取选中行
                String currentCategory = (String) transactionsTable.getValueAt(row, 4);  // 获取当前分类
                String newCategory = JOptionPane.showInputDialog("Enter new category for " + transactionsTable.getValueAt(row, 1), currentCategory);
                if (newCategory != null && !newCategory.equals(currentCategory)) {
                    transactionsTable.setValueAt(newCategory, row, 4);  // 更新表格中的用户分类
                }
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}

