package datasets;

/**
 * ガウス関数（正規分布曲線）
 * y = a * exp(-(x-μ)²/(2σ²))
 */
public class Gaussian extends Function {

    private double a = 1.0;    // 振幅
    private double mu = 0.0;   // 平均
    private double sigma = 1.0; // 標準偏差

    @Override
    public String getName() {
        return "Gaussian";
    }

    @Override
    public String getDescription() {
        return String.format("y = %.1f * exp(-(x-%.1f)²/(2*%.1f²))", a, mu, sigma);
    }

    @Override
    public double[] getXRange() {
        return new double[]{mu - 4*sigma, mu + 4*sigma};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-0.2, a + 0.2};
    }

    @Override
    public double compute(double x) {
        double z = (x - mu) / sigma;
        return a * Math.exp(-0.5 * z * z);
    }
}