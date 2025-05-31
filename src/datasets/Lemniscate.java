package datasets;

/**
 * レムニスケート（∞の形の曲線）
 * 極座標表現: r² = a² * cos(2θ)
 * パラメトリック表現: x = a*cos(t)/(1+sin²(t)), y = a*sin(t)*cos(t)/(1+sin²(t))
 */
public class Lemniscate extends Fn {

    public static final boolean IS_SPC = true;
    private double a = 2.0;  // サイズパラメータ

    private double[] displayX;
    private double[] displayY;
    private double[] testDisplayX;
    private double[] testDisplayY;

    @Override
    public String getName() {
        return "Lemniscate";
    }

    @Override
    public String getDescription() {
        return "Lemniscate of Bernoulli (∞ curve)";
    }

    @Override
    public double[] getXRange() {
        // パラメータtの範囲
        return new double[]{0, 2 * Math.PI};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-a * 0.6, a * 0.6};
    }

    public double[] getDisplayXRange() {
        return new double[]{-a * 1.2, a * 1.2};
    }

    @Override
    public double compute(double t) {
        // パラメトリック表現のy座標
        double sint = Math.sin(t);
        double cost = Math.cos(t);
        double denom = 1 + sint * sint;
        return a * sint * cost / denom;
    }

    public double computeX(double t) {
        // パラメトリック表現のx座標
        double sint = Math.sin(t);
        double cost = Math.cos(t);
        double denom = 1 + sint * sint;
        return a * cost / denom;
    }

    @Override
    public void generateDataset(int nTrain, int nTest, double noiseRate) {
        this.noiseRate = noiseRate;

        double[] tRange = getXRange();
        double tMin = tRange[0];
        double tMax = tRange[1];

        // 訓練データの生成
        trainX = new double[nTrain];
        trainY = new double[nTrain];
        displayX = new double[nTrain];
        displayY = new double[nTrain];

        for (int i = 0; i < nTrain; i++) {
            double t = tMin + (tMax - tMin) * i / (nTrain - 1);
            trainX[i] = t;
            trainY[i] = compute(t) + (rand.nextDouble() - 0.5) * noiseRate;

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

    public double[] getDisplayTrainX() { return displayX; }
    public double[] getDisplayTrainY() { return displayY; }
    public double[] getDisplayTestX() { return testDisplayX; }
    public double[] getDisplayTestY() { return testDisplayY; }
}