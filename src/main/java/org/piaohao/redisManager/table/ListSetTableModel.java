package org.piaohao.redisManager.table;

import javax.swing.table.DefaultTableModel;

public class ListSetTableModel extends DefaultTableModel {

    public ListSetTableModel(Object[] columnNames, int rowCount) {
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