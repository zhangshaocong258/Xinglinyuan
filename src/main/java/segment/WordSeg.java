package segment;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.dictionary.stopword.Filter;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

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
    private static String input = "E:\\Xinglinyuan\\inputCase";
    private static String output = "E:\\Xinglinyuan\\outputCase";
    private static List<File> fileList = new ArrayList<File>();
    private static String DELIMITER = "\t";

    public static void main(String args[]) throws Exception {

        //过滤
        CoreStopWordDictionary.FILTER = new Filter() {
            @Override
            public boolean shouldInclude(Term term) {
                String nature = term.nature != null ? term.nature.toString() : "空";
                char firstChar = nature.charAt(0);
                switch (firstChar) {
                    case 'b': //区别词 正 副
                    case 'z': //状态词
                    case 'r': //代词 怎样 如何
                    case 'm':
                        return true;
                    case 'c':
                    case 'e':

                    case 'o':
                    case 'p':
                    case 'q':

                    case 'u':
                    case 'w':
                    case 'y':
                        return false;
                    case 'd':
                    case 'f':
                    case 'g':
                    case 'h':
                    case 'i':
                    case 'j':
                    case 'k':
                    case 'l':
                    case 'n':
                    case 's':
                    case 't':
                    case 'v':
                    case 'x':

                    default:
                        return term.word.length() > 1 && !CoreStopWordDictionary.contains(term.word);
                }
            }
        };

        HanLP.Config.ShowTermNature = true;    // 关闭词性显示
        System.out.println("\n***********标准歧义语句*************");
        System.out.println((StandardTokenizer.segment("口淡无味，纳减（饮食少），食后腹胀，大便时溏，肢倦乏力,皮肤干，饮食减少，身背着凉即呕吐")));
        System.out.println(NLPTokenizer.segment("口淡无味，纳减（饮食少），食后腹胀，大便时溏，肢倦乏力,皮肤干，饮食减少，身背着凉即呕吐"));
        segment();

//        System.out.println("\n***********过滤语句*************");
        System.out.println(NotionalTokenizer.segment("为何赵本山的徒弟比郭德纲的徒弟忠诚度要高"));

    }

    private static void segment() throws Exception {
        File file = new File(input);
        getFiles(file);
        for (File f : fileList) {
            executeSeg(f);
        }
    }

    private static void executeSeg(File file) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "\\" + file.getName()), "UTF-8"));
        String line;
        List<Term> list = new ArrayList<Term>();
        System.out.println(file.getName());
        while ((line = br.readLine()) != null) {
            System.out.println((filter(StandardTokenizer.segment(line.substring(line.indexOf(":") + 1)))));
            list.addAll(filter(StandardTokenizer.segment(line.substring(line.indexOf(":") + 1))));//只取冒号后面的内容
        }
        Term temp = list.remove(list.size() - 1);
        list.add(0, temp);//将辨证（证名）放在第一位
        for (Term term : list) {
            bw.write(term.word);
            bw.write(DELIMITER);//分隔符为\t
        }
        br.close();
        bw.close();
    }

    //得到所有文件
    private static void getFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    getFiles(f);
                } else {
                    fileList.add(f);
                }
            }
        }
    }


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
                case 't':
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
