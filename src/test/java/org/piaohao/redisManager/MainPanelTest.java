package org.piaohao.redisManager;

import com.alee.laf.WebLookAndFeel;
import com.alee.laf.button.WebButton;
import com.alee.laf.splitpane.WebSplitPane;
import org.junit.Test;

import javax.swing.*;

public class MainPanelTest {

    @Test
    public void webSplitPane() throws InterruptedException {
        WebLookAndFeel.install();
        JFrame frame = new JFrame("Redis管理工具V1.0");
        WebSplitPane splitPane = new WebSplitPane(JSplitPane.HORIZONTAL_SPLIT, new WebButton("1"), new WebButton("2"));
        splitPane.setOneTouchExpandable(true);
//        splitPane.setPreferredSize ( new Dimension ( 250, 200 ) );
        splitPane.setDividerLocation(200);
        splitPane.setContinuousLayout(true);

        frame.setContentPane(splitPane);
        int DIALOG_WHITE = 1400;//宽度
        int DIALOG_HEIGHT = 800;//高度
        frame.setSize(DIALOG_WHITE, DIALOG_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        while (true) {
            Thread.sleep(200);
        }
    }
}