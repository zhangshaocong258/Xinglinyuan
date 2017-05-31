package corpus.qq;/*
 * 功能：将QQ词库qpyd文件中包含的词语转为txt存储，一个词语占一行
 */

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QQqpyd2Txt {
    private static String input = "C:\\Users\\zsc\\Desktop\\qq语料";
    private static String output = "C:\\Users\\zsc\\Desktop\\qq语料txt";
    private static HashMap<String, String> map = new HashMap<String, String>();

    /**
     * 功能: 将输入的qypd文件转为txt文件
     *
     * @param inputPath:  输入的qpyd文件路径
     * @param outputPath: 输出的txt文件路径
     * @return : void
     */
    public static void transBdict2Txt(String inputPath, String outputPath) throws Exception {
        List<String> wordList = new ArrayList<String>();
        wordList = QQqpydReader.readQpydFile(inputPath);

        //create outputDirs if not exists
        File outFile = new File(outputPath);
        outFile.getParentFile().mkdirs();

        PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
        for (int i = 0; i < wordList.size(); i++) {
            writer.println(wordList.get(i));
        }
        writer.close();
        System.out.println(outputPath + " created \ntotal " + wordList.size() + " words");

    }

    private static void getFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (f.isDirectory()) {
                    getFiles(f);
                } else {
                    map.put(f.getAbsolutePath(), output + "\\" + f.getName().substring(0, f.getName().indexOf(".")) + ".txt");
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        String inputPath = "G:/各大输入法词库/QQ/单线程下载/QQ/城市地区/安徽/城市信息(安庆).qpyd";
//        String outputPath = "G:/各大输入法词库/QQ/单线程下载/test/城市信息(安庆).txt";
//        transBdict2Txt(inputPath, outputPath);
        File inputFile = new File(input);
        getFiles(inputFile);
        for (Map.Entry<String, String> entry : map.entrySet()) {
//            System.out.println(entry.getKey()+ "\t" + entry.getValue());
            transBdict2Txt(entry.getKey(), entry.getValue());

        }
    }
}
