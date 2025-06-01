// ========== datasets/Circle.java ==========
package datasets;

/**
 * 円（パラメトリック表現）
 * x(t) = r*cos(t), y(t) = r*sin(t)
 * 入力tをパラメータとして使用
 */
public class Circle extends Fn {

    public static final boolean IS_SPC = true;

    private double radius = 1.0;

    // 実際のデータのX,Y座標を保持（表示用）
    private double[] displayX;
    private double[] displayY;
    private double[] testDisplayX;
    private double[] testDisplayY;

    @Override
    public boolean isParametric() {
        return IS_SPC;
    }

    @Override
    public String getName() {
        return "Circle";
    }

    @Override
    public String getDescription() {
        return "Unit circle: x² + y² = " + (radius * radius);
    }

    @Override
    public double[] getXRange() {
        // パラメータtの範囲
        return new double[]{0, 2 * Math.PI};
    }

    @Override
    public double[] getYRange() {
        // 表示用のY範囲
        return new double[]{-radius * 1.5, radius * 1.5};
    }

    /**
     * 表示用のX範囲を取得
     */
    public double[] getDisplayXRange() {
        return new double[]{-radius * 1.5, radius * 1.5};
    }

    @Override
    public double compute(double t) {
        // パラメータtからy座標を計算
        return radius * Math.sin(t);
    }

    /**
     * X座標を計算（表示用）
     * @param t パラメータ
     * @return x座標
     */
    public double computeX(double t) {
        return radius * Math.cos(t);
    }

    @Override
    public void generateDataset(int nTrain, int nTest, double noiseRate) {
        this.noiseRate = noiseRate;

        double[] tRange = getXRange();
        double tMin = tRange[0];
        double tMax = tRange[1];

        // 訓練データの生成
        trainX = new double[nTrain];  // パラメータt
        trainY = new double[nTrain];  // y座標
        displayX = new double[nTrain]; // 表示用x座標
        displayY = new double[nTrain]; // 表示用y座標

        for (int i = 0; i < nTrain; i++) {
            double t = tMin + (tMax - tMin) * i / (nTrain - 1);
            trainX[i] = t;  // 入力はパラメータt
            trainY[i] = compute(t) + (rand.nextDouble() - 0.5) * noiseRate;

            // 表示用座標
            displayX[i] = computeX(t);
            displayY[i] = trainY[i];
        }

        // テストデータの生成
        testX = new double[nTest];
        testY = new double[nTest];
        testDisplayX = new double[nTest];
        testDisplayY = new double[nTest];

        for (int i = 0; i < nTest; i++) {
            double t = tMin + (tMax - tMin) * (i + 0.5) / nTest;
            testX[i] = t;
            testY[i] = compute(t);

            testDisplayX[i] = computeX(t);
            testDisplayY[i] = testY[i];
        }
    }

    // 表示用座標のゲッター
    public double[] getDisplayTrainX() { return displayX; }
    public double[] getDisplayTrainY() { return displayY; }
    public double[] getDisplayTestX() { return testDisplayX; }
    public double[] getDisplayTestY() { return testDisplayY; }
}