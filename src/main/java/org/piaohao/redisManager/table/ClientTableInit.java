package org.piaohao.redisManager.table;

import cn.hutool.core.util.StrUtil;
import org.piaohao.redisManager.ClientListPanel;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientTableInit implements TableInit<String> {

    private String[] colNames = {"", "id", "addr", "fd", "name", "age", "idle", "flags", "db", "sub", "psub", "multi", "qbuf", "qbuf-free", "obl", "oll", "omem", "events", "cmd"};
    private ClientListPanel clientListPanel;

    public ClientTableInit(ClientListPanel clientListPanel) {
        this.clientListPanel = clientListPanel;
    }

    @Override
    public void init(JTable table, List<String> rows) {
        table.setModel(new NormalTableModel(colNames, rows.size()));
        int i = 0;
        for (String client : rows) {
            if (StrUtil.isBlank(client)) {
                continue;
            }
            Map<String, String> info = StrUtil.split(client, ' ')
                    .stream()
                    .map(s -> StrUtil.split(s, '='))
                    .collect(Collectors.toMap(
                            s1 -> s1.get(0), s1 -> s1.get(1)
                    ));
            int j = 0;
            for (String colName : colNames) {
                if (j == 0) {
                    table.setValueAt(i + 1, i, j);
                } else {
                    table.setValueAt(info.get(colName), i, j);
                }
                j++;
            }
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
            column0.setPreferredWidth(65);
            column0.setMinWidth(65);
            column0.setMaxWidth(65);
        }
        {
            TableColumn column0 = table.getColumnModel().getColumn(2);
            column0.setPreferredWidth(140);
            column0.setMinWidth(140);
            column0.setMaxWidth(140);
        }
    }

    @Override
    public TableData<String> refresh() {
        return new TableData<>(clientListPanel.refreshClientList());
    }
}