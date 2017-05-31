package spider.main;

import spider.util.Config;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;


/**
 * Created by zsc on 2016/8/13.
 */
public class PageProcessor implements Callable {
    private Scheduler scheduler;

    public PageProcessor(Scheduler scheduler) {
        this.scheduler = scheduler;
    }


    public Boolean call() {

        if (!Config.redisEnable) {
            //获取url和下载数据，注意同步
            int count = 0;
            //每个线程提取100个网页
            while (count++ < 500) {
                String str = scheduler.getURL();
//                System.out.println("url " + str);
                try {
                    if (str != null) {
                        String html = Downloader.downloadPage(str);
                        if (html == null) {
                            continue;//相当于没有得到页面数据
                        }
                        //得到当前URL的html和host，用来生成html中的绝对路径
                        Set<String> newStr = HtmlParserTool.extractLinks(html, str);
                        scheduler.insertNewURL(newStr);
                    } else {
                        System.out.println("队列为空，结束");
                        break;
                    }
                    //            } catch (NullPointerException e) {
                    //                System.out.println("NullPointerException: " + count);
                    //                e.printStackTrace();
                    //            } catch (NoSuchElementException e) {
                    //                System.out.println("NoSuchElementException: " + count);
                    //                e.printStackTrace();
                    //            } catch (ConcurrentModificationException e) {
                    //                System.out.println("ConcurrentModificationException: " + count);
                    //                e.printStackTrace();
                    //            } catch (IndexOutOfBoundsException e) {
                    //                System.out.println("IndexOutOfBoundsException: " + count);
                    //                e.printStackTrace();
                } catch (IOException e) {
                    //超时则添加到待爬取队列尾部，等待下一次爬取，若再次失败，继续...
                    scheduler.recallURL(str);
                    System.out.println("超时");
                    e.printStackTrace();
                } catch (Exception e) {
                    //捕获除了超时外的其他错误，
                    scheduler.recallURL(str);
                    System.out.println("其他错误");
                    e.printStackTrace();
                }

            }
            System.out.println(Thread.currentThread().getName() + "结束： " + count);
            return true;
        } else {
            //获取url和下载数据，注意同步
            int count = 0;
            //每个线程提取100个网页
            while (count++ < 500) {
                String str = scheduler.redisGetURL();
//                System.out.println("str " + str);
                try {
                    if (str != null) {
                        String html = Downloader.downloadPage(str);
                        if (html == null) {
                            continue;//相当于没有得到页面数据
                        }
                        //得到当前URL的html和host，用来生成html中的绝对路径
                        Set<String> newStr = HtmlParserTool.extractLinks(html, str);
                        scheduler.redisInsertNewURL(newStr);
                    } else {
                        System.out.println("队列为空，结束");
                        break;
                    }
                } catch (IOException e) {
                    //超时则添加到待爬取队列尾部，等待下一次爬取，若再次失败，继续...
                    scheduler.redisRecallURL(str);
                    System.out.println("超时" + str);
                    e.printStackTrace();
                } catch (Exception e) {
                    //捕获除了超时外的其他错误
                    scheduler.redisRecallURL(str);
                    System.out.println("其他错误");
                    e.printStackTrace();
                }

            }
//            System.out.println(Thread.currentThread().getName() + "结束： " + count);
            return true;
        }
    }
}
