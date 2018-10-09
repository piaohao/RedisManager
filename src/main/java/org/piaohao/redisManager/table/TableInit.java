package org.piaohao.redisManager.table;

import javax.swing.*;
import java.util.List;

public interface TableInit<T> {
    void init(JTable table, List<T> rows);

    TableData<T> refresh();
}
