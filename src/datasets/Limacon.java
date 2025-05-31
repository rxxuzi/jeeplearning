// ========== datasets/Limacon.java ==========
package datasets;

/**
 * リマソン（カタツムリ曲線）
 * 極座標表現: r = a + b*cos(θ)
 * 内側にループがある場合（a < b）とない場合（a ≥ b）がある
 */
public class Limacon extends Fn {

    public static final boolean IS_SPC = true;
    private double a = 1.0;  // 定数項
    private double b = 1.5;  // cos項の係数（a < bで内側にループ）

    private double[] displayX;
    private double[] displayY;
    private double[] testDisplayX;
    private double[] testDisplayY;

    @Override
    public String getName() {
        return "Limacon";
    }

    @Override
    public String getDescription() {
        return String.format("Limaçon: r = %.1f + %.1f*cos(θ)", a, b);
    }

    @Override
    public double[] getXRange() {
        // パラメータtの範囲
        return new double[]{0, 2 * Math.PI};
    }

    @Override
    public double[] getYRange() {
        double maxR = a + b;
        return new double[]{-maxR, maxR};
    }

    public double[] getDisplayXRange() {
        double maxR = a + b;
        return new double[]{-maxR * 1.2, maxR * 1.2};
    }

    @Override
    public double compute(double t) {
        // 極座標からy座標を計算
        double r = a + b * Math.cos(t);
        return r * Math.sin(t);
    }

    public double computeX(double t) {
        // 極座標からx座標を計算
        double r = a + b * Math.cos(t);
        return r * Math.cos(t);
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
