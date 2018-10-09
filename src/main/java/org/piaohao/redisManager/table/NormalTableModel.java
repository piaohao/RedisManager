package org.piaohao.redisManager.table;

import javax.swing.table.DefaultTableModel;

public class NormalTableModel extends DefaultTableModel {

    public NormalTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}