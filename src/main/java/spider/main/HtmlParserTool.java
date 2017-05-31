package spider.main;

import spider.util.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zsc on 2016/11/4.
 */
public class HtmlParserTool {
    public static Set<String> extractLinks(String html, String str){
        String url = new StringBuilder().append(Config.domainName).append(str).toString();
        Set<String> newUrl = new HashSet<String>();
        Document doc = Jsoup.parse(html, Config.domainName);
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            String absHref = link.attr("abs:href");
            //判断是否存入Redis，原来是(Config.redisEnable ? !RedisSet.visitedUrlContains(absHref) : true))，简化后如下，insert已经判断，不要了
            if (!absHref.equals("") && accept(url, absHref)) {
                newUrl.add(absHref.substring(18));//去掉前面的域名
            }
        }
        return newUrl;
    }

    //忽略的url
//    private static boolean fobidden(String url) {
//        return url.startsWith(Config.logout) || url.startsWith(Config.inbox) ||
//                url.startsWith(Config.settings) || url.startsWith(Config.lives) ||
//                url.startsWith(Config.copyright) || url.startsWith(Config.symbol) ||
//                url.startsWith(Config.questionMark);
//    }

    public static boolean accept(String url, String absHref) {
        boolean c = false;
        boolean d = false;
        Pattern category = Pattern.compile(Config.category);//
        Pattern doc = Pattern.compile("^(http://tcm.sstp.cn/index.php\\?doc-view(.*))");//
        Matcher categoryMatcher = category.matcher(absHref);
        Matcher docMatcher = doc.matcher(absHref);
        Matcher urlMatcher = category.matcher(url);
//
//        if (categoryMatcher.matches()) {
//            c = true;
//        }else if (docMatcher.matches()) {
//            if (urlMatcher.matches()) {
//                d = true;
//            }
//        }
        if (categoryMatcher.matches()) {
            c = true;
        }

        if (docMatcher.matches()) {
            d = true;
        }

        if (c || d) {
            return true;
        } else {
            return false;
        }
    }
}
