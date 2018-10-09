package org.piaohao.redisManager;

import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SessionServiceTest {

    @Test
    public void test() throws Exception {
        Class.forName("org.sqlite.JDBC");

        Connection conn = DriverManager.getConnection("jdbc:sqlite:test.db");

        Statement stat = conn.createStatement();
        stat.executeUpdate("drop table if exists people;");
        stat.executeUpdate("create table people (name, occupation);");
        PreparedStatement prep = conn
                .prepareStatement("insert into people values (?, ?);");

        prep.setString(1, "G");
        prep.setString(2, "politics");
        prep.addBatch();
        prep.setString(1, "Turing");
        prep.setString(2, "computers");
        prep.addBatch();
        prep.setString(1, "W");
        prep.setString(2, "Tester");
        prep.addBatch();

        conn.setAutoCommit(false);
        prep.executeBatch();
        conn.setAutoCommit(true);

        ResultSet rs = stat.executeQuery("select * from people;");
        while (rs.next()) {
            System.out.println("name = " + rs.getString("name"));
            System.out.println("job = " + rs.getString("occupation"));
        }
        rs.close();
        conn.close();
    }

    @Test
    public void split() {
        String s = "a=";
        List<String> list = StrUtil.split(s, '=');
        assertEquals("期望大小为2", 2, list.size());
    }

    @Test
    public void socket() throws IOException, InterruptedException {
        RedisClient client = new RedisClient("10.1.1.56", 6379, "123456", System.out::println);
        new Thread(client).start();
//        client.send("auth 123456");
        client.send("info");
        client.send("dbsize");
        client.send("get mytest");
        client.send("hgetall hashtest");
        while (true) {
            Thread.sleep(10);
        }
    }

    @Test
    public void length() {
        String s = "12345\r\n";
        System.out.println(s.length());
    }

}