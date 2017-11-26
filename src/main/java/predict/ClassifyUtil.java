package predict;

import preprocess.Repository;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zsc on 2017/11/26.
 * 使用weka进行预测
 * 将各个模块拆分
 */
public class ClassifyUtil {
    private static DecimalFormat df = new DecimalFormat("######0.0000");

    private static final String synonymDicPath = "src\\main\\resources\\同义词new.txt";
    private static String modelOutPath = "src\\main\\resources\\特征\\xinglinyuan.model";
    private static String dataInPath = "src\\main\\resources\\特征\\train.arff";
    public static void main(String args[])  throws Exception{
        List<String> zhengzhuangList = new ArrayList<String>();
        List<String> shezhiList = new ArrayList<String>();
        List<String> shetaiList = new ArrayList<String>();
        List<String> maiList = new ArrayList<String>();
        zhengzhuangList.add("心悸");
        zhengzhuangList.add("胸闷");
        zhengzhuangList.add("头晕");
        zhengzhuangList.add("疲乏无力");
        zhengzhuangList.add("少气懒言");
        zhengzhuangList.add("畏寒肢冷");

        shezhiList.add("淡白");

        shetaiList.add("薄");
        shetaiList.add("白");

        maiList.add("沉迟");

        Repository.readDic(synonymDicPath);
        Repository.genRepository();
        train(modelOutPath, dataInPath,true);
        predict(zhengzhuangList, shezhiList, shetaiList, maiList);

    }

    public static void train(String modelOutPath, String dataInPath, boolean reset) throws Exception{
        File file = new File(modelOutPath);
        if (file.exists()) {
            if (!reset) {
                return;
            }
        }
        ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(file));
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(new File(dataInPath));
        Instances instancesTrain = arffLoader.getDataSet();
        instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
        MultilayerPerceptron classifier = new MultilayerPerceptron();
        classifier.buildClassifier(instancesTrain);
        oos.writeObject(classifier);
        oos.close();
    }

    public static void predict(List<String> zhengzhuangList, List<String> shezhiList, List<String> shetaiList, List<String> maiList) throws Exception {
        //生成特征
        List<String> list = Repository.matchRule(zhengzhuangList, shezhiList, shetaiList, maiList);
        System.out.println("list " + list.toString());

        //生成临时文件用于预测
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src\\main\\resources\\特征\\format.arff")));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("src\\main\\resources\\特征\\temp.arff")));
        String line;
        while ((line = br.readLine()) != null) {
            bw.write(line);
            bw.newLine();
        }
        for (String str : list) {
            bw.write(str);
            bw.write(",");
        }
        bw.write("x");
        bw.newLine();
        bw.close();

        //读入模型进行预测
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(modelOutPath));
        MultilayerPerceptron classifier = (MultilayerPerceptron) ois.readObject();
        ArffLoader arffLoader = new ArffLoader();
        arffLoader.setFile(new File("src\\main\\resources\\特征\\temp.arff"));
        Instances instancesTest = arffLoader.getDataSet();
        instancesTest.setClassIndex(instancesTest.numAttributes() - 1);
        double[] probability = classifier.distributionForInstance(instancesTest.instance(0));
        List<String> pro = new ArrayList<String>();
        for (int i = 0; i < probability.length; i++) {
            pro.add(df.format(probability[i]));
        }
        System.out.println(pro);
    }
}
