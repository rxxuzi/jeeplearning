package datasets;

/**
 * 2次関数 y = ax² + bx + c
 */
public class Quadratic extends Fn {

    public static final boolean IS_SPC = false;
    private double a = 1.0;   // x²の係数
    private double b = -2.0;  // xの係数
    private double c = 0.0;   // 定数項

    @Override
    public String getName() {
        return "Quadratic";
    }

    @Override
    public String getDescription() {
        return String.format("y = %.1fx² + %.1fx + %.1f", a, b, c);
    }

    @Override
    public double[] getXRange() {
        return new double[]{-3.0, 3.0};
    }

    @Override
    public double[] getYRange() {
        // 頂点と端点での値を考慮して範囲を決定
        double vertex_x = -b / (2 * a);
        double vertex_y = compute(vertex_x);
        double y1 = compute(-3.0);
        double y2 = compute(3.0);

        double minY = Math.min(Math.min(vertex_y, y1), y2);
        double maxY = Math.max(Math.max(vertex_y, y1), y2);

        // マージンを追加
        double margin = (maxY - minY) * 0.2;
        return new double[]{minY - margin, maxY + margin};
    }

    @Override
    public double compute(double x) {
        return a * x * x + b * x + c;
    }
}