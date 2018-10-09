package org.piaohao.redisManager;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.piaohao.redisManager.table.TableData;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanResult;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RedisManager {

    public static XJedisClient client;

    public static String address;
    public static Integer port;
    public static String password;

    public static double usedMemory;
    public static double hitRate;
    public static double cpuRate;
    public static double connectedClient;

    public static String version;
    public static int dbCount;
    public static int runModel;
    public static long keyCount;
    public static int realPort;
    public static long uptime;

    public static boolean start = false;

    public static void init(String address, Integer port, String password) {
        RedisManager.address = address;
        RedisManager.port = port;
        RedisManager.password = password;
//        JedisPool pool = new JedisPool(new GenericObjectPoolConfig(), "10.1.1.56", 6379, 5000, "123456");
        JedisPool pool = null;
        if (StrUtil.isBlank(password)) {
            pool = new JedisPool(new GenericObjectPoolConfig(), address, port, 5000);
        } else {
            pool = new JedisPool(new GenericObjectPoolConfig(), address, port, 5000, password);
        }
        client = new XJedisClient();
        client.setPool(pool);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            String memoryStr = RedisManager.info(null);
            Map<String, String> ret = Maps.newHashMap();
            StrUtil.split(memoryStr, '\n')
                    .stream()
                    .filter(s -> s.contains(":"))
                    .map(s -> StrUtil.split(s, ":"))
                    .forEach(s -> ret.put(s[0], s[1]));
            usedMemory = NumberUtil.div((double) Convert.toLong(ret.get("used_memory")), (double) 1e6, 2);
            hitRate = Convert.toFloat(ret.get("keyspace_hits"));
            cpuRate = Convert.toFloat(ret.get("used_cpu_sys_children"));
            connectedClient = Convert.toLong(ret.get("connected_clients"));

            version = Convert.toStr(ret.get("redis_version"));
            realPort = Convert.toInt(ret.get("tcp_port"));
            dbCount = dbCount();
            keyCount = RedisManager.client.size();
            runModel = Convert.toInt(ret.get("cluster_enabled"));
            uptime = Convert.toLong(ret.get("uptime_in_seconds"));
        }, 0, 2, TimeUnit.SECONDS);
        start = true;
    }

    public static TableData<String> query(String pattern) {
        List<String> totalKeys = Lists.newArrayList();
        String lastKey = "0";
        while (true) {
            ScanResult<String> result = client.scanKeys(lastKey, pattern, 50);
            totalKeys.addAll(result.getResult());
            if (result.getStringCursor().equals("0")) {
                break;
            }
            if (totalKeys.size() >= 3000) {
                break;
            }
            lastKey = result.getStringCursor();
        }
        return new TableData<>(totalKeys);
    }

    public static TableData<Map.Entry<String, String>> queryHash(String key, String pattern) {
        List<Map.Entry<String, String>> totalKeys = Lists.newArrayList();
        String lastKey = "0";
        while (true) {
            ScanResult<Map.Entry<String, String>> result = client.scanHash(key, lastKey, pattern, 50);
            totalKeys.addAll(result.getResult());
            if (result.getStringCursor().equals("0")) {
                break;
            }
            if (totalKeys.size() >= 3000) {
                break;
            }
            lastKey = result.getStringCursor();
        }
        return new TableData<>(totalKeys);
    }

    public static List<String> zsetMembers(String key) {
        Jedis jedis = client.getJedis();
        Set<String> set = jedis.zrange(key, 0, jedis.zcount(key, 0, -1));
        client.returnJedis(jedis);
        return Lists.newArrayList(set);
    }

    public static String info(String section) {
        Jedis jedis = client.getJedis();
        String info = null;
        if (StrUtil.isBlank(section)) {
            info = jedis.info();
        } else {
            info = jedis.info(section);
        }

        client.returnJedis(jedis);
        return info;
    }

    public static Integer dbCount() {
        Jedis jedis = client.getJedis();
        List<String> ret = jedis.configGet("databases");
        Integer dbCount = Convert.toInt(ret.get(1));
        client.returnJedis(jedis);
        return dbCount;
    }

    public static String clientList() {
        Jedis jedis = client.getJedis();
        String ret = jedis.clientList();
        client.returnJedis(jedis);
        return ret;
    }

    public static void killClient(String address) {
        Jedis jedis = client.getJedis();
        try {
            jedis.clientKill(address);
        } catch (Exception e) {
            //ignore
        }
        client.returnJedis(jedis);
    }

    static class OriginalConnection extends Connection {

    }

    /*public static String execute(String command){
        Jedis jedis = client.getJedis();
        try {
            Connection jedisClient =  jedis.getClient();
        } catch (Exception e) {
            //ignore
        }
        client.returnJedis(jedis);
    }*/
}
