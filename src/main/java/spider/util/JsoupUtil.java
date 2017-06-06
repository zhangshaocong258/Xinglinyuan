package spider.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zsc on 2017/6/5.
 * 得到目录里面的内科疾病
 */
public class JsoupUtil {
    public static void main(String[] args) {
        File file = new File("E:\\XinglinyuanDownload\\目录\\category.html");//地址路径，相对是src同级路径
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            reader = new InputStreamReader(new FileInputStream(file));
            //将输入流写入输出流
            char[] buffer = new char[1024];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        //返回转换结果
        if (writer != null) {
            Map<String, String> map = new HashMap<>();
            String html = writer.toString();
            String baseUrl = "http://tcm.sstp.cn";
            Document document = Jsoup.parse(html, baseUrl);
            Element category = document.select("div[class=w-950]").first().
                    select("div[id=category]").first().
                    select("dl[class=i6-ff m-t10 p-b10]").first();
            Elements links = category.select("a[href]");
            Element first = category.select("a[href]").first();

            System.out.println(first.attr("href") + "    " + first.text());
            for (int i = 1; i < links.size(); i++) {
                map.put(links.get(i).attr("href"), first.text() + "/" + links.get(i).text());
//                System.out.println(links.get(i).attr("href") + "    " + links.get(i).text());
            }

            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + "   " + entry.getValue());
            }
        }

    }
}
