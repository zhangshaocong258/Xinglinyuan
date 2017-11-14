package synonym;


import tree.domain.Forest;
import tree.library.Library;
import util.MyUtil;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by zsc on 2017/6/1.
 * 计算同义词相似度
 * 解释 词语: 解释
 * 主题词 词语: 解释
 * 哈工大社会计算与信息检索研究中心同义词词林扩展版
 */
public class WordSyn {

    private static HashMap<String, List<String>> hashMap = new HashMap<String, List<String>>();
    private static List<String> codeList = new ArrayList<String>();
    private static String path1 = "src\\main\\resources\\哈工大社会计算与信息检索研究中心同义词词林扩展版.txt";
    private static String path2 = "src\\main\\resources\\解释词典new2全部添加.txt";
    private static Forest forest;

    static HashSet<String> hashSet = new HashSet<>();

    public static void main(String args[]) throws Exception {
        readDic(path1, path2);
//        System.out.println(sim("起重机", "器械"));
//        System.out.println(sim("失眠", "出汗"));
//        explainSim("饮食不佳：缺乏食欲，饮食无味", "多虑：有很多担忧和顾虑");// 夜不安寐 失眠
//        explainSim("饮食不佳：缺乏食欲，饮食无味", "纳减：食欲减退，吃饭减少");// 夜不安寐 失眠

        System.out.println("准确率 " + calAcc());
//        for (String str : hashSet) {
//            System.out.println(str);
//        }
    }

    /**
     * 读取同义词词林并存储
     *
     * @param path1
     * @throws Exception
     */
    private static void readDic(String path1, String path2) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path1)));
        String line;
        while ((line = br.readLine()) != null) {
            String[] strings = line.split(" ");
            String coding = strings[0].trim();
            codeList.add(coding);
            for (int i = 1; i < strings.length; i++) {
                if (hashMap.containsKey(strings[i])) {
                    hashMap.get(strings[i].trim()).add(coding);
                } else {
                    hashMap.put(strings[i].trim(), new ArrayList<String>());
                    hashMap.get(strings[i].trim()).add(coding);
                }
            }
        }
        forest = Library.makeForest(new BufferedReader(new InputStreamReader(new FileInputStream(path2))));
    }


    private static double calAcc() throws Exception {
        //读取同义词new作为判断标准
        String in = "src\\main\\resources\\同义词new.txt";
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
        HashMap<String, List<String>> synonymyMap = new HashMap<String, List<String>>();
        String line;
        while ((line = br.readLine()) != null) {
            String coding = line.split("：")[0].trim();
            String[] strings = line.split("：")[1].trim().split(",");
            for (int i = 0; i < strings.length; i++) {
                if (synonymyMap.containsKey(strings[i])) {
                    synonymyMap.get(strings[i].trim()).add(coding);
                } else {
                    synonymyMap.put(strings[i].trim(), new ArrayList<String>());
                    synonymyMap.get(strings[i].trim()).add(coding);

                }
            }
        }

        String path1 = "src\\main\\resources\\解释.txt";
        String path2 = "src\\main\\resources\\主题词.txt";
        HashMap<String, String> explain = new HashMap<String, String>();
        HashMap<String, String> standard = new HashMap<String, String>();
        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(path1)));
        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(path2)));
        while ((line = br1.readLine()) != null) {
            String[] strings = line.split("：");
            explain.put(strings[0], line);//改改改改改改改改改改改改改改改改strings[1]
        }
        while ((line = br2.readLine()) != null) {
            String[] strings = line.split("：");
            standard.put(strings[0], line);//改改改改改改改改改改改改改改改改strings[1]
        }
        String tempResult1, tempResult2 = null;
        int count = 0;
        //遍历解释词典，得到最大匹配的主题词，并计算最后的准确率
        for (String key1 : explain.keySet()) {
//            System.out.println("key1 " + key1);
            double sim = 0;
            tempResult1 = key1;
            for (String key2 : standard.keySet()) {
//                System.out.println("key2 " + key2);
                double temp = explainSim(explain.get(key1), standard.get(key2));
                if (sim < temp) {
                    sim = temp;
                    tempResult2 = key2;
                }
            }
            //计算准确度(判断两个词在同义词词典中是否属于同一个标签)

                for (String str : synonymyMap.get(tempResult1)) {
                    if (synonymyMap.get(tempResult2).contains(str)) {
                        count++;
                        break;
                    } else {
                        System.out.println("症状：" + tempResult1 + "  匹配的主题词：" + tempResult2 + "  相似度： " + sim);
                    }
                }

        }
        return (double) count / explain.size();
    }


    /**
     * 计算两个解释语句的相似度
     *
     * @param sentence1 第一个句子
     * @param sentence2 第二个句子
     * @return
     */
    private static double explainSim(String sentence1, String sentence2) {
        double result = 0;
        DecimalFormat df = new DecimalFormat("######0.0000");
        List<String> list1 = Arrays.asList(sentence1.split("，|："));
        List<String> list2 = Arrays.asList(sentence2.split("，|："));
        List<String> voc1 = new ArrayList<String>();
        List<String> voc2 = new ArrayList<String>();
        List<String> acc1 = new ArrayList<String>();
        List<String> acc2 = new ArrayList<String>();
        //存储分词后的词组
        for (String str : list1) {
            String temp = MyUtil.getSegExplain(forest, str);
            if (temp.length() > 0) {
                voc1.add(temp);
            }
        }
        for (String str : list2) {
            String temp = MyUtil.getSegExplain(forest, str);
            if (temp.length() > 0) {
                voc2.add(temp);
            }
        }
//        System.out.println("voc1 " + voc1.toString());
//        System.out.println("voc2 " + voc2.toString());
        //计算相似度 str1 A,B,C,  D,E,    str2 M,N,O   P,Q
        List<String> accList = new ArrayList<String>();//A,B,C,  D,E,的double值
        for (String str1 : voc1) {
            List<Double> subList = null;
            for (String str2 : voc2) {
                String[] v1 = str1.split(",");
                String[] v2 = str2.split(",");
                List<Double> tempList = new ArrayList<Double>();
                for (String s1 : v1) {
                    double tempS1 = 0;
                    for (String s2 : v2) {
                        double tempAcc = sim(s1, s2);
                        if (tempS1 < tempAcc) {
                            tempS1 = tempAcc;
                        }
                    }
                    tempList.add(tempS1);
                }
//                System.out.println("templist " + tempList.toString());
                //比较当前str1的概率和哪个大，取大的值
                double oldD = 0;
                double newD = 0;
                if (subList == null) {
                    subList = new ArrayList<Double>();
                    subList.addAll(tempList);
                }
                for (double d : subList) {
                    oldD += d;
                }

                for (double d : tempList) {
                    newD += d;
                }
                if (oldD < newD) {
                    subList.clear();
                    subList.addAll(tempList);
                }
            }
//            System.out.println("sublist " + subList.toString());
            StringBuilder sb = new StringBuilder();
            for (double d : subList) {
                sb.append(df.format(d)).append(",");
            }
            accList.add(sb.toString());
        }
//        System.out.println("accList " + accList.toString() + "  size " + accList.size());

        //计算相似度
        double alpha = 0.6;
        double beta = 0.4;
        int count = 0;
        for (String str : accList) {
            count++;
            String[] strings = str.split(",");
            for (int i = 0; i < strings.length; i++) {
                result += Double.valueOf(strings[i]);
            }
            result =  result / strings.length;
        }
        result /= count;
//        System.out.println("result " + result);
        return result;
    }

    /**
     * 根据田久乐论文计算两个词的相似度
     *
     * @param word1
     * @param word2
     * @return
     */
    private static double sim(String word1, String word2) {
        //基于实际情况，当两个词完全相同时，直接返回1
        if (word1.equals(word2)) {
            return 1.0;
        }
        List<String> codingList1 = hashMap.get(word1);
        List<String> codingList2 = hashMap.get(word2);

        double result = 0;
        //若词典中没有该词，则相似度为0
        if (codingList1 == null || codingList2 == null) {
//            if (codingList1 == null) {
//                System.out.println("没有 " + word1 + "end1");
//            }
//            if (codingList2 == null) {
//                System.out.println("没有 " + word2+ "end2");
//            }
            return result;
        }
//        if (codingList1.size() > 1) {
//            hashSet.add(word1);
//        }
//        if (codingList2.size() > 1) {
//            hashSet.add(word2);
//        }
        for (String coding1 : codingList1) {
            for (String coding2 : codingList2) {
                double temp = calculateSim(coding1, coding2);
                if (result < temp) {
                    result = temp;
                }
            }
        }
        return result;
    }

    private static double calculateSim(String coding1, String coding2) {
        if (coding1.equals(coding2)) {
            if (coding1.endsWith("=")) {
                return 1;
            } else if (coding1.endsWith("#")) {
                return 0.5;
            } else if (coding1.endsWith("@")) {
                return 1;
            }
        }
        int n;//分支层几点总数
        int k;//分支之间距离
        int index = 0;//前几个分支相同，最大为6
        char[] chars1 = coding1.toCharArray();
        char[] chars2 = coding2.toCharArray();
        for (int i = 0; i < chars1.length; i++) {
            if (chars1[i] == chars2[i]) {
                index++;
            } else {
                break;
            }
        }
        if (index == 0) {
            return 0.1;
        } else if (index == 1 || index == 3 || index == 4 || index == 6) {
            k = Math.abs(chars1[index] - chars2[index]);
        } else {
            k = Math.abs((chars1[index] - '0') * 10 + (chars1[index + 1] - '0') -
                    ((chars2[index] - '0') * 10 + (chars2[index + 1] - '0')));
        }

        String temp = coding1.substring(0, index);
        int max = 0;
        int tempMax = 0;
        boolean flag = false;
        for (String str : codeList) {
            if (str.startsWith(temp)) {
                flag = true;
                max++;
            } else {
                if (flag) {
                    break;
                }
                tempMax++;
            }

        }
        max += tempMax - 1;
        if (index == 1) {
            n = codeList.get(max).charAt(index) - 'a' + 1;
        } else if (index == 4) {
            n = codeList.get(max).charAt(index) - 'A' + 1;
        } else if (index == 3 || index == 6) {
            n = codeList.get(max).charAt(index) - '0';
        } else {
            n = (codeList.get(max).charAt(index) - '0') * 10 + (codeList.get(max).charAt(index + 1) - '0');
        }
//        System.out.println(codeList.get(max));
//        System.out.println("k " + k + " n " + n);
        double result = Math.cos(Math.toRadians(n)) * (double) (n - k + 1) / n;
        if (index == 1) {
            return 0.6 * result;
        } else if (index == 2 || index == 3) {
            return 0.8 * result;
        } else if (index == 4) {
            return 0.9 * result;
        } else {
            return 0.96 * result;
        }

    }
}
