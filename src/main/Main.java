package main;

import alg.Backprop;
import datasets.Fn;
import ui.GUI;
import javax.swing.*;

public class Main {

    // ハイパーパラメータ
    public static final int  HIDDEN1_SIZE = 32;      // 隠れ層1のニューロン数
    public static final int HIDDEN2_SIZE = 16;      // 隠れ層2のニューロン数
    public static final double LEARNING_RATE = 0.002; // 学習率（Adamに適した値）
    public static final double L2_LAMBDA = 0.0001;   // L2正則化の強度
    public static final int EPOCHS = 3000;          // エポック数
    public static final int TRAIN_SIZE = 1000;      // 訓練データ数
    public static final int TEST_SIZE = 200;        // テストデータ数
    private static Thread trainingThread;
    private static volatile boolean stopTraining = false;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);

            // Stopボタンのアクション設定
            gui.setStopAction(e -> {
                stopTraining = true;
                gui.setStopEnabled(false);
                gui.setStatus("Stopping training...");
            });

            // Reloadボタンのアクション設定
            gui.setReloadAction(e -> {
                // 実行中の学習を停止
                if (trainingThread != null && trainingThread.isAlive()) {
                    stopTraining = true;
                    gui.setStopEnabled(false);
                    try {
                        trainingThread.join(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                // 新しい学習を開始
                stopTraining = false;
                gui.getViewer().clearHistory();
                gui.getViewer().clearPredictions();
                trainingThread = new Thread(() -> runTraining(gui));
                trainingThread.start();
            });

            // 初回の学習を開始
            trainingThread = new Thread(() -> runTraining(gui));
            trainingThread.start();
        });
    }

    private static void runTraining(GUI gui) {
        gui.setReloadEnabled(false);
        gui.setStopEnabled(true);  // 学習開始時にStopボタンを有効化
        gui.setProgress(0);


        try {
            // データセットの生成
            gui.setStatus("Generating dataset...");
            Fn fn = gui.getSelectedFunction();

            double[] trainX = fn.getTrainX();
            double[] trainY = fn.getTrainY();
            double[] testX = fn.getTestX();
            double[] testY = fn.getTestY();

            // ビューアーの設定
            gui.getViewer().setCurrentFunction(fn);
            gui.getViewer().setRanges(fn.getXRange(), fn.getYRange());
            gui.getViewer().setTrainData(trainX, trainY);
            gui.getViewer().setTestData(testX, testY);

            // ニューラルネットワークの初期化
            gui.setStatus("Initializing neural network...");
            System.out.println("\n=== Neural Network Configuration ===");
            System.out.println("Fn: " + fn.getName() + " - " + fn.getDescription());
            System.out.println("Architecture: Input(1) -> Hidden1(" + HIDDEN1_SIZE +
                    ") -> Hidden2(" + HIDDEN2_SIZE + ") -> Output(1)");
            System.out.println("Activation: tanh (hidden layers), identity (output layer)");
            System.out.println("Optimizer: Adam (lr=" + LEARNING_RATE + ")");
            System.out.println("Regularization: L2 (lambda=" + L2_LAMBDA + ")");
            System.out.println("Noise Rate: " + gui.getNoiseRate());
            System.out.println("=====================================\n");

            Backprop nn = new Backprop(1, HIDDEN1_SIZE, HIDDEN2_SIZE, 1, LEARNING_RATE, L2_LAMBDA);

            // 学習
            gui.setStatus("Training neural network...");
            double bestTestLoss = Double.MAX_VALUE;
            int bestEpoch = 0;

            // 早期停止のためのパラメータ
            int patience = 500;
            int patienceCounter = 0;

            for (int epoch = 0; epoch < EPOCHS; epoch++) {
                if (stopTraining) {
                    gui.setStatus("Training stopped by user");
                    break;
                }

                double totalLoss = 0.0;

                // ミニバッチ風にランダムな順序で学習
                int[] indices = new int[TRAIN_SIZE];
                for (int i = 0; i < TRAIN_SIZE; i++) indices[i] = i;
                shuffleArray(indices);

                // エポックごとに全訓練データで学習
                for (int i : indices) {
                    if (stopTraining) break;
                    double loss = nn.train(trainX[i], trainY[i]);
                    totalLoss += loss;
                }

                if (stopTraining) break;

                double avgLoss = totalLoss / TRAIN_SIZE;

                // 進捗更新
                gui.setProgress((epoch + 1) * 100 / EPOCHS);

                // 10エポックごとに表示更新
                if ((epoch + 1) % 10 == 0) {
                    gui.getViewer().updatePredictions(nn, epoch + 1);
                    gui.getViewer().addLossHistory(epoch + 1, avgLoss);

                    // 50エポックごとに詳細を表示
                    if ((epoch + 1) % 50 == 0) {
                        double testLoss = evaluateTestSet(nn, testX, testY);
                        System.out.printf("Epoch %4d: Train Loss = %.6f, Test Loss = %.6f%n",
                                epoch + 1, avgLoss, testLoss);
                        gui.setStatus(String.format("Epoch %d: Train Loss = %.6f, Test Loss = %.6f",
                                epoch + 1, avgLoss, testLoss));

                        // 早期停止の判定
                        if (testLoss < bestTestLoss) {
                            bestTestLoss = testLoss;
                            bestEpoch = epoch + 1;
                            patienceCounter = 0;
                        } else {
                            patienceCounter += 50;
                            if (patienceCounter >= patience && epoch > 1000) {
                                System.out.println("Early stopping triggered at epoch " + (epoch + 1));
                                break;
                            }
                        }
                    }
                }
            }

            if (!stopTraining) {
                // 最終評価
                gui.setStatus("Training completed!");
                System.out.println("\n=== Training Completed! ===");
                double finalTestLoss = evaluateTestSet(nn, testX, testY);
                System.out.printf("Final test loss: %.6f%n", finalTestLoss);
                System.out.printf("Best test loss: %.6f (at epoch %d)%n", bestTestLoss, bestEpoch);

                // ノイズのないデータでの評価
                System.out.println("\n=== Evaluation on Clean Data ===");
                double cleanLoss = evaluateCleanData(nn, fn);
                System.out.printf("Loss on clean fn: %.6f%n", cleanLoss);

                // サンプル予測の表示
                System.out.println("\n=== Sample Predictions ===");
                System.out.println("       x        |    True y      |   Prediction   |     Error");
                System.out.println("----------------|----------------|----------------|---------------");
                for (int i = 0; i < Math.min(10, testX.length); i++) {
                    int idx = i * (testX.length / 10);
                    double x = testX[idx];
                    double yTrue = testY[idx];
                    double yPred = nn.predict(x);
                    double error = Math.abs(yTrue - yPred);
                    System.out.printf("%14.6f | %14.6f | %14.6f | %14.6f%n", x, yTrue, yPred, error);
                }

                // 最終的な予測を表示
                gui.getViewer().updatePredictions(nn, bestEpoch);
            }

        } catch (Exception e) {
            e.printStackTrace();
            gui.setStatus("Error: " + e.getMessage());
        } finally {
            gui.setReloadEnabled(true);
            gui.setStopEnabled(false);  // 学習終了時にStopボタンを無効化
            if (!stopTraining) {
                gui.setProgress(100);
            }
        }
    }

    /**
     * クリーンなデータ（ノイズなし）での評価
     */
    private static double evaluateCleanData(Backprop nn, Fn fn) {
        double totalLoss = 0.0;
        int numPoints = 100;

        double[] xRange = fn.getXRange();
        for (int i = 0; i < numPoints; i++) {
            double x = xRange[0] + (xRange[1] - xRange[0]) * i / (numPoints - 1);
            double yTrue = fn.compute(x);
            double yPred = nn.predict(x);
            double diff = yPred - yTrue;
            totalLoss += 0.5 * diff * diff;
        }
        return totalLoss / numPoints;
    }

    /**
     * テストセットでの評価
     */
    private static double evaluateTestSet(Backprop nn, double[] testX, double[] testY) {
        double totalLoss = 0.0;
        for (int i = 0; i < testX.length; i++) {
            double pred = nn.predict(testX[i]);
            double diff = pred - testY[i];
            totalLoss += 0.5 * diff * diff;
        }
        return totalLoss / testX.length;
    }

    /**
     * 配列をシャッフル（Fisher-Yates）
     */
    private static void shuffleArray(int[] array) {
        java.util.Random rand = new java.util.Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}