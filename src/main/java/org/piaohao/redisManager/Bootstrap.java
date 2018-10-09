package org.piaohao.redisManager;

import com.alee.laf.WebLookAndFeel;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bootstrap {

    private static void initGlobalFont() {
        FontUIResource fontUIResource = new FontUIResource(new Font("微软雅黑", Font.PLAIN, 12));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

    public static void main(String[] args) {
        initGlobalFont();
        SwingUtilities.invokeLater(() -> {
            WebLookAndFeel.install();
            JFrame frame = new JFrame("Redis管理工具V1.0");
            MainPanel mainPanel = new MainPanel(frame);
            mainPanel.init();
            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.scheduleAtFixedRate(mainPanel::refreshInfo, 0, 2, TimeUnit.SECONDS);
            frame.setContentPane(mainPanel.getRootPane());

            JMenuBar menuBar = new JMenuBar();
            {
                JMenu menu = new JMenu("文件");
                /*JMenuItem item1 = new JMenuItem("新建会话");
                item1.addActionListener(e -> {
                    JDialog dialog = new JDialog(frame, "新建会话", true);
                    dialog.setContentPane(new SessionForm(dialog).getRootPanel());
                    dialog.setSize(400, 300);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);
                });
                menu.add(item1);*/
                JMenuItem item2 = new JMenuItem("打开会话");
                item2.addActionListener(e -> {
                    /*JDialog dialog = new JDialog(frame, "会话列表", true);
                    dialog.setContentPane(new SessionListForm(dialog, mainPanel).getRootPanel());
                    dialog.setSize(400, 300);
                    dialog.setLocationRelativeTo(null);
                    dialog.setVisible(true);*/
                    SessionDialog2 sessionDialog = new SessionDialog2(frame, mainPanel);
                    sessionDialog.setSize(400, 300);
                    sessionDialog.setLocationRelativeTo(null);
                    sessionDialog.setVisible(true);
                });
                menu.add(item2);
                /*JMenuItem item3 = new JMenuItem("编辑会话");
                menu.add(item3);
                JMenuItem item4 = new JMenuItem("删除会话");
                menu.add(item4);*/
                JMenuItem item5 = new JMenuItem("退出");
                item5.addActionListener(e -> frame.dispose());
                menu.add(item5);
                menuBar.add(menu);
            }
            {
                JMenu menu = new JMenu("帮助");
                menuBar.add(menu);
            }
            frame.setJMenuBar(menuBar);

            int DIALOG_WHITE = 1400;//宽度
            int DIALOG_HEIGHT = 800;//高度
            frame.setSize(DIALOG_WHITE, DIALOG_HEIGHT);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setIconImage(new ImageIcon(Bootstrap.class.getResource("/images/redis.png")).getImage());
            frame.setVisible(true);
        });
    }
}
