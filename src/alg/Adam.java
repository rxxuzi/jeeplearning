package alg;

import math.Matrix;

/**
 * Adam最適化アルゴリズム
 * 適応的学習率で収束を改善
 */
public class Adam {

    private double beta1 = 0.9;
    private double beta2 = 0.999;
    private double epsilon = 1e-8;
    private double learningRate;
    private int t = 0;

    // 1次モーメント
    private double[][] mW1, mW2, mW3;
    private double[] mb1, mb2, mb3;

    // 2次モーメント
    private double[][] vW1, vW2, vW3;
    private double[] vb1, vb2, vb3;

    public Adam(double learningRate, int hidden1Size, int hidden2Size) {
        this.learningRate = learningRate;

        // モーメントの初期化
        mW1 = new double[hidden1Size][1];
        vW1 = new double[hidden1Size][1];
        mb1 = new double[hidden1Size];
        vb1 = new double[hidden1Size];

        mW2 = new double[hidden2Size][hidden1Size];
        vW2 = new double[hidden2Size][hidden1Size];
        mb2 = new double[hidden2Size];
        vb2 = new double[hidden2Size];

        mW3 = new double[1][hidden2Size];
        vW3 = new double[1][hidden2Size];
        mb3 = new double[1];
        vb3 = new double[1];
    }

    /**
     * Adamによる重み更新
     */
    public void update(calc.FWD fwd, calc.BWD bwd) {
        t++;

        // バイアス補正項
        double biasCorrection1 = 1.0 - Math.pow(beta1, t);
        double biasCorrection2 = 1.0 - Math.pow(beta2, t);

        // W1の更新
        updateMatrix(fwd.getW1(), bwd.getGradW1(), mW1, vW1, biasCorrection1, biasCorrection2);
        updateVector(fwd.getB1(), bwd.getGradB1(), mb1, vb1, biasCorrection1, biasCorrection2);

        // W2の更新
        updateMatrix(fwd.getW2(), bwd.getGradW2(), mW2, vW2, biasCorrection1, biasCorrection2);
        updateVector(fwd.getB2(), bwd.getGradB2(), mb2, vb2, biasCorrection1, biasCorrection2);

        // W3の更新
        updateMatrix(fwd.getW3(), bwd.getGradW3(), mW3, vW3, biasCorrection1, biasCorrection2);
        updateVector(fwd.getB3(), bwd.getGradB3(), mb3, vb3, biasCorrection1, biasCorrection2);
    }

    private void updateMatrix(double[][] param, double[][] grad, double[][] m, double[][] v,
                              double bc1, double bc2) {
        for (int i = 0; i < param.length; i++) {
            for (int j = 0; j < param[0].length; j++) {
                // モーメントの更新
                m[i][j] = beta1 * m[i][j] + (1 - beta1) * grad[i][j];
                v[i][j] = beta2 * v[i][j] + (1 - beta2) * grad[i][j] * grad[i][j];

                // バイアス補正
                double mHat = m[i][j] / bc1;
                double vHat = v[i][j] / bc2;

                // パラメータ更新
                param[i][j] -= learningRate * mHat / (Math.sqrt(vHat) + epsilon);
            }
        }
    }

    private void updateVector(double[] param, double[] grad, double[] m, double[] v,
                              double bc1, double bc2) {
        for (int i = 0; i < param.length; i++) {
            // モーメントの更新
            m[i] = beta1 * m[i] + (1 - beta1) * grad[i];
            v[i] = beta2 * v[i] + (1 - beta2) * grad[i] * grad[i];

            // バイアス補正
            double mHat = m[i] / bc1;
            double vHat = v[i] / bc2;

            // パラメータ更新
            param[i] -= learningRate * mHat / (Math.sqrt(vHat) + epsilon);
        }
    }
}
