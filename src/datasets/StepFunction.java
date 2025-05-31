package datasets;

/**
 * 階段関数（不連続点を含む）
 */
public class StepFunction extends Fn {

    public static final boolean IS_SPC = false;
    private double[] steps = {-2.0, -1.0, 0.0, 1.0, 2.0};
    private double[] values = {-1.0, -0.5, 0.0, 0.5, 1.0, 1.5};

    @Override
    public String getName() {
        return "Step Fn";
    }

    @Override
    public String getDescription() {
        return "Piecewise constant function";
    }

    @Override
    public double[] getXRange() {
        return new double[]{-3.0, 3.0};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-1.5, 2.0};
    }

    @Override
    public double compute(double x) {
        // 階段関数の実装
        for (int i = 0; i < steps.length; i++) {
            if (x < steps[i]) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }
}