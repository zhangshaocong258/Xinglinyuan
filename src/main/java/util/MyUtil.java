package util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.http.io.BufferInfo;
import tree.domain.Forest;
import tree.library.Library;
import tree.splitWord.GetWord;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/10/31.
 * <p>
 * 脾.{0,1}胃
 * 脉象: 浮 沉 迟 数 洪 细 虚 实 滑 涩 弦 紧 结 代 促 长 短 缓 濡 弱 微 散 芤 伏 牢 革 动 疾
 * 舌质: 淡红 淡白 红 白 绛 青 紫
 * 老 嫩 胖 瘦 点 刺 裂纹 齿痕 齿印
 * 舌苔: 厚 薄 润 燥 少津 少津液 腻 腐 剥落 真 假 少 无
 * 白 黄 灰黑
 */
public class MyUtil {
//    private static String casePath = "E:\\医案\\脾胃虚弱\\";
//    private static String casePath = "E:\\医案\\肝肾阴虚\\";
    private static String casePath = "E:\\医案\\心气虚证\\";
    private static String segPath = "E:\\医案\\脾胃虚弱分词后\\";
//    private static String disease = "piweixuruo";
//    private static String disease = "ganshenyinxu";
    private static String disease = "xinqixu";

    public static void main(String args[]) throws Exception {
//        genDic();
//        genCase();
//        segCase();
//        maisheFix();
//        genExplainDic();
//        synonymyNew();
        genTestData();
    }


    private static void genTestData() throws Exception {
        String allData = "src\\main\\resources\\特征\\iris.csv";
        String trainData = "src\\main\\resources\\特征\\iristrain.csv";
        String testData = "src\\main\\resources\\特征\\iristest.csv";
        BufferedReader all = new BufferedReader(new InputStreamReader(new FileInputStream(allData)));
        BufferedWriter train = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(trainData)));
        BufferedWriter test = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(testData)));
//        for (int i = 1; i < 56; i++) {
//            train.write("a" + i + ",");
//            test.write("a" + i + ",");
//        }
//        train.write("label");
//        test.write("label");
//        train.newLine();
//        test.newLine();
        String line;
        while ((line = all.readLine()) != null) {
            if (Math.random() >= 0.3) {
                train.write(line);
                train.newLine();
            } else {
                test.write(line);
                test.newLine();
            }
        }
        train.close();
        test.close();

    }


    /**
     * 同义词生成标签
     * ascii: a 97  z 122
     * @throws Exception
     */
    private static void synonymyNew() throws Exception{
        String in = "src\\main\\resources\\同义词.txt";
        String out = "src\\main\\resources\\同义词new.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

        String line;
        String head = "aaa";
        while ((line = br.readLine()) != null) {
            char[] chars = head.toCharArray();
            if (chars[2] >= 122) {
                if (chars[1] >= 122) {
                    if (chars[0] >= 122) {
                        throw new IndexOutOfBoundsException();
                    }else {
                        chars[0] += 1;
                        chars[1] = 'a';
                        chars[2] = 'a';
                    }
                }else {
                    chars[1] += 1;
                    chars[2] = 'a';
                }
            } else {
                chars[2] += 1;
            }
            head = String.valueOf(chars);
            bw.write(head + "：" + line);
            bw.newLine();
        }
        bw.close();
    }

    /**
     * 脉舌分词测试
     * @throws Exception
     */
    private static void maisheFix() throws Exception {

//        String path = "E:\\aaaa\\脾胃虚弱";
//        String path = "E:\\aaaa\\肝肾阴虚";
        String path = "E:\\aaaa\\心气虚证";
        File folder = new File(path);

        File[] files = folder.listFiles();//若目录存在，没有文件，files不为null，length为0；若目录不存在，files为null
        if (files.length != 0) {
            for (File f : files) {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
                br.readLine();
                br.readLine();
                String examine = br.readLine().split(":")[1].trim();
                String sheZhi = "";
                String sheTai= "";
                String maiXiang= "";
                String[] strings = examine.split(",|，|。|、|;|；");
                for (int i = 0; i < strings.length; i++) {
                    if (strings[i].startsWith("舌质")|| strings[i].startsWith("质")) {
                        sheZhi = strings[i];
                    } else if (strings[i].startsWith("舌苔") || strings[i].startsWith("苔")) {
                        sheTai = strings[i];
                    } else if (strings[i].startsWith("脉") ) {
                        maiXiang = strings[i];
                    }
                }
                Forest forest = Library.makeForest(new BufferedReader(new InputStreamReader(
                        new FileInputStream(new File("D:\\IDEA\\Xinglinyuan\\data\\dictionary\\custom\\脉舌词典带标签.txt")))));

                String segSheZhi = getSegResult(forest,sheZhi);
                String segSheTai = getSegResult(forest,sheTai);
                String segMaiXiang = getSegResult(forest,maiXiang);
                System.out.println(segSheZhi + "    " + segSheTai + "   " + segMaiXiang);
                if (segSheZhi.length() == 0){
                    System.out.println(f.getName() + "  " + "舌质");
                }
                if (segSheTai.length() == 0){
                    System.out.println(f.getName() + "  " + "舌苔");
                }
                if (segMaiXiang.length() == 0){
                    System.out.println(f.getName() + "  " + "脉");
                }

            }
        }
    }


    /**
     * 生成解释词典带标签(一次性)
     */
    private static void genExplainDic() throws Exception {

        File in = new File("src\\main\\resources\\症状词典.txt");
        File out = new File("src\\main\\resources\\症状词典带标签.txt");
        if (!out.exists()) {
            out.createNewFile();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        String string = br.readLine();
        while (string != null) {
            bw.write(string.trim() + "\t" + "maishe");
            bw.newLine();
            string = br.readLine();
        }
        bw.close();

    }


    /**
     * 生成脉舌词典(一次性)
     */
    private static void genDic() throws Exception {
        String str = "浮 沉 迟 数 洪 细 虚 实 滑 涩 弦 紧 结 代 促 长 短 缓 濡 弱 微 散 芤 伏 牢 革 动 疾 " +
                "淡红 淡白 红 白 绛 青 紫 " +
                "老 嫩 胖 瘦 点 刺 裂 齿 " +
                "厚 薄 润 燥 少津 腻 腐 剥落 真 假 少 无 " +
                "白 黄 灰黑";
        File out = new File("E:\\医案\\脉舌词典带标签.txt");
        if (!out.exists()) {
            out.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        String[] strings = str.split(" ");
        for (String s : strings) {
            bw.write(s + "\t" + "maishe");
            bw.newLine();
        }
        bw.close();

    }

    /**
     *
     * 获取原始医案并保存
     * 医案格式如下：
     * 个人信息
     * 主诉及病史
     * 诊查
     * 辨证
     */
    private static void genCase() throws Exception {
        //       脾.{0,1}胃        肝.{0,1}肾    心.{0,1}气
        String personalInf = "个人信息: 张某，男，65岁";
        String medicalHis = "主诉及病史: 胸痛，气短心悸，面色苍白，自汗";
        String examine = "诊查: 舌质淡红，苔薄白，脉弦结代";
        String discriminate = "辨证: 心气虚";
        int num = 1;
        File folder = new File(casePath);
        File[] files = folder.listFiles();//若目录存在，没有文件，files不为null，length为0；若目录不存在，files为null
        if (files.length != 0) {
            for (File f : files) {
                int temp = Integer.valueOf(f.getName().substring(disease.length(), f.getName().lastIndexOf(".txt")));
                if (temp > num) {
                    num = temp;
                }
            }
            num++;
        }

        File out = new File(casePath + disease + num + ".txt");
        if (!out.exists()) {
            out.createNewFile();
        } else {
            throw new Exception("文件已存在");
        }
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));
        bw.write(personalInf);
        bw.newLine();
        bw.write(medicalHis);
        bw.newLine();
        bw.write(examine);
        bw.newLine();
        bw.write(discriminate);
        bw.close();
    }

    /**
     * 对医案进行分词处理
     */
    private static void segCase() throws Exception{
        File folder = new File(casePath);
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File f : files) {
                doSegCase(f);
            }
        } else {
            doSegCase(folder);
        }
    }

    private static void doSegCase(File file) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        br.readLine();
        String medicalHis = br.readLine().split(":")[1].trim();
        String examine = br.readLine().split(":")[1].trim();
        String sheZhi = "";
        String sheTai= "";
        String maiXiang= "";
        String[] strings = examine.split(",|，|。|、|;|；");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].startsWith("舌质")|| strings[i].startsWith("质")) {
                sheZhi = strings[i];
            } else if (strings[i].startsWith("舌苔") || strings[i].startsWith("苔")) {
                sheTai = strings[i];
            } else if (strings[i].startsWith("脉") ) {
                maiXiang = strings[i];
            }
        }
        System.out.println("shezhi " + sheZhi);
        System.out.println("sheTai " + sheTai);
        System.out.println("maiXiang " + maiXiang);
        String segExamine = new StringBuilder().append(getSymptom(HanLP.segment(medicalHis)).toString()).
                append(getSymptom(HanLP.segment(examine)).toString()).toString();
        /*CustomDictionary.add("浮", "shemai 1");
        CustomDictionary.add("沉", "shemai 1");
        CustomDictionary.add("迟", "shemai 1");
        CustomDictionary.add("数", "shemai 1");
        CustomDictionary.add("洪", "shemai 1");
        CustomDictionary.add("细", "shemai 1");
        CustomDictionary.add("虚", "shemai 1");
        CustomDictionary.add("实", "shemai 1");
        CustomDictionary.add("滑", "shemai 1");
        CustomDictionary.add("涩", "shemai 1");
        CustomDictionary.add("弦", "shemai 1");
        CustomDictionary.add("紧", "shemai 1");
        CustomDictionary.add("结", "shemai 1");
        CustomDictionary.add("代", "shemai 1");
        CustomDictionary.add("促", "shemai 1");
        CustomDictionary.add("长", "shemai 1");
        CustomDictionary.add("短", "shemai 1");
        CustomDictionary.add("缓", "shemai 1");
        CustomDictionary.add("濡", "shemai 1");
        CustomDictionary.add("弱", "shemai 1");
        CustomDictionary.add("微", "shemai 1");
        CustomDictionary.add("散", "shemai 1");
        CustomDictionary.add("芤", "shemai 1");
        CustomDictionary.add("伏", "shemai 1");
        CustomDictionary.add("牢", "shemai 1");
        CustomDictionary.add("革", "shemai 1");
        CustomDictionary.add("动", "shemai 1");
        CustomDictionary.add("疾", "shemai 1");



        CustomDictionary.add("淡红", "shemai 1");
        CustomDictionary.add("淡白", "shemai 1");
        CustomDictionary.add("白", "shemai 1");
        CustomDictionary.add("红", "shemai 1");
        CustomDictionary.add("绛", "shemai 1");
        CustomDictionary.add("青", "shemai 1");
        CustomDictionary.add("紫", "shemai 1");


        CustomDictionary.add("老", "shemai 1");
        CustomDictionary.add("嫩", "shemai 1");
        CustomDictionary.add("胖", "shemai 1");
        CustomDictionary.add("瘦", "shemai 1");
        CustomDictionary.add("点", "shemai 1");
        CustomDictionary.add("刺", "shemai 1");
        CustomDictionary.add("裂纹", "shemai 1");
        CustomDictionary.add("齿痕", "shemai 1");
        CustomDictionary.add("齿印", "shemai 1");*/

        Forest forest = Library.makeForest(new BufferedReader(new InputStreamReader(
                new FileInputStream(new File("D:\\IDEA\\Xinglinyuan\\data\\dictionary\\custom\\脉舌词典带标签.txt")))));

        String segSheZhi = getSegResult(forest,sheZhi);
        String segSheTai = getSegResult(forest,sheTai);
        String segMaiXiang = getSegResult(forest,maiXiang);
        System.out.println("segExamine " + segExamine);
        System.out.println("segSheZhi " + segSheZhi);
        System.out.println("segSheTai " + segSheTai);
        System.out.println("segMaiXiang " + segMaiXiang);
    }



    /**
     * 得到分词后的解释
     * @param forest
     * @param content
     * @return
     */
    public static String getSegExplain(Forest forest, String content) {
        GetWord udg = forest.getWord(content);
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = udg.getFrontWords()) != null){
            sb.append(temp + "\\" + udg.getParam(0)).append(",");
        }
        return sb.toString();
    }

    /**
     * 得到分词后的脉舌字符串信息
     * @param forest
     * @param content
     * @return
     */
    public static String getSegResult(Forest forest, String content) {
        GetWord udg = forest.getWord(content);
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = udg.getFrontWords()) != null){
            if (udg.getParam(0).equals("maishe")) {
                sb.append(temp).append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 过滤得到症状
     * @param termList 待过滤的字符串
     * @return 过滤后的症状字符串
     */
    public static String getSymptom(List<Term> termList) {
        List<Term> temp = new ArrayList<Term>();
        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            String nature = term.nature != null ? term.nature.toString() : "空";
            if (!nature.equals("symptom")) {
                temp.add(term);
            }
        }
        termList.removeAll(temp);
        StringBuilder sb = new StringBuilder();
        for (Term term : termList) {
            sb.append(term.toString()).append(",");
        }
        int length = sb.toString().length();
        return sb.toString();
    }

    /**
     * 过滤得到症状
     * @param termList 待过滤的字符串
     * @return 过滤后的症状字符串
     */
    private static List<Term> getSheMai(List<Term> termList) {
        List<Term> temp = new ArrayList<Term>();
        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            String nature = term.nature != null ? term.nature.toString() : "空";
            if (!nature.equals("maishe")) {
                temp.add(term);
            }
        }
        termList.removeAll(temp);
        return termList;
    }
}
