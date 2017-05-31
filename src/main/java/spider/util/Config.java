package spider.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by zsc on 2016/10/28.
 */
public class Config {

    /**
     * 下载网页线程数
     */
    public static int thread_num;


    /**
     * 验证码路径
     */
    public static String captcha;


    /**
     * 账号
     */
    public static String account;


    /**
     * 密码
     */
    public static String password;

    /**
     * cookie路径
     */
    public static String cookiePath;

    /**
     * 下载保存地址
     */
    public static String downloadPath;

    /**
     * 爬虫入口
     */
    public static String startURL;

    /**
     * 域名
     */
    public static String domainName;

    /**
     * 网页限制
     */
    public static String category;

    /**
     * Redis配置
     */

    /**
     * Redis 是否使用
     */
    public static boolean redisEnable;

    /**
     * Redis IP
     */
    public static String redisIP;

    /**
     * Redis Port
     */
    public static int redisPort;

    /**
     * Redis visitedUrl
     */
    public static String visitedUrl;

    /**
     * Redis unVisitedUrl
     */
    public static String unVisitedUrl;


    static {
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(Config.class.getResourceAsStream("/config.properties"), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        thread_num = Integer.valueOf(properties.getProperty("threadNum"));
        captcha = properties.getProperty("captcha");
        account = properties.getProperty("account");
        password = properties.getProperty("password");
        cookiePath = properties.getProperty("cookies");
        downloadPath = properties.getProperty("download") + "-" + sdf.format(date);//加上时间
        startURL = properties.getProperty("startURL");
        domainName = properties.getProperty("domainName");
        category = properties.getProperty("category");
        redisEnable = Boolean.valueOf(properties.getProperty("redisEnable"));
        redisIP = properties.getProperty("redisIP");
        redisPort = Integer.valueOf(properties.getProperty("redisPort"));
        visitedUrl = properties.getProperty("visitedUrl");
        unVisitedUrl = properties.getProperty("unVisitedUrl");

    }

}
