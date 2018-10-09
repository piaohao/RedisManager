package org.piaohao.redisManager;

import cn.hutool.core.util.StrUtil;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.label.WebLabel;
import com.google.common.collect.Lists;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Data;
import org.piaohao.redisManager.table.ClientTableInit;
import org.piaohao.redisManager.table.TableData;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

@Data
public class ClientListPanel {
    private JPanel rootPanel;
    private JPanel topPanel;
    private JPanel titlePanelWrapper;
    private JButton refreshBtn;
    private JButton killBtn;
    private JButton infoBtn;
    private JTable clientTable;
    private JButton closeBtn;
    private JLabel clientCountLabel;
    private TablePanel tablePanel;
    private JPanel tablePanelWrapper;

    private JFrame frame;

    public ClientListPanel(JFrame frame, MainPanel mainPanel) {
        this.frame = frame;
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(221, 221, 221)));
        titlePanelWrapper.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(136, 183, 224)));

        closeBtn.addActionListener(e -> {
            mainPanel.getTabMap().remove("ClientList");
            mainPanel.getTabbedPane1().remove(rootPanel);
        });
        refreshBtn.addActionListener(e -> tablePanel.refresh());
        infoBtn.addActionListener(e -> {
            final WebPopOver popOver = new WebPopOver(infoBtn);
            popOver.setCloseOnFocusLoss(true);
            popOver.setLayout(new BorderLayout(10, 0));
            popOver.add(new WebLabel(html), BorderLayout.CENTER);
            popOver.show(infoBtn);
        });
        killBtn.addActionListener(e -> {
            int[] rows = clientTable.getSelectedRows();
            if (rows.length == 0) {
                JOptionPane.showMessageDialog(frame, "请选择要终止掉的客户端");
                return;
            }
            Arrays.stream(rows).forEach(r -> RedisManager.killClient(((String) clientTable.getValueAt(r, 2))));
            JOptionPane.showMessageDialog(frame, "kill成功");
        });
        tablePanel = new TablePanel<>(frame, new TableData<>(refreshClientList()), new ClientTableInit(this));
        tablePanelWrapper.add(tablePanel.getRootPanel(), BorderLayout.CENTER);
    }

    public List<String> refreshClientList() {
        if (!RedisManager.start) {
            JOptionPane.showMessageDialog(frame, "请选择会话");
            return Lists.newArrayList();
        }
        String clientListStr = RedisManager.clientList();
        List<String> clients = StrUtil.split(clientListStr, '\n');
        clientCountLabel.setText("总连接数: " + clients.size());
        return clients;
    }

    //language=HTML
    String html = "<html>\n" +
            "<body>\n" +
            "以下是域的含义：\n" +
            "<table>\n" +
            "    <tr><td>addr ：</td><td>客户端的地址和端口</td></tr>\n" +
            "    <tr><td>fd ：</td><td>套接字所使用的文件描述符</td></tr>\n" +
            "    <tr><td>age ：</td><td>以秒计算的已连接时长</td></tr>\n" +
            "    <tr><td>idle ：</td><td>以秒计算的空闲时长</td></tr>\n" +
            "    <tr><td>flags ：</td><td>客户端 flag （见下文</td></tr>\n" +
            "    <tr><td>db ：</td><td>该客户端正在使用的数据库 ID</td></tr>\n" +
            "    <tr><td>sub ：</td><td>已订阅频道的数量</td></tr>\n" +
            "    <tr><td>psub ：</td><td>已订阅模式的数量</td></tr>\n" +
            "    <tr><td>multi ：</td><td>在事务中被执行的命令数量</td></tr>\n" +
            "    <tr><td>qbuf ：</td><td>查询缓存的长度（ 0 表示没有查询在等待）</td></tr>\n" +
            "    <tr><td>qbuf-free ：</td><td>查询缓存的剩余空间（ 0 表示没有剩余空间）</td></tr>\n" +
            "    <tr><td>obl ：</td><td>输出缓存的长度</td></tr>\n" +
            "    <tr><td>oll ：</td><td>输出列表的长度（当输出缓存没有剩余空间时，回复被入队到这个队列里）</td></tr>\n" +
            "    <tr><td>omem ：</td><td>输出缓存的内存占用量</td></tr>\n" +
            "    <tr><td>events ：</td><td>文件描述符事件（见下文）</td></tr>\n" +
            "    <tr><td>cmd ：</td><td>最近一次执行的命令</td></tr>\n" +
            "</table>\n" +
            "客户端 flag 可以由以下部分组成：\n" +
            "<table>\n" +
            "    <tr><td>O ：</td><td>客户端是 MONITOR 模式下的附属节点（slave）</td></tr>\n" +
            "    <tr><td>S ：</td><td>客户端是一般模式下（normal）的附属节点</td></tr>\n" +
            "    <tr><td>M ：</td><td>客户端是主节点（master）</td></tr>\n" +
            "    <tr><td>x ：</td><td>客户端正在执行事务</td></tr>\n" +
            "    <tr><td>b ：</td><td>客户端正在等待阻塞事件</td></tr>\n" +
            "    <tr><td>i ：</td><td>客户端正在等待 VM I/O 操作（已废弃）</td></tr>\n" +
            "    <tr><td>d ：</td><td>一个受监视（watched）的键已被修改， EXEC 命令将失败</td></tr>\n" +
            "    <tr><td>c ：</td><td>在将回复完整地写出之后，关闭链接</td></tr>\n" +
            "    <tr><td>u ：</td><td>客户端未被阻塞（unblocked）</td></tr>\n" +
            "    <tr><td>A ：</td><td>尽可能快地关闭连接</td></tr>\n" +
            "    <tr><td>N ：</td><td>未设置任何 flag</td></tr>\n" +
            "</table>\n" +
            "文件描述符事件可以是：\n" +
            "<table>\n" +
            "    <tr><td>r</td><td>客户端套接字（在事件 loop 中）是可读的（readable）</td></tr>\n" +
            "    <tr><td>w</td><td>客户端套接字（在事件 loop 中）是可写的（writeable）</td></tr>\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>";

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
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 3, new Insets(10, 10, 10, 10), -1, -1));
        topPanel.setBackground(new Color(-65794));
        rootPanel.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        titlePanelWrapper = new JPanel();
        titlePanelWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 20, 0, 0), -1, -1));
        titlePanelWrapper.setBackground(new Color(-65794));
        topPanel.add(titlePanelWrapper, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-14930880));
        label1.setText("会话管理");
        titlePanelWrapper.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        topPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        closeBtn = new JButton();
        closeBtn.setText("关闭");
        topPanel.add(closeBtn, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 5, new Insets(10, 10, 10, 10), -1, -1));
        panel1.setBackground(new Color(-657931));
        rootPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        refreshBtn = new JButton();
        refreshBtn.setText("刷新");
        panel1.add(refreshBtn, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        killBtn = new JButton();
        killBtn.setText("Kill");
        panel1.add(killBtn, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        infoBtn = new JButton();
        infoBtn.setText("参数说明");
        panel1.add(infoBtn, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        clientCountLabel = new JLabel();
        clientCountLabel.setText("Label");
        panel1.add(clientCountLabel, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tablePanelWrapper = new JPanel();
        tablePanelWrapper.setLayout(new BorderLayout(0, 0));
        rootPanel.add(tablePanelWrapper, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
