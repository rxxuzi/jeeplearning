package datasets;

/**
 * sin(x)関数
 */
public class Sin extends Function {

    @Override
    public String getName() {
        return "Sin(x)";
    }

    @Override
    public String getDescription() {
        return "y = sin(x)";
    }

    @Override
    public double[] getXRange() {
        return new double[]{-Math.PI, Math.PI};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-1.5, 1.5};
    }

    @Override
    public double compute(double x) {
        return Math.sin(x);
    }
}