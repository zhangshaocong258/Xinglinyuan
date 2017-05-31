package corpus.sougou;


/**
 * 功能：输入scel的词库文件路径,根据指定路径生成包含该词库文件的词条的txt文件
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;

/**
 * 解析sogo词库工具类
 */
public class SogouScel2Txt {

    private static String input = "C:\\Users\\zsc\\Desktop\\搜狗中医语料";
    private static String output = "C:\\Users\\zsc\\Desktop\\搜狗中医语料txt";
    private static HashMap<String, String> map = new HashMap<String, String>();

    public static void main(String[] args) throws Exception {
        File inputFile = new File(input);
        getFiles(inputFile);
        for (Entry<String, String> entry : map.entrySet()) {
//            System.out.println(entry.getKey()+ "\t" + entry.getValue());
            sogou(entry.getKey(), entry.getValue(), false);

        }

//        sogou("C:\\Users\\zsc\\Desktop\\搜狗中医语料\\JGYL.scel", "C:\\Users\\zsc\\Desktop\\搜狗中医语料\\JGYL.txt", false);
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

    /**
     * 读取scel的词库文件,生成txt格式的文件
     *
     * @param inputPath  输入路径
     * @param outputPath 输出路径
     * @param isAppend   是否拼接追加词库内容,true 代表追加,false代表重建
     **/

    public static void sogou(String inputPath, String outputPath, boolean isAppend) throws IOException {
        File file = new File(inputPath);
        if (!isAppend) {
            if (Files.exists(Paths.get(outputPath), LinkOption.values())) {
                System.out.println("存储此文件已经删除");
                Files.deleteIfExists(Paths.get(outputPath));

            }
        }
        RandomAccessFile raf = new RandomAccessFile(outputPath, "rw");

        int count = 0;
        SougouScelMdel model = new SougouScelReader().read(file);
        Map<String, List<String>> words = model.getWordMap(); //词<拼音,词>
        Set<Entry<String, List<String>>> set = words.entrySet();
        Iterator<Entry<String, List<String>>> iter = set.iterator();
        while (iter.hasNext()) {
            Entry<String, List<String>> entry = iter.next();
            List<String> list = entry.getValue();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                String word = list.get(i);
                //System.out.println(word);
                raf.seek(raf.getFilePointer());
                raf.write((word + "\n").getBytes());//写入txt文件
                count++;
            }
        }
        raf.close();
        System.out.println(outputPath + "生成成功，总写入" + count + "个词条");
    }

}
