package core.spider;

import core.util.Config;

/**
 * Created by zsc on 2016/11/23.
 * 只爬取特定（单个）网页时，start内输入网页名，去除主域名www.zhihu.com
 * config中threadNum=1，redisEnable=false
 */
public class Main {
    public static void main(String[] args) {
        HttpClientTool.getInstance();//首先初始化httpclient和context，否则出错，后面初始化可能没有得到正确的context
        Spider.getInstance().start(Config.startURL);
    }
}
