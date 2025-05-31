package datasets;

/**
 * アルキメデスの螺旋（パラメトリック表現）
 * パラメータtを入力として、y座標を出力する
 * 表示時のみ(x,y)座標に変換
 */
public class Spiral extends Function {

    private double a = 0.0;  // 初期半径
    private double b = 0.5;  // 成長率

    // 実際のデータのX,Y座標を保持（表示用）
    private double[] displayX;
    private double[] displayY;
    private double[] testDisplayX;
    private double[] testDisplayY;

    @Override
    public String getName() {
        return "Spiral";
    }

    @Override
    public String getDescription() {
        return "Archimedean spiral: r = " + a + " + " + b + "θ";
    }

    @Override
    public double[] getXRange() {
        // パラメータtの範囲
        return new double[]{0, 4 * Math.PI};
    }

    @Override
    public double[] getYRange() {
        // 表示用のY範囲
        double maxT = 4 * Math.PI;
        double maxR = a + b * maxT;
        return new double[]{-maxR, maxR};
    }

    /**
     * 表示用のX範囲を取得
     */
    public double[] getDisplayXRange() {
        double maxT = 4 * Math.PI;
        double maxR = a + b * maxT;
        return new double[]{-maxR, maxR};
    }

    @Override
    public double compute(double t) {
        // パラメータtからy座標を計算
        double r = a + b * t;
        return r * Math.sin(t);
    }

    /**
     * X座標を計算（表示用）
     * @param t パラメータ
     * @return x座標
     */
    public double computeX(double t) {
        double r = a + b * t;
        return r * Math.cos(t);
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
        displayY = new double[nTrain]; // 表示用y座標（ノイズ付き）

        for (int i = 0; i < nTrain; i++) {
            double t = tMin + (tMax - tMin) * i / (nTrain - 1);
            trainX[i] = t;  // 入力はパラメータt
            trainY[i] = compute(t) + (rand.nextDouble() - 0.5) * noiseRate;

            // 表示用座標
            displayX[i] = computeX(t);
            displayY[i] = trainY[i];
        }

        // テストデータの生成（ノイズなし）
        testX = new double[nTest];  // パラメータt
        testY = new double[nTest];  // y座標
        testDisplayX = new double[nTest];
        testDisplayY = new double[nTest];

        for (int i = 0; i < nTest; i++) {
            double t = tMin + (tMax - tMin) * (i + 0.5) / nTest;
            testX[i] = t;
            testY[i] = compute(t);

            // 表示用座標
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