package org.piaohao.redisManager.table;

import org.piaohao.redisManager.HashPanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.util.List;
import java.util.Map;

public class HashTableInit implements TableInit<Map.Entry<String, String>> {

    private String[] colNames = {"", "键", "值"};
    private HashPanel hashPanel;

    public HashTableInit(HashPanel hashPanel) {
        this.hashPanel = hashPanel;
    }

    @Override
    public void init(JTable table, List<Map.Entry<String, String>> rows) {
        table.setModel(new HashTableModel(colNames, rows.size()));
        int i = 0;
        for (Map.Entry<String, String> client : rows) {
            table.setValueAt(i + 1, i, 0);
            table.setValueAt(client.getKey(), i, 1);
            table.setValueAt(client.getValue(), i, 2);
            i++;
        }
        {
            TableColumn column0 = table.getColumnModel().getColumn(0);
            column0.setPreferredWidth(50);
            column0.setMinWidth(50);
            column0.setMaxWidth(50);
        }
        {
            TableColumn column0 = table.getColumnModel().getColumn(1);
            column0.setPreferredWidth(140);
            column0.setMinWidth(140);
            column0.setMaxWidth(140);
        }
    }

    @Override
    public TableData<Map.Entry<String, String>> refresh() {
        return hashPanel.refreshClientList();
    }

    class HashTableModel extends DefaultTableModel {

        public HashTableModel(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return false;
            }
            return true;
        }

    }
}