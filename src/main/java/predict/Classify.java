package predict;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Debug;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;

/**
 * Created by zsc on 2017/11/18.
 * 使用不同的分类算法进行预测
 */
public class Classify {
    private static Instances instancesTrain = null;
//    private static Instances instancesTest = null;
    private static Classifier classifier = null;

    public static void main(String args[]) {
        long a = System.currentTimeMillis();
//        baseClassify();
        network();
        long b = System.currentTimeMillis();
        System.out.println("time " + (b - a));

    }


    /**
     * 神经网络
     */
    private static void network() {
        try {

            //outall:0.9796 outall2:0.9851 delete: 0.8592 delete0: 0.9148 outRule: 0.9574(时间83085)keras:0.9425 outNo:0.9777(时间795816)
            //xgboost： outRule: 0.9590 outNo2: 0.9670
            File file = new File("src\\main\\resources\\特征\\outRule.arff");
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instancesTrain = arffLoader.getDataSet();
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
            MultilayerPerceptron classifier = new MultilayerPerceptron();
            classifier.buildClassifier(instancesTrain);
            Evaluation testingEvaluation = new Evaluation(instancesTrain);
            testingEvaluation.crossValidateModel(classifier, instancesTrain, 10, new Debug.Random(1));

            System.out.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * weka.classifiers.bayes.NaiveBayes
     * weka.classifiers.trees.J48
     * weka.classifiers.rules.ZeroR
     * weka.classifiers.functions.LibSVM
     *
     */
    private static void baseClassify() {
        try {
            File file = new File("src\\main\\resources\\特征\\outNo.arff");
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instancesTrain = arffLoader.getDataSet();

//            file = new File("src\\main\\resources\\特征\\test.arff");
//            arffLoader.setFile(file);
//            instancesTest = arffLoader.getDataSet();

            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
//            instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

            //outall：0.8759 outall2:0.85 delete: 0.81111  delete0:0.9481 outRule: 0.8388888 outNo:0.9648
//            classifier = (Classifier) Class.forName("weka.classifiers.bayes.NaiveBayes").newInstance();

            //outall:0.8944 outall2:0.9 delete: 0.81111 delete0: 0.8926 outRule:0.8870 outNo: 0.9444
//            classifier = (Classifier) Class.forName("weka.classifiers.functions.LibSVM").newInstance();

            //outall:0.9296 outall2:0.9333 delete:0.7814 delete0: 0.83333 outRule: 0.89444 outNo: 0.9240
            classifier = (Classifier) Class.forName("weka.classifiers.trees.J48").newInstance();

            classifier.buildClassifier(instancesTrain);


            Evaluation testingEvaluation = new Evaluation(instancesTrain);

//            testingEvaluation.evaluateModel(classifier, instancesTest);

            testingEvaluation.crossValidateModel(classifier, instancesTrain, 10, new Debug.Random(1));



            System.out.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
