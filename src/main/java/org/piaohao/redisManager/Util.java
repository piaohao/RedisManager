package org.piaohao.redisManager;

import javax.swing.*;

public class Util {

    /***
     * 增加参数后,使滚动条自动定位到底部
     */
    public static void scrollToBottom(JScrollPane scrollPane) {
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum() + 20);
//        int maxHeight = scrollPane.getVerticalScrollBar().getMaximum();
//        scrollPane.getViewport().setViewPosition(new Point(0, maxHeight + 10));
//        scrollPane.updateUI();
    }

    /***
     * 增加参数后,使滚动条自动定位到顶部
     */
    public static void scrollToTop(JScrollPane scrollPane) {
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMinimum());
//        int minHeight = scrollPane.getVerticalScrollBar().getMinimum();
//        scrollPane.getViewport().setViewPosition(new Point(0, minHeight));
//        scrollPane.updateUI();
    }

}
