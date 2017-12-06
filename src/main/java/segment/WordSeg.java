package segment;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import tree.domain.Forest;
import tree.library.Library;
import util.MyUtil;

import java.io.*;
import java.util.*;

/**
 * Created by zsc on 2016/12/12.
 * CustomDictionary中添加新词时，需要删除下面的bin，其他的应该类似，间隔是空格
 * filter用于索引分词的去停用词，只能自己定义，没有api
 * inputCase 为原始病例数据 格式为：证名 \n 症状描述
 * outputCase 为分词后的病例数据 格式为：证名 \n 症状1 症状2 ...
 */
public class WordSeg {
    private static String in = "src\\main\\resources\\解释.txt";
    private static String out1 = "src\\main\\resources\\temp解释1.txt";
    private static String dic = "src\\main\\resources\\解释词典带标签.txt";
    private static String out2 = "src\\main\\resources\\temp解释2.txt";
    private static List<File> fileList = new ArrayList<File>();
    private static String DELIMITER = "\t";
    private static Forest forest;

    public static void main(String args[]) throws Exception {
        //最长分词
//        forest = Library.makeForest(new BufferedReader(new InputStreamReader(new FileInputStream(dic))));
//        BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
//        BufferedWriter bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out1)));
//        String line;
//        String head;
//        String content;
//        while ((line = br1.readLine()) != null) {
//            head = line.split("：")[0];
//            content = line.split("：")[1];
//
//            String temp = MyUtil.getSegResult(forest, content);
//            bw1.write(head + "：" + temp);
//            bw1.newLine();
//
//        }
//        bw1.close();
//
//        //hanlp分词
//        HanLP.Config.ShowTermNature = true;
//
//        BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(in)));
//        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out2)));
//        while ((line = br2.readLine()) != null) {
//            head = line.split("：")[0];
//            content = line.split("：")[1];
//            String temp = StandardTokenizer.segment(content).toString();
//            bw2.write(temp);
//            bw2.newLine();
//
//        }
//        bw2.close();


        HanLP.Config.ShowTermNature = true;    // 关闭词性显示
//        System.out.println("\n***********标准歧义语句*************");
        System.out.println((StandardTokenizer.segment("腿脚不方便")));
//        System.out.println((StandardTokenizer.segment("雀盲：白天视力正常，每至黄昏视物不清")));
//        System.out.println((StandardTokenizer.segment("恶闻声响：不喜欢听到声音，容易惊慌恐惧")));
//        System.out.println((StandardTokenizer.segment("精神萎靡：精神不振，意志消沉")));
//        System.out.println((StandardTokenizer.segment("多虑：有很多担忧和顾虑")));


    }

//    private static void segment() throws Exception {
//        File file = new File(input);
//        getFiles(file);
//        for (File f : fileList) {
//            executeSeg(f);
//        }
//    }
//
//    private static void executeSeg(File file) throws Exception {
//        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
//        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "\\" + file.getName()), "UTF-8"));
//        String line;
//        List<Term> list = new ArrayList<Term>();
//        System.out.println(file.getName());
//        while ((line = br.readLine()) != null) {
//            System.out.println((filter(StandardTokenizer.segment(line.substring(line.indexOf(":") + 1)))));
//            list.addAll(filter(StandardTokenizer.segment(line.substring(line.indexOf(":") + 1))));//只取冒号后面的内容
//        }
//        Term temp = list.remove(list.size() - 1);
//        list.add(0, temp);//将辨证（证名）放在第一位
//        for (Term term : list) {
//            bw.write(term.word);
//            bw.write(DELIMITER);//分隔符为\t
//        }
//        br.close();
//        bw.close();
//    }
//
//    //得到所有文件
//    private static void getFiles(File file) {
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            for (File f : files) {
//                if (f.isDirectory()) {
//                    getFiles(f);
//                } else {
//                    fileList.add(f);
//                }
//            }
//        }
//    }


    //过滤
    private static List<Term> filter(List<Term> termList) {
        List<Term> temp = new ArrayList<Term>();
        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            String nature = term.nature != null ? term.nature.toString() : "空";
            char firstChar = nature.charAt(0);
            switch (firstChar) {
                case 'b': //区别词 正 副
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'm':
                case 'o':
                case 'p':
                case 'q':
                case 'r': //代词 怎样 如何
                case 's':
//                case 't':
                case 'u':
                case 'w':
                case 'y':
                case 'z': //状态词
                    temp.add(term);
                    break;
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'n':
                case 'v':
                case 'x':
                default:
                    if (term.word.length() == 1) {//长度为1，删除，可以理解为没有分出来词，因此删除，最后查询时分出的词，也可以删除停用词
                    temp.add(term);
                    }
            }
        }
        termList.removeAll(temp);
        return termList;
    }

}
