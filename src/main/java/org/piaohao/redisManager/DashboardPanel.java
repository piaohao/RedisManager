package org.piaohao.redisManager;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lombok.Data;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.time.Second;

import javax.swing.*;
import java.awt.*;

@Data
public class DashboardPanel {
    private JPanel rootPanel;
    private JPanel topPanel;
    private JLabel topLabel;
    private JPanel topLabelWrapper;
    private JPanel thirdPanel;
    private JPanel thirdLabelWrapper;
    private JPanel chartPanel;
    private JPanel memoryPanel;
    private JPanel keyHitPanel;
    private JPanel cpuPanel;
    private JPanel connectionPanel;
    private JLabel versionLabel;
    private JLabel dbCountLabel;
    private JLabel runModelLabel;
    private JLabel keyCountLabel;
    private JLabel portLabel;
    private JLabel uptimeLabel;

    public DashboardPanel() {
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(221, 221, 221)));
        topLabelWrapper.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(136, 183, 224)));

        thirdPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(221, 221, 221)));
        thirdLabelWrapper.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(136, 183, 224)));

        // 设置显示样式，避免中文乱码
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        standardChartTheme.setExtraLargeFont(new Font("微软雅黑", Font.BOLD, 20));
        standardChartTheme.setRegularFont(new Font("微软雅黑", Font.PLAIN, 15));
        standardChartTheme.setLargeFont(new Font("微软雅黑", Font.PLAIN, 15));
        ChartFactory.setChartTheme(standardChartTheme);

        MemoryChartFactory memoryChartFactory = new MemoryChartFactory("内存动态图", "内存", "数值(M)");
        RealTimeChartPanel memoryChartPanel = new RealTimeChartPanel(memoryChartFactory.getJFreeChart());
        memoryPanel.add(memoryChartPanel, BorderLayout.CENTER);
        (new Thread(memoryChartFactory)).start();

        KeyHitChartFactory keyHitChartFactory = new KeyHitChartFactory("Key命中率动态图", "Key命中", "数值(%)");
        RealTimeChartPanel keyHitChartPanel = new RealTimeChartPanel(keyHitChartFactory.getJFreeChart());
        keyHitPanel.add(keyHitChartPanel, BorderLayout.CENTER);
        (new Thread(keyHitChartFactory)).start();

        CpuChartFactory cpuChartFactory = new CpuChartFactory("Cpu占用率动态图", "CPU", "数值(%)");
        RealTimeChartPanel cpuChartPanel = new RealTimeChartPanel(cpuChartFactory.getJFreeChart());
        cpuPanel.add(cpuChartPanel, BorderLayout.CENTER);
        (new Thread(cpuChartFactory)).start();

        ConnectionChartFactory connectionChartFactory = new ConnectionChartFactory("连接数动态图", "连接数", "数值(个)");
        RealTimeChartPanel connectionChartPanel = new RealTimeChartPanel(connectionChartFactory.getJFreeChart());
        connectionPanel.add(connectionChartPanel, BorderLayout.CENTER);
        (new Thread(connectionChartFactory)).start();
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
        rootPanel = new JPanel();
        rootPanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        rootPanel.setBackground(new Color(-65794));
        thirdPanel = new JPanel();
        thirdPanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        thirdPanel.setBackground(new Color(-65794));
        rootPanel.add(thirdPanel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        thirdLabelWrapper = new JPanel();
        thirdLabelWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 20, 0, 0), -1, -1));
        thirdLabelWrapper.setBackground(new Color(-65794));
        thirdPanel.add(thirdLabelWrapper, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 40), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-14930880));
        label1.setText("性能监控");
        thirdLabelWrapper.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        thirdPanel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(10, 40, 10, 40), -1, -1));
        panel1.setBackground(new Color(-65794));
        rootPanel.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        versionLabel = new JLabel();
        versionLabel.setText("版本号： 2.8.19");
        panel1.add(versionLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dbCountLabel = new JLabel();
        dbCountLabel.setText("DB数： 256");
        panel1.add(dbCountLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        runModelLabel = new JLabel();
        runModelLabel.setText("运行模式： 单机");
        panel1.add(runModelLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        portLabel = new JLabel();
        portLabel.setText("服务监听端口： 6379");
        panel1.add(portLabel, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyCountLabel = new JLabel();
        keyCountLabel.setText("Key总数： 77979");
        panel1.add(keyCountLabel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uptimeLabel = new JLabel();
        uptimeLabel.setText("已运行时间： 170天 22时 26分");
        panel1.add(uptimeLabel, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        topPanel.setBackground(new Color(-65794));
        rootPanel.add(topPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        topLabelWrapper = new JPanel();
        topLabelWrapper.setLayout(new GridLayoutManager(1, 1, new Insets(0, 20, 0, 0), -1, -1));
        topLabelWrapper.setBackground(new Color(-65794));
        topPanel.add(topLabelWrapper, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(-1, 40), null, 0, false));
        topLabel = new JLabel();
        topLabel.setForeground(new Color(-14930880));
        topLabel.setText("实例基本信息");
        topLabelWrapper.add(topLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(250, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        topPanel.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(521, 11), null, 0, false));
        chartPanel = new JPanel();
        chartPanel.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
        chartPanel.setBackground(new Color(-65794));
        rootPanel.add(chartPanel, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        memoryPanel = new JPanel();
        memoryPanel.setLayout(new BorderLayout(0, 0));
        chartPanel.add(memoryPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        keyHitPanel = new JPanel();
        keyHitPanel.setLayout(new BorderLayout(0, 0));
        chartPanel.add(keyHitPanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        cpuPanel = new JPanel();
        cpuPanel.setLayout(new BorderLayout(0, 0));
        chartPanel.add(cpuPanel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        connectionPanel = new JPanel();
        connectionPanel.setLayout(new BorderLayout(0, 0));
        chartPanel.add(connectionPanel, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }

    class MemoryChartFactory extends RealTimeChartFactory {

        public MemoryChartFactory(String chartContent, String title, String yAxisName) {
            super(chartContent, title, yAxisName);
        }

        @Override
        protected void refresh() {
            this.getTimeSeries().add(new Second(), RedisManager.usedMemory);
        }
    }

    class KeyHitChartFactory extends RealTimeChartFactory {

        public KeyHitChartFactory(String chartContent, String title, String yAxisName) {
            super(chartContent, title, yAxisName);
        }

        @Override
        protected void refresh() {
            this.getTimeSeries().add(new Second(), RedisManager.hitRate);
        }
    }

    class CpuChartFactory extends RealTimeChartFactory {

        public CpuChartFactory(String chartContent, String title, String yAxisName) {
            super(chartContent, title, yAxisName);
        }

        @Override
        protected void refresh() {
            this.getTimeSeries().add(new Second(), RedisManager.cpuRate);
        }
    }

    class ConnectionChartFactory extends RealTimeChartFactory {

        public ConnectionChartFactory(String chartContent, String title, String yAxisName) {
            super(chartContent, title, yAxisName);
        }

        @Override
        protected void refresh() {
            this.getTimeSeries().add(new Second(), RedisManager.connectedClient);
        }
    }
}
