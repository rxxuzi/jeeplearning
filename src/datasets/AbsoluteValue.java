package datasets;

/**
 * 絶対値関数（微分不可能点を含む）
 * y = |ax + b| + c
 */
public class AbsoluteValue extends Function {

    private double a = 1.0;
    private double b = 0.0;
    private double c = 0.0;

    @Override
    public String getName() {
        return "Absolute Value";
    }

    @Override
    public String getDescription() {
        return String.format("y = |%.1fx + %.1f| + %.1f", a, b, c);
    }

    @Override
    public double[] getXRange() {
        return new double[]{-3.0, 3.0};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-0.5, 3.5};
    }

    @Override
    public double compute(double x) {
        return Math.abs(a * x + b) + c;
    }
}