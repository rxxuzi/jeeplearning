package datasets;

/**
 * チャープ信号（周波数が増加する正弦波）
 * y = sin(2π(f0 + kt)t)
 */
public class Chirp extends Function {

    private double f0 = 1.0;  // 初期周波数
    private double k = 0.5;   // 周波数変化率

    @Override
    public String getName() {
        return "Chirp Signal";
    }

    @Override
    public String getDescription() {
        return "Frequency-swept sine wave";
    }

    @Override
    public double[] getXRange() {
        return new double[]{0, 5.0};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-1.5, 1.5};
    }

    @Override
    public double compute(double t) {
        double phase = 2 * Math.PI * (f0 * t + 0.5 * k * t * t);
        return Math.sin(phase);
    }
}