package org.piaohao.redisManager;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Data
public class RedisClient implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    private volatile String cmd;

    private Callback callback;

    public RedisClient(String address, Integer port, String password, Callback callback) {
        this.callback = callback;
        try {
            socket = new Socket(address, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("auth " + password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String cmd) {
        this.cmd = StrUtil.split(cmd, ' ').get(0);
        writer.println(cmd);
    }

    public void run() {
        try {
            StringBuilder buffer = new StringBuilder();
            int length = 0;
            int headerLen = 0;
            String buff = "";
            int resultLength = 0;
            List<String> results = Lists.newArrayList();
            String type = "single";
            while ((buff = reader.readLine()) != null) {
                buffer.append(buff).append("\r\n");
                if (buff.startsWith("+")) {
                    /*if (!cmd.equalsIgnoreCase("auth")) {
                        callback.call(buffer.toString());
                    }*/
                    callback.call(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                } else if (buff.startsWith("-")) {
                    callback.call(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                } else if (buff.startsWith(":")) {
                    callback.call(buffer.toString());
                    buffer = new StringBuilder();
                    continue;
                } else if (buff.startsWith("$")) {
                    String str = StrUtil.subSuf(buff, 1);
                    length = Integer.parseInt(str);
                    headerLen = buff.length() + 2;
                } else if (buff.startsWith("*")) {
                    String str = StrUtil.subSuf(buff, 1);
                    resultLength = Integer.parseInt(str);
                    type = "multi";
                    buffer = new StringBuilder();
                }
                if (length == -1 && type.equals("multi")) {
                    results.add(buffer.toString());
                }
                if (length == buffer.toString().getBytes(StandardCharsets.UTF_8).length - headerLen - 2) {
                    if (type.equals("multi")) {
                        results.add(buffer.toString());
                    } else {
                        callback.call(buffer.toString());
                    }
                    buffer = new StringBuilder();
                }
                if (type.equals("multi") && results.size() == resultLength) {
                    callback.call(StrUtil.join("", results));
                    results = Lists.newArrayList();
                    resultLength = 0;
                }
            }
            System.out.println(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("problem");
        } finally {
            //最后关闭Socket
            try {
                if (socket != null) socket.close();
                if (reader != null) reader.close();
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface Callback {
        void call(String result);
    }

}