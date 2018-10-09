package org.piaohao.redisManager;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Data;
import org.piaohao.redisManager.table.NormalTableModel;
import org.piaohao.redisManager.table.TableData;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
public class MainPanel {
    private JComboBox<String> dbCombo;
    private JTextField keyCountField;
    private JTextField searchInput;
    private JButton searchBtn;
    private JPanel rootPane;
    private JList<String> keyListView;
    private JTabbedPane tabbedPane1;
    private JLabel realInfoToolLabel;
    private JPanel toolLabelPanel1;
    private JPanel toolLabelPanel2;
    private JLabel terminalToolLabel;
    private JPanel toolLabelPanel3;
    private JLabel clientToolLabel;
    private JTable keyTable;
    private JButton firstPageBtn;
    private JButton prePageBtn;
    private JButton nextPageBtn;
    private JButton lastPageBtn;
    private JButton refreshSizeBtn;
    private JLabel pageInfoLabel;
    private JTextField pageField;
    private JPanel dashboardTab;
    private JLabel addressLabel;
    private JButton createBtn;
    private JButton deleteBtn;

    private DashboardPanel dashboardPanel;
    private JFrame window;

    private BiMap<String, Object> tabMap = HashBiMap.create();
    private String columnNames[] = {"", "类型", "键名"};
    private TableData<String> tableData = null;

    public MainPanel(JFrame frame) {
        this.window = frame;
    }

    public void init() {
        refreshSizeBtn.addActionListener(e -> refreshDbSize());
        searchBtn.addActionListener(e -> doSearch());
        searchInput.requestFocusInWindow();
        searchInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSearch();
                }
            }
        });
        createBtn.addActionListener(e -> {
            JDialog dialog = new JDialog(window, "新建Key", true);
            dialog.setContentPane(new KeyValueForm(dialog).getRootPanel());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        });
        deleteBtn.addActionListener(e -> deleteKeys());

        refreshDbSize();

        initToolPanel(toolLabelPanel1, realInfoToolLabel, 1);
        initToolPanel(toolLabelPanel2, terminalToolLabel, 2);
        initToolPanel(toolLabelPanel3, clientToolLabel, 3);

        initTable();
        initDashboard();
    }

    private void deleteKeys() {
        int[] rows = keyTable.getSelectedRows();
        List<String> keys = Arrays.stream(rows)
                .mapToObj(i -> (String) keyTable.getValueAt(i, 2))
                .collect(Collectors.toList());
        String[] keysArr = keys.toArray(new String[]{});
        int tag = JOptionPane.showConfirmDialog(window, JSONUtil.toJsonPrettyStr(keysArr), "是否确认删除", JOptionPane.OK_CANCEL_OPTION);
        if (tag == JOptionPane.CANCEL_OPTION) {
            return;
        }
        RedisManager.client.KEYS.del(keysArr);
    }

    private void initDashboard() {
        dashboardPanel = new DashboardPanel();
        dashboardTab.add(dashboardPanel.getRootPanel(), BorderLayout.CENTER);
        dashboardPanel.getVersionLabel().setText("版本号:  " + RedisManager.version);
        dashboardPanel.getDbCountLabel().setText("DB数:  " + RedisManager.dbCount + "");
        dashboardPanel.getKeyCountLabel().setText("Key总数:  " + RedisManager.keyCount + "");
        dashboardPanel.getRunModelLabel().setText("运行模式:  " + (RedisManager.runModel == 0 ? "单机" : "集群"));
        dashboardPanel.getPortLabel().setText("服务监听端口:  " + RedisManager.realPort + "");
        long days = TimeUnit.SECONDS.toDays(RedisManager.uptime);
        long hours = TimeUnit.SECONDS.toHours(RedisManager.uptime - TimeUnit.DAYS.toSeconds(days));
        long minutes = TimeUnit.SECONDS.toMinutes(RedisManager.uptime - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours));
        dashboardPanel.getUptimeLabel().setText("已运行时间:  " + days + "天" + hours + "时" + minutes + "分");
    }

    private void refreshDbSize() {
        keyCountField.setText("Keys:" + RedisManager.keyCount);
    }

    private void initTableModel(int rows) {
        keyTable.setModel(new NormalTableModel(columnNames, rows));
        {
            TableColumn column0 = keyTable.getColumnModel().getColumn(0);
            column0.setPreferredWidth(50);
            column0.setMinWidth(50);
            column0.setMaxWidth(50);
            DefaultTableCellRenderer backGroundColor = new DefaultTableCellRenderer();
            backGroundColor.setBackground(new Color(237, 237, 237));
            backGroundColor.setHorizontalAlignment(SwingConstants.CENTER);
            column0.setCellRenderer(backGroundColor);
        }
        {
            TableColumn column1 = keyTable.getColumnModel().getColumn(1);
            column1.setPreferredWidth(80);
            column1.setMinWidth(80);
            column1.setMaxWidth(80);
            DefaultTableCellRenderer backGroundColor = new DefaultTableCellRenderer();
            backGroundColor.setBackground(new Color(118, 190, 71));
            column1.setCellRenderer(backGroundColor);
        }
    }

    private void initTable() {
        initTableModel(0);
        final MainPanel self = this;
        keyTable.addMouseListener(new KeyTableMouseAdapter(self));
        firstPageBtn.addActionListener(e -> refreshTableData(1));
        lastPageBtn.addActionListener(e -> refreshTableData(tableData.getPageCount()));
        prePageBtn.addActionListener(e -> refreshTableData(tableData.getCurrentPage() - 1));
        nextPageBtn.addActionListener(e -> refreshTableData(tableData.getCurrentPage() + 1));
        JPopupMenu menu = new JPopupMenu();
        JMenuItem deleteMenuItem = new JMenuItem("删除");
        deleteMenuItem.addActionListener(e -> deleteKeys());
        menu.add(deleteMenuItem);
        keyTable.setComponentPopupMenu(menu);
    }

    private void initCombobox() {
        List<String> dbNames = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            dbNames.add("DB" + i);
        }
        String[] dbArr = new String[16];
        dbCombo = new JComboBox<>(dbNames.toArray(dbArr));
    }

    private void initToolPanel(JPanel toolLabelPanel1, JLabel toolLabel1, int type) {
        MainPanel self = this;
        Color hoverBgColor = new Color(76, 76, 76);
        Color hoverFgColor = new Color(247, 133, 58);
        Color normalBgColor = new Color(47, 59, 83);
        Color normalFgColor = new Color(244, 245, 246);
        toolLabelPanel1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                toolLabelPanel1.setBackground(hoverBgColor);
                toolLabelPanel1.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, hoverFgColor));
                toolLabel1.setForeground(hoverFgColor);
                toolLabelPanel1.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                toolLabelPanel1.setBackground(normalBgColor);
                toolLabelPanel1.setBorder(BorderFactory.createEmptyBorder());
                toolLabel1.setForeground(normalFgColor);
                toolLabelPanel1.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                switch (type) {
                    case 1:
                        break;
                    case 2: {
                        if (!RedisManager.start) {
                            JOptionPane.showMessageDialog(rootPane, "请先连接redis");
                            return;
                        }
                        Object clientTab = tabMap.get("Terminal");
                        if (clientTab == null) {
                            clientTab = new TerminalPanel(window, self);
                            tabMap.put("Terminal", clientTab);
                            tabbedPane1.addTab("命令终端", ((TerminalPanel) clientTab).getRootPanel());
                        }
                        TerminalPanel tab = (TerminalPanel) clientTab;
                        tabbedPane1.setSelectedComponent(tab.getRootPanel());
                    }
                    break;
                    case 3: {
                        Object clientTab = tabMap.get("ClientList");
                        if (clientTab == null) {
                            clientTab = new ClientListPanel(window, self);
                            tabMap.put("ClientList", clientTab);
                            tabbedPane1.addTab("会话管理", ((ClientListPanel) clientTab).getRootPanel());
                        }
                        ClientListPanel tab = (ClientListPanel) clientTab;
                        tabbedPane1.setSelectedComponent(tab.getRootPanel());
                    }
                    break;
                }
            }
        });
    }

    private void createUIComponents() {
        initCombobox();

        {
            pageField = new JTextField();
//            pageSpinner.addChangeListener(e -> refreshTableData(Convert.toInt(pageSpinner.getNextValue())));
            pageField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    int keyChar = e.getKeyChar();
                    if (keyChar >= KeyEvent.VK_0 && keyChar <= KeyEvent.VK_9) {

                    } else {
                        e.consume(); //关键，屏蔽掉非法输入
                    }

                }

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (tableData == null) {
                            return;
                        }
                        Integer pageNo = Convert.toInt(pageField.getText());
                        if (pageNo > tableData.getPageCount()) {
                            pageNo = tableData.getPageCount();
                        } else if (pageNo < 1) {
                            pageNo = 1;
                        }
                        refreshTableData(pageNo);
                        pageField.setText(pageNo + "");
                    }
                }
            });
        }
    }

    private void doSearch() {
        String query = searchInput.getText();
        if (StrUtil.isBlank(query)) {
//            JOptionPane.showMessageDialog(rootPane, "查询条件不能为空");
//            return;
            query = "*";
        }
        if (!RedisManager.start) {
            JOptionPane.showMessageDialog(rootPane, "请先连接redis");
            return;
        }
        String finalQuery = query;
        new Thread(() -> {
            if (!RedisManager.start) {
                JOptionPane.showMessageDialog(rootPane, "请先连接redis");
                return;
            }
            tableData = RedisManager.query(finalQuery);
            refreshTableData(1);
        }).start();
    }

    private void refreshTableData(int pageNo) {
        List<String> keys = tableData.getPageData(pageNo);
        initTableModel(keys.size() >= 50 ? 50 : keys.size());
        int i = 0;
        for (String key : keys) {
            keyTable.getModel().setValueAt(i + 1, i, 0);
            keyTable.getModel().setValueAt(RedisManager.client.KEYS.type(key), i, 1);
            keyTable.getModel().setValueAt(key, i, 2);
            if (i++ >= 49) {
                break;
            }
        }
        tableData.setCurrentPage(pageNo);
        pageField.setText(pageNo + "");
        pageInfoLabel.setText("页,共" + tableData.getPageCount() + "页");
    }

    public void refreshInfo() {
        keyCountField.setText("Keys:" + RedisManager.keyCount);

        dashboardPanel.getVersionLabel().setText("版本号:  " + RedisManager.version);
        dashboardPanel.getDbCountLabel().setText("DB数:  " + RedisManager.dbCount + "");
        dashboardPanel.getKeyCountLabel().setText("Key总数:  " + RedisManager.keyCount + "");
        dashboardPanel.getRunModelLabel().setText("运行模式:  " + (RedisManager.runModel == 0 ? "单机" : "集群"));
        dashboardPanel.getPortLabel().setText("服务监听端口:  " + RedisManager.realPort + "");
        long days = TimeUnit.SECONDS.toDays(RedisManager.uptime);
        long hours = TimeUnit.SECONDS.toHours(RedisManager.uptime - TimeUnit.DAYS.toSeconds(days));
        long minutes = TimeUnit.SECONDS.toMinutes(RedisManager.uptime - TimeUnit.DAYS.toSeconds(days) - TimeUnit.HOURS.toSeconds(hours));
        dashboardPanel.getUptimeLabel().setText("已运行时间:  " + days + "天" + hours + "时" + minutes + "分");
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
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
        rootPane = new JPanel();
        rootPane.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        rootPane.setBackground(new Color(-65794));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(5, 5, 5, 5), -1, -1));
        panel1.setBackground(new Color(-13681837));
        rootPane.add(panel1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 50), null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        toolLabelPanel1 = new JPanel();
        toolLabelPanel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        toolLabelPanel1.setBackground(new Color(-13681837));
        panel1.add(toolLabelPanel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        realInfoToolLabel = new JLabel();
        realInfoToolLabel.setForeground(new Color(-723466));
        realInfoToolLabel.setText("实时性能");
        toolLabelPanel1.add(realInfoToolLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 50), null, 0, false));
        toolLabelPanel2 = new JPanel();
        toolLabelPanel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        toolLabelPanel2.setBackground(new Color(-13681837));
        panel1.add(toolLabelPanel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        terminalToolLabel = new JLabel();
        terminalToolLabel.setForeground(new Color(-723466));
        terminalToolLabel.setText("命令终端");
        toolLabelPanel2.add(terminalToolLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        toolLabelPanel3 = new JPanel();
        toolLabelPanel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        toolLabelPanel3.setBackground(new Color(-13681837));
        panel1.add(toolLabelPanel3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        clientToolLabel = new JLabel();
        clientToolLabel.setForeground(new Color(-723466));
        clientToolLabel.setText("会话管理");
        toolLabelPanel3.add(clientToolLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addressLabel = new JLabel();
        addressLabel.setForeground(new Color(-723466));
        addressLabel.setText("");
        panel1.add(addressLabel, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        rootPane.add(spacer2, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JSplitPane splitPane1 = new JSplitPane();
        rootPane.add(splitPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        splitPane1.setLeftComponent(panel2);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(5, 5, 5, 5), -1, -1));
        panel3.setBackground(new Color(-65794));
        panel2.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(-1, 30), new Dimension(-1, 30), 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-2236963)), null));
        final JLabel label1 = new JLabel();
        label1.setText("对象列表");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 3, new Insets(5, 5, 5, 5), -1, -1));
        panel4.setBackground(new Color(-65794));
        panel2.add(panel4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(-1, 30), null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-2236963)), null));
        panel4.add(dbCombo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyCountField = new JTextField();
        keyCountField.setEditable(false);
        panel4.add(keyCountField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        refreshSizeBtn = new JButton();
        refreshSizeBtn.setText("刷新");
        panel4.add(refreshSizeBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(5, 5, 5, 5), -1, -1));
        panel5.setBackground(new Color(-65794));
        panel2.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, new Dimension(-1, 30), null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-2236963)), null));
        searchInput = new JTextField();
        searchInput.setFocusCycleRoot(false);
        searchInput.setFocusTraversalPolicyProvider(false);
        panel5.add(searchInput, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        searchBtn = new JButton();
        searchBtn.setText("查询");
        panel5.add(searchBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(panel6, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel6.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        keyTable = new JTable();
        scrollPane1.setViewportView(keyTable);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 5, new Insets(5, 5, 5, 5), -1, -1));
        panel2.add(panel7, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        firstPageBtn = new JButton();
        firstPageBtn.setText("首页");
        panel7.add(firstPageBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        prePageBtn = new JButton();
        prePageBtn.setText("上一页");
        panel7.add(prePageBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel7.add(panel8, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("第");
        panel8.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pageInfoLabel = new JLabel();
        pageInfoLabel.setText("页,共0页");
        panel8.add(pageInfoLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pageField.setText("0");
        panel8.add(pageField, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(100, -1), null, 0, false));
        nextPageBtn = new JButton();
        nextPageBtn.setText("下一页");
        panel7.add(nextPageBtn, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lastPageBtn = new JButton();
        lastPageBtn.setText("尾页");
        panel7.add(lastPageBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 3, new Insets(5, 5, 5, 5), -1, -1));
        panel9.setBackground(new Color(-723466));
        panel2.add(panel9, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panel9.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-2236963)), null));
        createBtn = new JButton();
        createBtn.setText("新建");
        panel9.add(createBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteBtn = new JButton();
        deleteBtn.setText("删除");
        panel9.add(deleteBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel9.add(spacer3, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        tabbedPane1 = new JTabbedPane();
        splitPane1.setRightComponent(tabbedPane1);
        dashboardTab = new JPanel();
        dashboardTab.setLayout(new BorderLayout(0, 0));
        tabbedPane1.addTab("首页", dashboardTab);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPane;
    }

    private class KeyTableMouseAdapter extends MouseAdapter {
        private final MainPanel mainPanel;

        public KeyTableMouseAdapter(MainPanel mainPanel) {
            this.mainPanel = mainPanel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && RedisManager.start) {
                Point p = e.getPoint();
                int row = keyTable.rowAtPoint(p);
                int column = keyTable.columnAtPoint(p);
                //从而获得双击时位于的单元格
                Object value = keyTable.getModel().getValueAt(row, 2);
                if (value == null) {
                    return;
                }
                String title = (String) value;
                Object keyValueTab = tabMap.get("键值详情");
                if (keyValueTab == null) {
                    keyValueTab = new KeyDetailPanel(mainPanel);
//                        String dbName = (String) dbCombo.getSelectedItem();
                    tabMap.put("键值详情", keyValueTab);
                    tabbedPane1.addTab("键值详情", ((KeyDetailPanel) keyValueTab).getRootPanel());
                }
                KeyDetailPanel tab = (KeyDetailPanel) keyValueTab;
                tab.getKeyTextFiled().setText(title);
                tab.getKeyTextFiled().setCaretPosition(0);

                String type = (String) keyTable.getModel().getValueAt(row, 1);
                tab.getTypeLabel().setText(type);
                tab.getExpireInput().setText(RedisManager.client.KEYS.ttl(title) + "");
                tabbedPane1.setSelectedComponent(tab.getRootPanel());

                JPanel contentPanel = tab.getContentPanel();
                switch (type) {
                    case "string": {
                        StringPanel detailPanel = new StringPanel(window, title);
                        detailPanel.getValueArea().setText(RedisManager.client.STRINGS.get(title));
                        JPanel rootPanel = detailPanel.getRootPanel();
                        contentPanel.removeAll();
                        contentPanel.setLayout(new BorderLayout());
                        contentPanel.add(rootPanel, BorderLayout.CENTER);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    break;
                    case "list": {
                        contentPanel.removeAll();
                        contentPanel.setLayout(new BorderLayout());
                        contentPanel.add(new ListPanel(window, title).getRootPanel(), BorderLayout.CENTER);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    break;
                    case "hash": {
                        contentPanel.removeAll();
                        contentPanel.setLayout(new BorderLayout());
                        contentPanel.add(new HashPanel(window, title).getRootPanel(), BorderLayout.CENTER);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    break;
                    case "zset": {
                        List<String> values = RedisManager.zsetMembers(title);
                        String[] cNames = {"", "Value"};
                        JTable listTable = new JTable(new DefaultTableModel(cNames, values.size()));
                        listTable.getColumnModel().getColumn(0).setPreferredWidth(50);
                        listTable.getColumnModel().getColumn(0).setMinWidth(50);
                        listTable.getColumnModel().getColumn(0).setMaxWidth(50);
                        int i = 0;
                        for (String v : values) {
                            listTable.setValueAt(i + 1, i, 0);
                            listTable.setValueAt(v, i++, 1);
                        }
                        JScrollPane listRootPanel = new JScrollPane(listTable);
                        contentPanel.removeAll();
                        contentPanel.setLayout(new BorderLayout());
                        contentPanel.add(listRootPanel, BorderLayout.CENTER);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    break;
                    case "set": {
                        contentPanel.removeAll();
                        contentPanel.setLayout(new BorderLayout());
                        contentPanel.add(new SetPanel(window, title).getRootPanel(), BorderLayout.CENTER);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    break;
                }
            }
        }
    }
}
