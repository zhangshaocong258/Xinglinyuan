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
    private static Instances instancesTest = null;
    private static Classifier classifier = null;

    public static void main(String args[]) {
        String[] strings = {"i", "a", "o"};
        double[] doubles = {0.1, 0.3, 0.5, 0.7};
        long a = System.currentTimeMillis();
        baseClassify();
//        for (int i = 0; i < strings.length; i++) {
//            for (int j = 0; j < doubles.length; j++) {
//                network(strings[i], doubles[j], "y");
//            }
//        }
//        for (int i = 0; i < strings.length; i++) {
//            for (int j = 0; j < doubles.length; j++) {
//                network(strings[i], doubles[j], "n");
//            }
//        }
//        network();
        long b = System.currentTimeMillis();
        System.out.println("time " + (b - a));

    }


    /**
     * 神经网络
     */
    private static void network() {
        try {

            //outall:0.9796 outall2:0.9851 delete: 0.8592 delete0: 0.9148 outRule使用规则: 0.9574(时间83085)keras:0.9425 outNo没有使用规则:0.9777(时间795816,616029) outh:0.9296(时间10134)
            //xgboost： outRule: 0.9590 outNo2: 0.9670
            //1080:分类器的正确率：0.9657089898053753 time 223647.
            //1080：八折 分类器的正确率：0.9629286376274329time 164161
            //no1080分类器的正确率：0.9b42446709916589time 1075425
            File file = new File("src\\main\\resources\\特征\\out1280.arff");
            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instancesTrain = arffLoader.getDataSet();
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);


            /**
             * begin
             */
            MultilayerPerceptron classifier = new MultilayerPerceptron();
            classifier.buildClassifier(instancesTrain);
            classifier.setTrainingTime(300);
//            classifier.setAutoBuild(true);
            classifier.setHiddenLayers("o");
//            classifier.setLearningRate(0.1);

            Evaluation testingEvaluation = new Evaluation(instancesTrain);
            testingEvaluation.crossValidateModel(classifier, instancesTrain, 10, new Debug.Random(1));
//            System.out.println("fn " + testingEvaluation.falseNegativeRate(0) );
//            System.out.println("fp " + testingEvaluation.falsePositiveRate(0));
//            System.out.println("tn " + testingEvaluation.trueNegativeRate(0));
            System.out.println("tp " + (double)testingEvaluation.numTrueNegatives(0) / (testingEvaluation.numTruePositives(0) + testingEvaluation.numFalsePositives(0)));
            System.out.println("tp " + (double)testingEvaluation.numTrueNegatives(1) / (testingEvaluation.numTruePositives(1) + testingEvaluation.numFalsePositives(1)));
            System.out.println("tp " + (double)testingEvaluation.numTrueNegatives(2) / (testingEvaluation.numTruePositives(2) + testingEvaluation.numFalsePositives(2)));

            System.out.println("分类器召回率 " +   testingEvaluation.recall(0));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(1));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(2));
            System.out.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));
            /**
             * end
             */

            /*file = new File("src\\main\\resources\\特征\\test.arff");
            arffLoader.setFile(file);
            instancesTest = arffLoader.getDataSet();
            instancesTest.setClassIndex(instancesTest.numAttributes() - 1);
            MultilayerPerceptron classifier = new MultilayerPerceptron();
            classifier.buildClassifier(instancesTrain);
            double[] r = classifier.distributionForInstance(instancesTest.instance(60));
            for (int i = 0; i < r.length; i++) {
                System.out.println(r[i]);
            }
            System.out.println(classifier.classifyInstance(instancesTest.instance(60))+",,,"+instancesTest.instance(60).classValue());
*/

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
            File file = new File("src\\main\\resources\\特征\\outno1280.arff");
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
            //1080:分类器的正确率：0.9221501390176089 time 2755

            classifier = (Classifier) Class.forName("weka.classifiers.trees.J48").newInstance();
            String options[]=new String[3];//训练参数数组
            options[0]="-R";//使用reduced error pruning
            options[1]="-M";//叶子上的最小实例数
            options[2]="3";//set叶子上的最小实例数
            classifier.setOptions(options);//设置训练参数

            classifier.buildClassifier(instancesTrain);


            Evaluation testingEvaluation = new Evaluation(instancesTrain);

//            testingEvaluation.evaluateModel(classifier, instancesTest);

            testingEvaluation.crossValidateModel(classifier, instancesTrain, 10, new Debug.Random(1));

            System.out.println("tp " + (double)testingEvaluation.numTruePositives(0) / (testingEvaluation.numTruePositives(0) + testingEvaluation.numFalsePositives(0)));
            System.out.println("tp " + (double)testingEvaluation.numTruePositives(1) / (testingEvaluation.numTruePositives(1) + testingEvaluation.numFalsePositives(1)));
            System.out.println("tp " + (double)testingEvaluation.numTruePositives(2) / (testingEvaluation.numTruePositives(2) + testingEvaluation.numFalsePositives(2)));


            System.out.println("分类器召回率 " +   testingEvaluation.recall(0));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(1));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(2));

            System.out.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    /**
     * 神经网络
     */
    private static void network( String h, double l, String f) {
        try {

            //outall:0.9796 outall2:0.9851 delete: 0.8592 delete0: 0.9148 outRule使用规则: 0.9574(时间83085)keras:0.9425 outNo没有使用规则:0.9777(时间795816,616029) outh:0.9296(时间10134)
            //xgboost： outRule: 0.9590 outNo2: 0.9670
            //1080:分类器的正确率：0.9657089898053753 time 223647.
            //1080：八折 分类器的正确率：0.9629286376274329time 164161
            //no1080分类器的正确率：0.9b42446709916589time 1075425
            File file;
            if (f.equals("y")) {
                file = new File("src\\main\\resources\\特征\\out1280.arff");
            } else {
                file = new File("src\\main\\resources\\特征\\outno1280.arff");
            }

            ArffLoader arffLoader = new ArffLoader();
            arffLoader.setFile(file);
            instancesTrain = arffLoader.getDataSet();
            instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);


            /**
             * begin
             */
            MultilayerPerceptron classifier = new MultilayerPerceptron();
            classifier.setTrainingTime(300);
            classifier.setLearningRate(l);
            classifier.setHiddenLayers(h);
            classifier.buildClassifier(instancesTrain);
            Evaluation testingEvaluation = new Evaluation(instancesTrain);
            testingEvaluation.crossValidateModel(classifier, instancesTrain, 10, new Debug.Random(1));
            System.out.println("**********************");
            System.out.println("learning rate " + l);
            System.out.println("hidden layer " + h);
            System.out.println("f " + f);
            System.out.println("tp " + (double)testingEvaluation.numTruePositives(0) / (testingEvaluation.numTruePositives(0) + testingEvaluation.numFalsePositives(0)));
            System.out.println("tp " + (double)testingEvaluation.numTruePositives(1) / (testingEvaluation.numTruePositives(1) + testingEvaluation.numFalsePositives(1)));
            System.out.println("tp " + (double)testingEvaluation.numTruePositives(2) / (testingEvaluation.numTruePositives(2) + testingEvaluation.numFalsePositives(2)));

//            double all0 = testingEvaluation.numTruePositives(0) + testingEvaluation.numTrueNegatives(0) + testingEvaluation.numFalsePositives(0) + testingEvaluation.numFalseNegatives(0);
//            double all1 = testingEvaluation.numTruePositives(1) + testingEvaluation.numTrueNegatives(1) + testingEvaluation.numFalsePositives(1) + testingEvaluation.numFalseNegatives(1);
//            double all2 = testingEvaluation.numTruePositives(2) + testingEvaluation.numTrueNegatives(2) + testingEvaluation.numFalsePositives(2) + testingEvaluation.numFalseNegatives(2);
//            System.out.println("all0" + all0);
//            System.out.println("all1" + all1);
//            System.out.println("all2" + all2);
//            System.out.println("tp " + (double)(testingEvaluation.numTruePositives(0) + testingEvaluation.numTrueNegatives(0))/ all0);
//            System.out.println("tp " + (double)(testingEvaluation.numTruePositives(1) + testingEvaluation.numTrueNegatives(1)) / all1);
//            System.out.println("tp " + (double)(testingEvaluation.numTruePositives(2) + testingEvaluation.numTrueNegatives(2)) / all2);


            System.out.println("分类器召回率 " +   testingEvaluation.recall(0));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(1));
            System.out.println("分类器召回率 " +   testingEvaluation.recall(2));
            System.out.println("分类器的正确率：" + (1 - testingEvaluation.errorRate()));
            /**
             * end
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
