package core.util;

import redis.clients.jedis.Jedis;


/**
 * Created by zsc on 2016/12/4.
 */
public class RedisSet {
    private static Jedis jedis = new Jedis(Config.redisIP, Config.redisPort, 100000);


    //队列初始化原始URLlist
    public static void initializeUrls(String url) {
        jedis.lpush(Config.unVisitedUrl, url);
    }

    //未访问队列新增URL
    public static void addUnvisititedUrl(String url) {
        jedis.lpush(Config.unVisitedUrl, url);
    }

    //访问过的队列新增URL
    public static void addVisitedUrl(String url) {
        jedis.sadd(Config.visitedUrl, url);
    }

    //移除访问队列中的URL
    public static void removeVisitedUrl(String url) {
        jedis.srem(Config.visitedUrl, url);
    }

    //已经访问的URL队列是否包含
    public static boolean visitedUrlContains(String url) {
        return jedis.sismember(Config.visitedUrl, url);
    }

    //得到未访问的URL队列的第一个URL
    public static String getUnvisitedUrl() {
        return jedis.lpop(Config.unVisitedUrl);//返回并移除
    }

    //判断未访问的URL队列是否为空
    public static boolean unVisitedUrlsEmpty() {
        return jedis.llen(Config.unVisitedUrl) == 0l;
    }

    //判断访问过的URL队列是否为空
    public static boolean visitedUrlsEmpty() {
        return jedis.scard(Config.visitedUrl) == 0l;
    }

    public static void save() {
        jedis.save();
    }

}


