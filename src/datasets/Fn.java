package datasets;

import java.util.Random;

/**
 * 回帰分析用の関数の基底クラス
 */
public abstract class Fn {

    protected double[] trainX;
    protected double[] trainY;
    protected double[] testX;
    protected double[] testY;


    protected Random rand = new Random();
    protected double noiseRate = 0.0;

    /**
     * パラメトリック（極座標）関数かどうかを判定
     * @return パラメトリック関数の場合true
     */
    public boolean isParametric() {
        return false;  // デフォルトはfalse（デカルト座標）
    }

    /**
     * パラメトリック関数のX座標を計算（オプション）
     * @param t パラメータ
     * @return x座標
     */
    public double computeX(double t) {
        throw new UnsupportedOperationException("This function is not parametric");
    }

    /**
     * 関数の名前を取得
     * @return 関数名
     */
    public abstract String getName();

    /**
     * 関数の説明を取得
     * @return 説明文
     */
    public abstract String getDescription();

    /**
     * X軸の範囲を取得
     * @return [min, max]
     */
    public abstract double[] getXRange();

    /**
     * Y軸の範囲を取得
     * @return [min, max]
     */
    public abstract double[] getYRange();

    /**
     * 実際の関数値を計算（ノイズなし）
     * @param x 入力値
     * @return 関数値
     */
    public abstract double compute(double x);

    /**
     * データセットを生成
     * @param nTrain 訓練データ数
     * @param nTest テストデータ数
     * @param noiseRate ノイズレート
     */
    public void generateDataset(int nTrain, int nTest, double noiseRate) {
        this.noiseRate = noiseRate;

        double[] xRange = getXRange();
        double xMin = xRange[0];
        double xMax = xRange[1];

        // 訓練データの生成
        trainX = new double[nTrain];
        trainY = new double[nTrain];
        for (int i = 0; i < nTrain; i++) {
            trainX[i] = xMin + (xMax - xMin) * i / (nTrain - 1);
            trainY[i] = compute(trainX[i]) + (rand.nextDouble() - 0.5) * noiseRate;
        }

        // テストデータの生成（ノイズなし）
        testX = new double[nTest];
        testY = new double[nTest];
        for (int i = 0; i < nTest; i++) {
            testX[i] = xMin + (xMax - xMin) * (i + 0.5) / nTest;
            testY[i] = compute(testX[i]);
        }
    }

    // ゲッターメソッド
    public double[] getTrainX() { return trainX; }
    public double[] getTrainY() { return trainY; }
    public double[] getTestX() { return testX; }
    public double[] getTestY() { return testY; }
}