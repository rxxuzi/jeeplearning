package datasets;

/**
 * 減衰振動
 * y = A * exp(-γx) * cos(ωx + φ)
 */
public class DampedOscillation extends Fn {

    public static final boolean IS_SPC = false;
    private double A = 1.0;      // 初期振幅
    private double gamma = 0.2;  // 減衰係数
    private double omega = 4.0;  // 角周波数
    private double phi = 0.0;    // 位相

    @Override
    public String getName() {
        return "Damped Oscillation";
    }

    @Override
    public String getDescription() {
        return String.format("y = %.1f * exp(-%.1fx) * cos(%.1fx)", A, gamma, omega);
    }

    @Override
    public double[] getXRange() {
        return new double[]{0, 5 * Math.PI};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-1.2, 1.2};
    }

    @Override
    public double compute(double x) {
        return A * Math.exp(-gamma * x) * Math.cos(omega * x + phi);
    }
}