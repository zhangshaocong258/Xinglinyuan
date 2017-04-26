package core.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zsc on 2016/10/25.
 */
public class LinkQueue {
    private static HashSet<String> visitedUrl = new HashSet<String>();//已访问过的URL，不能重复
    private static List<String> unVisitedUrl = new LinkedList<String>();//等待访问的URL

    //获得已经访问的URL
    public static HashSet<String> getVisitedUrl() {
        return visitedUrl;
    }

    //获得URL队列
    public static List<String> getUnVisitedUrl() {
        return unVisitedUrl;
    }

    //添加到访问过的URL队列中
    public static void addVisitedUrl(String url) {
        visitedUrl.add(url);
    }

    //移除访问过的URL
    public static void removeVisitedUrl(String url) {
        visitedUrl.remove(url);
    }

    //队列初始化原始URLlist
    public static void initializeUrls(String url) {
        unVisitedUrl.add(url);
    }

    //队列新增URL
    public static void addUnvisititedUrl(String url) {
        unVisitedUrl.add(url);
    }

    //移除队列中的URL
    public static void removeUnvisitedUrl(String url) {
        unVisitedUrl.remove(url);
    }

    //判断未访问的URL队列是否为空
    public static boolean unVisitedUrlsEmpty() {
        return unVisitedUrl.isEmpty();
    }

}
