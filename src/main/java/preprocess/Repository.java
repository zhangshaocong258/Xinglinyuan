package preprocess;

import com.hankcs.hanlp.HanLP;
import mybatis.Rule;
import mybatis.RuleHandler;
import tree.domain.Forest;
import tree.library.Library;
import util.MyUtil;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by zsc on 2017/11/15.
 * 知识库的构建
 * 三个症状知识库
 * 生成特征
 */
public class Repository {
    private static final String synonymDicPath = "src\\main\\resources\\同义词new.txt";
    private static final String maishePath = "src\\main\\resources\\脉舌词典带标签.txt";
    private static final String caseDataPath = "src\\main\\resources\\心气虚证";
    private static final String featureFolder = "src\\main\\resources\\特征";
    private static final String featureFile = "src\\main\\resources\\特征\\out.csv";
    private static HashMap<String, String> dicHashmap = new HashMap<String, String>();
    private static Forest forest;
    private static Rule x;
    private static Rule g;
    private static Rule p;
    private static HashSet<String> xZhuzheng1Set = new HashSet<String>();
    private static HashSet<String> xZhuzheng2Set = new HashSet<String>();
    private static HashSet<String> xCizhengSet = new HashSet<String>();
    private static HashSet<String> xZhushezhiSet = new HashSet<String>();
    private static HashSet<String> xZhushetaiSet = new HashSet<String>();
    private static HashSet<String> xZhumaiSet = new HashSet<String>();
    private static HashSet<String> xCishezhiSet = new HashSet<String>();
    private static HashSet<String> xCishetaiSet = new HashSet<String>();
    private static HashSet<String> xCimaiSet = new HashSet<String>();

    private static HashSet<String> gZhuzheng1LeftSet = new HashSet<String>();
    private static HashSet<String> gZhuzheng1RightSet = new HashSet<String>();
    private static HashSet<String> gZhuzheng2LeftSet = new HashSet<String>();
    private static HashSet<String> gZhuzheng2RightSet = new HashSet<String>();
    private static HashSet<String> gZhuzheng3Set = new HashSet<String>();
    private static HashSet<String> gCizhengSet = new HashSet<String>();
    private static HashSet<String> gZhushezhiSet = new HashSet<String>();
    private static HashSet<String> gZhushetaiSet = new HashSet<String>();
    private static HashSet<String> gZhumaiSet = new HashSet<String>();
    private static HashSet<String> gCishezhiSet = new HashSet<String>();
    private static HashSet<String> gCishetaiSet = new HashSet<String>();
    private static HashSet<String> gCimaiSet = new HashSet<String>();

    private static HashSet<String> pZhuzheng1Set = new HashSet<String>();
    private static HashSet<String> pZhuzheng2Set = new HashSet<String>();
    private static HashSet<String> pCizhengSet= new HashSet<String>();
    private static HashSet<String> pZhushezhiSet = new HashSet<String>();
    private static HashSet<String> pZhushetaiSet = new HashSet<String>();
    private static HashSet<String> pZhumaiSet = new HashSet<String>();
    private static HashSet<String> pCishezhiSet = new HashSet<String>();
    private static HashSet<String> pCishetaiSet = new HashSet<String>();
    private static HashSet<String> pCimaiSet = new HashSet<String>();
    private static DecimalFormat df = new DecimalFormat("######0.0000");

    public static void main(String args[]) throws Exception {
        readDic(synonymDicPath);
        readForest(maishePath);
//        getRule();
//        createFile(featureFolder, featureFile);

//        genFeature(caseDataPath);
        doGenFeature(new File("src\\main\\resources\\心气虚证\\xinqixu1.txt"));


    }

    /**
     * 读取病例数据
     *
     * @param dic
     * @throws Exception
     */
    private static void readDic(String dic) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dic)));
        String line;
        while ((line = br.readLine()) != null) {
            String[] strings = line.split("：|,");
            String coding = strings[0].trim();
            for (int i = 1; i < strings.length; i++) {
                if (dicHashmap.containsKey(strings[i])) {
                    throw new Exception("重复的key");
                } else {
                    dicHashmap.put(strings[i].trim(), coding);
                }
            }
        }
    }

    private static void readForest(String maishePath) throws Exception {
        forest = Library.makeForest(new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(maishePath)))));
    }

    private static void getRule() {
        x = RuleHandler.selectByName("x");
        g = RuleHandler.selectByName("g");
        p = RuleHandler.selectByName("p");
        //心气虚
        String[] xZhuzhengs = x.getZhuzheng().split(",");
        for (String zhengzhuang : xZhuzhengs) {
            if (zhengzhuang.startsWith("!")) {
                xZhuzheng1Set.add(zhengzhuang.substring(1, zhengzhuang.length()));
            } else {
                xZhuzheng2Set.add(zhengzhuang);
            }
        }
        xCizhengSet.addAll(Arrays.asList(x.getCizheng().split(",")));
        xZhushezhiSet.addAll(Arrays.asList(x.getZhushezhi().split(",")));
        xCishezhiSet.addAll(Arrays.asList(x.getCishezhi().split(",")));
        xZhushetaiSet.addAll(Arrays.asList(x.getZhushetai().split(",")));
        xCishetaiSet.addAll(Arrays.asList(x.getCishetai().split(",")));
        xZhumaiSet.addAll(Arrays.asList(x.getZhumai().split(",")));
        xCimaiSet.addAll(Arrays.asList(x.getCimai().split(",")));

        //肝肾
        String[] gZhuzhengs = g.getZhuzheng().split(",");
        for (String zhengzhuang : gZhuzhengs) {
            if (zhengzhuang.startsWith("!")) {
                String temp = zhengzhuang.substring(1, zhengzhuang.length());
                if (temp.equals("aaz") || temp.equals("aax")) {
                    gZhuzheng1LeftSet.add(temp);
                } else {
                    gZhuzheng1RightSet.add(temp);
                }
            } else if (zhengzhuang.startsWith("@")) {
                String temp = zhengzhuang.substring(1, zhengzhuang.length());
                if (temp.equals("abn") || temp.equals("abm") || temp.equals("acj") || temp.equals("ack") || temp.equals("acl")) {
                    gZhuzheng2LeftSet.add(temp);
                } else {
                    gZhuzheng2RightSet.add(temp);
                }
            } else if (zhengzhuang.startsWith("#")) {
                gZhuzheng3Set.add(zhengzhuang.substring(1, zhengzhuang.length()));
            }
        }
        gCizhengSet.addAll(Arrays.asList(g.getCizheng().split(",")));
        gZhushezhiSet.addAll(Arrays.asList(g.getZhushezhi().split(",")));
        gCishezhiSet.addAll(Arrays.asList(g.getCishezhi().split(",")));
        gZhushetaiSet.addAll(Arrays.asList(g.getZhushetai().split(",")));
        gCishetaiSet.addAll(Arrays.asList(g.getCishetai().split(",")));
        gZhumaiSet.addAll(Arrays.asList(g.getZhumai().split(",")));
        gCimaiSet.addAll(Arrays.asList(g.getCimai().split(",")));


        //脾胃
        String[] pZhuzhengs = p.getZhuzheng().split(",");
        for (String zhengzhuang : pZhuzhengs) {
            if (zhengzhuang.startsWith("!")) {
                pZhuzheng1Set.add(zhengzhuang.substring(1, zhengzhuang.length()));
            } else {
                pZhuzheng2Set.add(zhengzhuang);
            }
        }
        pCizhengSet.addAll(Arrays.asList(p.getCizheng().split(",")));
        pZhushezhiSet.addAll(Arrays.asList(p.getZhushezhi().split(",")));
        pCishezhiSet.addAll(Arrays.asList(p.getCishezhi().split(",")));
        pZhushetaiSet.addAll(Arrays.asList(p.getZhushetai().split(",")));
        pCishetaiSet.addAll(Arrays.asList(p.getCishetai().split(",")));
        pZhumaiSet.addAll(Arrays.asList(p.getZhumai().split(",")));
        pCimaiSet.addAll(Arrays.asList(p.getCimai().split(",")));


    }

    /**
     * 创建输出文件
     *
     * @param output
     * @param outFile
     * @throws Exception
     */
    private static void createFile(String output, String outFile) throws Exception {
        File file = new File(output);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(outFile);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    /**
     * 遍历数据，生成特征
     *
     * @param path
     */
    private static void genFeature(String path) throws Exception {
        File folder = new File(path);
        if (folder.isFile()) {
            doGenFeature(folder);
        } else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                genFeature(file.getAbsolutePath());
            }

        }

    }

    /**
     * 生成特征具体实现
     *
     * @param file
     */
    private static void doGenFeature(File file) throws Exception {
        //对病例文件进行处理，生成list
        List<String> zhengzhuangList;
        List<String> shezhiList;
        List<String> shetaiList;
        List<String> maiList;
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        br.readLine();
        String medicalHis = br.readLine().split(":")[1].trim();
        String examine = br.readLine().split(":")[1].trim();
        String sheZhi = "";
        String sheTai = "";
        String maiXiang = "";
        String[] strings = examine.split(",|，|。|、|;|；");
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].startsWith("舌质") || strings[i].startsWith("质")) {
                sheZhi = strings[i];
            } else if (strings[i].startsWith("舌苔") || strings[i].startsWith("苔")) {
                sheTai = strings[i];
            } else if (strings[i].startsWith("脉")) {
                maiXiang = strings[i];
            }
        }
        String segExamine = new StringBuilder().append(MyUtil.getSymptom(HanLP.segment(medicalHis)).toString()).
                append(MyUtil.getSymptom(HanLP.segment(examine)).toString()).toString();
        String segSheZhi = MyUtil.getSegResult(forest, sheZhi);
        String segSheTai = MyUtil.getSegResult(forest, sheTai);
        String segMaiXiang = MyUtil.getSegResult(forest, maiXiang);
        zhengzhuangList = Arrays.asList(segExamine.split(","));
        shezhiList = Arrays.asList(segSheZhi.split(","));
        shetaiList = Arrays.asList(segSheTai.split(","));
        maiList = Arrays.asList(segMaiXiang.split(","));
//        System.out.println("seg1 " + segExamine);
//        System.out.println("seg2 " + segSheZhi);
//        System.out.println("seg3 " + segSheTai);
//        System.out.println("seg4 " + segMaiXiang);


    }

    /**
     * 对得到的病例list数据进行匹配
     *
     * @param zhengzhuangList
     * @param shezhiList
     * @param shetaiList
     * @param maiList
     * @param file
     */
    private static List<String> matchRule(List<String> zhengzhuangList, List<String> shezhiList, List<String> shetaiList,
                                           List<String> maiList, File file) {
        List<String> list = new ArrayList<String>();
        int xZhuzheng1 = 0;
        int xZhuzheng2 = 0;
        int xCizheng = 0;
        int xZhushezhi = 0;
        int xZhushetai = 0;
        int xZhumai = 0;
        int xCishezhi = 0;
        int xCishetai = 0;
        int xCimai = 0;

        int gZhuzheng1Left = 0;
        int gZhuzheng1Right = 0;
        int gZhuzheng2Left = 0;
        int gZhuzheng2Right = 0;
        int gZhuzheng3 = 0;
        int gCizheng = 0;
        int gZhushezhi = 0;
        int gZhushetai = 0;
        int gZhumai = 0;
        int gCishezhi = 0;
        int gCishetai = 0;
        int gCimai = 0;

        int pZhuzheng1 = 0;
        int pZhuzheng2 = 0;
        int pCizheng = 0;
        int pZhushezhi = 0;
        int pZhushetai = 0;
        int pZhumai = 0;
        int pCishezhi = 0;
        int pCishetai = 0;
        int pCimai = 0;

        //统计个数
        for (String zhengzhuang : zhengzhuangList) {
            if (xZhuzheng1Set.contains(zhengzhuang)) {
                xZhuzheng1++;
            } 
            if (xZhuzheng2Set.contains(zhengzhuang)) {
                xZhuzheng2++;
            }
            if (xCizhengSet.contains(zhengzhuang)) {
                xCizheng++;
            }


            if (gZhuzheng1LeftSet.contains(zhengzhuang)) {
                gZhuzheng1Left++;
            }
            if (gZhuzheng1RightSet.contains(zhengzhuang)) {
                gZhuzheng1Right++;
            }
            if (gZhuzheng2LeftSet.contains(zhengzhuang)) {
                gZhuzheng2Left++;
            }
            if (gZhuzheng2RightSet.contains(zhengzhuang)) {
                gZhuzheng2Right++;
            }
            if (gZhuzheng3Set.contains(zhengzhuang)) {
                gZhuzheng1Left++;
            }
            if (gZhuzheng3Set.contains(zhengzhuang)) {
                gZhuzheng3++;
            }
            if (gCizhengSet.contains(zhengzhuang)) {
                gCizheng++;
            }


            if (pZhuzheng1Set.contains(zhengzhuang)) {
                xZhuzheng1++;
            }
            if (pZhuzheng2Set.contains(zhengzhuang)) {
                pZhuzheng2++;
            }
            if (pCizhengSet.contains(zhengzhuang)) {
                pCizheng++;
            }
        }

        for (String shezhi : shezhiList) {
            if (xZhushezhiSet.contains(shezhi)) {
                xZhushezhi++;
            }
            if (xCishezhiSet.contains(shezhi)) {
                xCishezhi++;
            }


            if (gZhushezhiSet.contains(shezhi)) {
                gZhushezhi++;
            }
            if (gCishezhiSet.contains(shezhi)) {
                gCishezhi++;
            }
            
            if (pZhushezhiSet.contains(shezhi)) {
                pZhushezhi++;
            }
            if (pCishezhiSet.contains(shezhi)) {
                pCishezhi++;
            }
        }

        for (String shetai : shetaiList) {
            if (xZhushetaiSet.contains(shetai)) {
                xZhushetai++;
            }
            if (xCishetaiSet.contains(shetai)) {
                xCishetai++;
            }

            if (gZhushetaiSet.contains(shetai)) {
                gZhushetai++;
            }
            if (gCishetaiSet.contains(shetai)) {
                gCishetai++;
            }
            
            
            if (pZhushetaiSet.contains(shetai)) {
                pZhushetai++;
            }
            if (pCishetaiSet.contains(shetai)) {
                pCishetai++;
            }
        }

        for (String mai : maiList) {
            if (xZhumaiSet.contains(mai)) {
                xZhumai++;
            }
            if (xCimaiSet.contains(mai)) {
                xCimai++;
            }

            if (gZhumaiSet.contains(mai)) {
                gZhumai++;
            }
            if (gCimaiSet.contains(mai)) {
                gCimai++;
            }
            
            
            if (pZhumaiSet.contains(mai)) {
                pZhumai++;
            }
            if (pCimaiSet.contains(mai)) {
                pCimai++;
            }
        }

        //xRule1,主舌,主脉
        list.add(df.format((double)(xZhuzheng1 + xZhuzheng2)/ 4));
        list.add("1");
        list.add(df.format((double) xZhushetai));
        list.add(df.format(((double) xZhushezhi / 2)));
        list.add(df.format((double) xZhumai));

        //xRule2
        list.add(df.format(0.5 * xZhuzheng1 + 0.5 * (Math.min((double) xZhuzheng2 / 2, 1.0))));
        list.add(df.format(Math.min(xCizheng, 1)));
        list.add(df.format((double) xZhushezhi));
        list.add(df.format((double) xZhushetai / 2));
        list.add(df.format((double) xZhumai));

        //xRule3,任何舌脉
        list.add(df.format(0.5 * xZhuzheng1 + 0.5 * (Math.min((double) xZhuzheng2 / 2, 1.0))));
        list.add(df.format(Math.min((double) xCizheng / 2, 1)));
        list.add(df.format(Math.min(xZhushezhi + xCishezhi, 1)));
        list.add(df.format(Math.min(xZhushetai + xCishetai, 1)));
        list.add(df.format(Math.min(xZhumai + xCimai, 1)));

        //gRule1
        list.add(df.format((double) 1 / 3 * Math.min(((double) gZhuzheng1Left + gZhuzheng1Right) / 2, 1) +
                (double) 1 / 3 * Math.min(((double) gZhuzheng2Left + gZhuzheng2Right) / 2, 1) +
                (double) 1 / 3 * Math.min((double) gZhuzheng3 / 2, 1)));
        list.add(df.format((double) gZhushetai));
        list.add(df.format((double) gZhushezhi));
        list.add(df.format((double) xZhumai));

        //gRule2
        list.add(df.format((double) 1 / 3 * Math.min(((double) gZhuzheng1Left + gZhuzheng1Right) / 2, 1) +
                (double) 1 / 3 * Math.min((double) gZhuzheng2Left + gZhuzheng2Right, 1) +
                (double) 1 / 3 * Math.min((double) gZhuzheng3 / 2, 1)));

        return list;

    }


}
