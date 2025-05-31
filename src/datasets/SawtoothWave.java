package datasets;

/**
 * のこぎり波
 */
public class SawtoothWave extends Function {

    private double period = 2.0;
    private double amplitude = 1.0;

    @Override
    public String getName() {
        return "Sawtooth Wave";
    }

    @Override
    public String getDescription() {
        return "Periodic sawtooth wave";
    }

    @Override
    public double[] getXRange() {
        return new double[]{-4.0, 4.0};
    }

    @Override
    public double[] getYRange() {
        return new double[]{-1.5, 1.5};
    }

    @Override
    public double compute(double x) {
        // のこぎり波の計算
        double t = x / period;
        double fractional = t - Math.floor(t);
        return amplitude * (2 * fractional - 1);
    }
}