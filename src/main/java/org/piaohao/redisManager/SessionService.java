package org.piaohao.redisManager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import org.piaohao.redisManager.db.SqliteHelper;

import java.sql.*;
import java.util.List;

public class SessionService {

    private static SessionService sessionService = new SessionService();

    private SqliteHelper sqliteHelper;
    private static final Object lock = new Object();

    public static SessionService getInstance() {
        return sessionService;
    }

    private void ensureDb() {
        if (sqliteHelper != null) {
            return;
        }
        synchronized (lock) {
            try {
                sqliteHelper = new SqliteHelper("session.db");
                Long count = sqliteHelper.executeQuery("select count(*)  from sqlite_master where type='table' and name = 'session';",
                        rs -> {
                            try {
                                return rs.getLong(1);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                return 0L;
                            }
                        });
                if (count < 1) {
                    sqliteHelper.executeUpdate("create table session (\n" +
                            "  address varchar(100) primary key ,\n" +
                            "  port int,\n" +
                            "  password varchar(20),\n" +
                            "  updateTime datetime  default (datetime('now', 'localtime')) ,\n" +
                            "  createdTime datetime  default (datetime('now', 'localtime')) \n" +
                            ");");
                }
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (SQLException e1) {
                e1.printStackTrace();
                sqliteHelper = null;
            }
        }
    }

    private void ensureTable() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            ResultSet resultSet = stat.executeQuery("select count(*)  from sqlite_master where type='table' and name = 'session';");
            boolean exist = false;
            while (resultSet.next()) {
                long count = resultSet.getLong(1);
                if (count > 0) {
                    exist = true;
                }
            }
            if (exist) {
                return;
            }
            stat.executeUpdate("create table session (\n" +
                    "  id integer primary key autoincrement ,\n" +
                    "  address varchar(100),\n" +
                    "  port int,\n" +
                    "  password varchar(20),\n" +
                    "  remark varchar(100),\n" +
                    "  updateTime datetime  default (datetime('now', 'localtime')) ,\n" +
                    "  createdTime datetime  default (datetime('now', 'localtime')) \n" +
                    ");");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Session get(Integer id) {
        ensureTable();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            ResultSet resultSet = stat.executeQuery("select * from session where id=" + id);
            List<Session> ret = Lists.newArrayList();
            while (resultSet.next()) {
                int idTmp = resultSet.getInt("id");
                String address = resultSet.getString("address");
                int port = resultSet.getInt("port");
                String password = resultSet.getString("password");
                String remark = resultSet.getString("remark");
                ret.add(new Session(idTmp, address, port, password, remark));
            }
            if (CollUtil.isEmpty(ret)) {
                return null;
            }
            return ret.get(0);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean exist(String address, Integer port) {
        ensureTable();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            ResultSet resultSet = stat.executeQuery(StrUtil.format("select * from session where address='{}' and port={}", address, port));
            List<Session> ret = Lists.newArrayList();
            while (resultSet.next()) {
                return true;
            }
            return false;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Session> all() {
        ensureTable();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            ResultSet resultSet = stat.executeQuery("select * from session");
            List<Session> ret = Lists.newArrayList();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String address = resultSet.getString("address");
                int port = resultSet.getInt("port");
                String password = resultSet.getString("password");
                String remark = resultSet.getString("remark");
                ret.add(new Session(id, address, port, password, remark));
            }
            return ret;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return Lists.newArrayList();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(String address, Integer port, String password, String remark) {
        ensureTable();
        if (exist(address, port)) {
            return;
        }
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate(StrUtil.format("insert into session (address,port,password,remark) values('{}',{},'{}','{}')", address, port, password, remark));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete(Integer id) {
        ensureTable();
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate(StrUtil.format("delete from session where id={}", id));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(Session session) {
        ensureTable();
        String address = session.getAddress();
        Integer port = session.getPort();
        String password = session.getPassword();
        String remark = session.getRemark();
        if (session.getId() == null) {
            save(address, port, password, remark);
            return;
        }
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:session.db");
            Statement stat = conn.createStatement();
            stat.executeUpdate(StrUtil.format("update session set address='{}',port={},password='{}',remark='{}' where id={}", address, port, password, remark, session.getId()));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
