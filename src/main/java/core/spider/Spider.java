package core.spider;

import core.util.Config;
import core.util.LinkQueue;
import core.util.RedisSet;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zsc on 2016/8/13.
 */
public class Spider {

    private static class SpiderHolder {
        private static Spider spider = new Spider();
    }

    public static Spider getInstance() {
        return SpiderHolder.spider;
    }

    public Spider() {
    }

    private Scheduler scheduler = new Scheduler();
    private LoginTool loginTool = new LoginTool();


    /**
     * 启动线程gather，然后开始收集网页资料
     * 刚开始
     */
    public void start(String url) {
        //初始化URL表
        if (Config.redisEnable) {
            System.out.println("redis");
            if (RedisSet.unVisitedUrlsEmpty()) {
                RedisSet.initializeUrls(url);
            }
        } else {
            LinkQueue.initializeUrls(url);
        }

        //创建文件夹
        File folder = new File(Config.downloadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        if (!loginTool.deserializeCookieStore(Config.cookiePath)) {
            System.out.println("进行登录...");
            loginTool.login(Config.account, Config.password);
        }


        long startTime = System.currentTimeMillis();
        System.out.println("采集开始");
        ExecutorService threadPool = Executors.newFixedThreadPool(20);//这个不变，线程20个位置，把所有线程往里面扔
        ArrayList<Future<Boolean>> results = new ArrayList<Future<Boolean>>();
        for (int i = 0; i < Config.thread_num; i++) {
            PageProcessor pageProcessor = new PageProcessor(scheduler);
            results.add(threadPool.submit(pageProcessor));
        }

        //等待结果完成，相当于join
        for (int i = 0; i < results.size(); i++) {
            try {
                results.get(i).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                threadPool.shutdown();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("采集结束,程序运行时间： " + (endTime - startTime) + "ms");
        //超过2s，报错
        if (Config.redisEnable) {
            RedisSet.save();
        }
        System.out.println("保存结束,时间： " + (endTime - startTime) + "ms");

    }

}
