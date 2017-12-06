import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/11/27.
 * 生成select.jsp中 的症状
 */
public class JSPUtil {
    public static void main(String args[]) throws Exception{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src\\main\\resources\\脉舌词典带标签.txt")));
        String line;
        List<String> list = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            list.add(line.split("\\t")[0]);
        }
        for (String str : list) {
            System.out.println("<tr><td><input type=\"checkbox\" name=\"mai\" value=\""+str+"\">"+str+"</td></tr>");
        }
    }
}
