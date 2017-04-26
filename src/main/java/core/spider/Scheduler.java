package core.spider;

import core.util.Config;
import core.util.LinkQueue;
import core.util.RedisSet;

import java.util.Set;

/**
 * Created by zsc on 2016/8/13.
 * 广度优先遍历
 * 多线程设计
 */
public class Scheduler {
    private static int threads = Config.thread_num;

    public synchronized String getURL() {
        while (true) {
            try {
                if (!LinkQueue.unVisitedUrlsEmpty()) {
                    String url = LinkQueue.getUnVisitedUrl().get(0);
                    LinkQueue.removeUnvisitedUrl(url);
                    //判断要爬取的url是否已经爬过，若爬过，则进行下一次循环
                    //保证VisitedUrl与最后生成的doc一致，目前超时则放入待爬取队列尾端，若还是连接失败，继续放至尾端
                    //并且保证此URL合法，因为添加URL的时候LinkQueue已经做了处理，除非初始URL有问题
                    if (!LinkQueue.getVisitedUrl().contains(url)) {
                        LinkQueue.addVisitedUrl(url);//addUnvisititedUrl保证此时url不会错误
                        return url;
                    } else {
                        continue;
                    }
                } else {
                    threads--;
                    if (threads > 0) {
                        wait();//InterruptedException
                        threads++;
                    } else {
                        notifyAll();
                        return null;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //下载失败，重新添加回待爬取队列
    public synchronized void recallURL(String url) {
        if (url != null && !url.trim().equals("") &&
                !LinkQueue.getVisitedUrl().contains(url) &&
                !LinkQueue.getUnVisitedUrl().contains(url)) {
            LinkQueue.addUnvisititedUrl(url);
        }
        LinkQueue.removeVisitedUrl(url);
    }

    public synchronized void insertNewURL(Set<String> newURL) {
        for (String url : newURL) {
            if (url != null && !url.trim().equals("") &&
                    !LinkQueue.getVisitedUrl().contains(url) &&
                    !LinkQueue.getUnVisitedUrl().contains(url)) {
                LinkQueue.addUnvisititedUrl(url);
            }
        }

        //添加完毕后，如果URL队列不为空，则唤起
        if (!LinkQueue.unVisitedUrlsEmpty() && threads < Config.thread_num) {
            notifyAll();
        }
    }

    /**
     * Redis操作
     * @return
     */
    public synchronized String redisGetURL() {
        while (true) {
            try {
                if (!RedisSet.unVisitedUrlsEmpty()) {//取出并删除
                    String url = RedisSet.getUnvisitedUrl();
                    if (!RedisSet.visitedUrlContains(url)) {
                        RedisSet.addVisitedUrl(url);//添加至已访问
                        return url;
                    } else {
                        continue;
                    }
                } else {
                    threads--;
                    if (threads > 0) {
                        wait();//InterruptedException
                        threads++;
                    } else {
                        notifyAll();
                        return null;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //下载失败，重新添加回待爬取队列
    public synchronized void redisRecallURL(String url) {
        if (url != null && !url.trim().equals("")) {
            RedisSet.addUnvisititedUrl(url);
            RedisSet.removeVisitedUrl(url);
        }
    }

    public synchronized void redisInsertNewURL(Set<String> newURL) {
        for (String url : newURL) {
            if (url != null && !url.trim().equals("") &&
                    !RedisSet.visitedUrlContains(url)) {
                RedisSet.addUnvisititedUrl(url);
            }
        }

        //添加完毕后，如果URL队列不为空且线程少了，则唤起
        if (threads < Config.thread_num) {
            notifyAll();
        }
    }

}
