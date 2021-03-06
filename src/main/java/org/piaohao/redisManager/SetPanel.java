package org.piaohao.redisManager;

import cn.hutool.core.util.StrUtil;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Data;
import org.piaohao.redisManager.table.ListSetTableModel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.Arrays;
import java.util.Set;

@Data
public class SetPanel {
    private WebButton refreshBtn;
    private WebButton insertBtn;
    private WebButton deleteBtn;
    private WebLabel countLabel;
    private JPanel tablePanelWrapper;
    private JPanel rootPanel;
    private JTable table;
    private JScrollPane tableScrollPanel;

    private JFrame frame;
    private TablePanel<String> tablePanel;
    private String key;

    private String[] colNames = {"", "值"};

    public SetPanel(JFrame frame, String key) {
        this.frame = frame;
        this.key = key;

        $$$setupUI$$$();
        refresh(true);
        table.getModel().addTableModelListener(e -> updateKeyValue(table, e.getFirstRow(), e.getColumn()));
    }

    private void updateKeyValue(JTable table, int row, int column) {
        if (column < 0) {
            return;
        }
        String setValue = (String) table.getValueAt(row, column);
        String index = (String) table.getValueAt(row, column - 1);
        if (StrUtil.isBlank(index)) {
            RedisManager.client.SETS.sadd(key, setValue);
            JOptionPane.showMessageDialog(frame, "保存成功");
        } else {
            JOptionPane.showMessageDialog(frame, "暂不支持更改元素，请进行新增操作");
        }
        refresh(false);
    }

    private void createUIComponents() {
        refreshBtn = new WebButton("刷新");
        refreshBtn.addActionListener(e -> refresh(false));
        insertBtn = new WebButton("新增");
        insertBtn.addActionListener(e -> {
            ((DefaultTableModel) table.getModel()).insertRow(0, new String[2]);
            table.scrollRectToVisible(table.getCellRect(table.getRowCount() - 1, 0, true));
        });
        deleteBtn = new WebButton("删除");
        deleteBtn.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            Arrays.stream(rows)
                    .forEach(r -> {
                        String value = (String) table.getValueAt(r, 1);
                        RedisManager.client.SETS.srem(key, value);
                    });
            JOptionPane.showMessageDialog(frame, "删除成功");
            refresh(false);
        });
        countLabel = new WebLabel("");
    }

    private void refresh(boolean firstTime) {
        Set<String> values = RedisManager.client.SETS.smembers(key);
        long total = values.size();
        countLabel.setText("总数：" + total);
        if (firstTime) {
            table = new JTable(new ListSetTableModel(colNames, values.size()));
            tableScrollPanel = new JScrollPane(table);
            tablePanelWrapper.add(tableScrollPanel, BorderLayout.CENTER);
        } else {
            table.setModel(new ListSetTableModel(colNames, values.size()));
        }
        int i = 0;
        for (String value : values) {
            table.setValueAt(i + 1, i, 0);
            table.setValueAt(value, i, 1);
            i++;
        }
        {
            TableColumn column0 = table.getColumnModel().getColumn(0);
            column0.setPreferredWidth(50);
            column0.setMinWidth(50);
            column0.setMaxWidth(50);
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(5, 5, 5, 5), -1, -1));
        rootPanel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel1.add(refreshBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(insertBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(deleteBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.add(countLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tablePanelWrapper = new JPanel();
        tablePanelWrapper.setLayout(new BorderLayout(0, 0));
        rootPanel.add(tablePanelWrapper, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
