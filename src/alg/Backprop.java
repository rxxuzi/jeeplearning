package alg;

import calc.Backward;
import calc.Forward;

public class Backprop {

    private final Forward fwd;
    private final Backward bwd;
    private final Adam adam;
    private final double l2Lambda; // L2正則化の強度

    /**
     * コンストラクタ（2層隠れ層）
     * @param inputSize 入力層のサイズ
     * @param hidden1Size 隠れ層1のサイズ
     * @param hidden2Size 隠れ層2のサイズ
     * @param outputSize 出力層のサイズ
     * @param learningRate 学習率
     * @param l2Lambda L2正則化の強度
     */
    public Backprop(int inputSize, int hidden1Size, int hidden2Size, int outputSize,
                    double learningRate, double l2Lambda) {
        this.fwd = new Forward(inputSize, hidden1Size, hidden2Size, outputSize);
        this.bwd = new Backward();
        this.adam = new Adam(learningRate, hidden1Size, hidden2Size);
        this.l2Lambda = l2Lambda;
    }

    /**
     * 1つのサンプルで学習
     * @param x 入力値
     * @param y 正解値
     * @return 損失値
     */
    public double train(double x, double y) {
        // 順伝播
        fwd.forward(x);

        // 逆伝播
        double loss = bwd.backward(fwd, y);

        // L2正則化項を勾配に追加
        addL2Regularization();

        // Adamで重みを更新
        adam.update(fwd, bwd);

        return loss;
    }

    /**
     * 予測（順伝播のみ）
     * @param x 入力値
     * @return 予測値
     */
    public double predict(double x) {
        return fwd.forward(x);
    }

    /**
     * L2正則化を勾配に追加
     */
    private void addL2Regularization() {
        if (l2Lambda > 0) {
            // W1の正則化
            double[][] gradW1 = bwd.getGradW1();
            double[][] w1 = fwd.getW1();
            for (int i = 0; i < gradW1.length; i++) {
                for (int j = 0; j < gradW1[0].length; j++) {
                    gradW1[i][j] += l2Lambda * w1[i][j];
                }
            }

            // W2の正則化
            double[][] gradW2 = bwd.getGradW2();
            double[][] w2 = fwd.getW2();
            for (int i = 0; i < gradW2.length; i++) {
                for (int j = 0; j < gradW2[0].length; j++) {
                    gradW2[i][j] += l2Lambda * w2[i][j];
                }
            }

            // W3の正則化
            double[][] gradW3 = bwd.getGradW3();
            double[][] w3 = fwd.getW3();
            for (int i = 0; i < gradW3.length; i++) {
                for (int j = 0; j < gradW3[0].length; j++) {
                    gradW3[i][j] += l2Lambda * w3[i][j];
                }
            }
        }
    }
}
