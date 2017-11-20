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
//        baseClassify();
        network();
    }


    /**
     * 神经网络
     */
    private static void network() {
        try {
            File file = new File("src\\main\\resources\\特征\\out2.arff");
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
            File file = new File("src\\main\\resources\\特征\\out2.arff");
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instancesTrain = arffLoader.getDataSet();

//            file = new File("src\\main\\resources\\特征\\test.arff");
//            arffLoader.setFile(file);
//            instancesTest = arffLoader.getDataSet();

            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
//            instancesTest.setClassIndex(instancesTest.numAttributes() - 1);


//            classifier = (Classifier) Class.forName("weka.classifiers.bayes.NaiveBayes").newInstance();
//            classifier = (Classifier) Class.forName("weka.classifiers.functions.LibSVM").newInstance();
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
