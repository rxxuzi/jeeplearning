package ui;

import datasets.Fn;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * グラフ描画コンポーネント
 */
public class Viewer extends JPanel {

    // 定数定義
    private static final int PREFERRED_WIDTH = 1200;
    private static final int PREFERRED_HEIGHT = 700;
    private static final double GRAPH_SPLIT_RATIO = 0.7;
    private static final int MARGIN = 10;
    private static final int GRAPH_MARGIN = 20;
    private static final int AXIS_MARGIN = 60;
    private static final int TITLE_HEIGHT = 40;

    // 描画設定
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color TRUE_FUNCTION_COLOR = new Color(150, 150, 255, 100);
    private static final Color PREDICTION_COLOR = new Color(255, 50, 50);
    private static final Color TRAIN_POINT_COLOR = new Color(0, 100, 255, 150);
    private static final Color LOSS_CURVE_COLOR = new Color(255, 100, 0);
    private static final Color AXIS_COLOR = Color.GRAY;

    private static final float DEFAULT_STROKE = 1.0f;
    private static final float THICK_STROKE = 2.0f;
    private static final float PREDICTION_STROKE = 2.5f;

    private static final int POINT_RADIUS = 3;
    private static final int PREDICTION_POINTS = 500;
    private static final int TRUE_FUNCTION_POINTS = 500;

    // フォント設定
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Font AXIS_FONT = new Font("Arial", Font.PLAIN, 10);

    // 凡例設定
    private static final int LEGEND_WIDTH = 145;
    private static final int LEGEND_HEIGHT = 80;
    private static final int LEGEND_PADDING = 5;
    private static final int LEGEND_LINE_LENGTH = 30;
    private static final int LEGEND_LINE_HEIGHT = 20;

    // データポイント（スレッドセーフ）
    private final CopyOnWriteArrayList<Point2D.Double> trainPoints = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Point2D.Double> testPoints = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Point2D.Double> predictionPoints = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Point2D.Double> lossHistory = new CopyOnWriteArrayList<>();

    // 描画範囲
    private double minX = -Math.PI * 1.5;
    private double maxX = Math.PI * 1.5;
    private double minY = -1.5;
    private double maxY = 1.5;

    // 現在の状態
    private volatile int currentEpoch = 0;
    private Fn currentFunction;

    public Viewer() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
    }

    /**
     * 現在の関数を設定
     */
    public void setCurrentFunction(Fn fn) {
        this.currentFunction = fn;
    }

    /**
     * 描画範囲を設定
     */
    public void setRanges(double[] xRange, double[] yRange) {
        if (currentFunction instanceof datasets.Spiral ||
                currentFunction instanceof datasets.Circle ||
                currentFunction instanceof datasets.Lemniscate ||
                currentFunction instanceof datasets.Limacon) {
            // パラメトリック関数の場合は表示用の範囲を使用
            double[] displayXRange = null;

            if (currentFunction instanceof datasets.Spiral) {
                displayXRange = ((datasets.Spiral) currentFunction).getDisplayXRange();
            } else if (currentFunction instanceof datasets.Circle) {
                displayXRange = ((datasets.Circle) currentFunction).getDisplayXRange();
            } else if (currentFunction instanceof datasets.Lemniscate) {
                displayXRange = ((datasets.Lemniscate) currentFunction).getDisplayXRange();
            } else if (currentFunction instanceof datasets.Limacon) {
                displayXRange = ((datasets.Limacon) currentFunction).getDisplayXRange();
            }

            double xMargin = (displayXRange[1] - displayXRange[0]) * 0.1;
            double yMargin = (yRange[1] - yRange[0]) * 0.1;
            this.minX = displayXRange[0] - xMargin;
            this.maxX = displayXRange[1] + xMargin;
            this.minY = yRange[0] - yMargin;
            this.maxY = yRange[1] + yMargin;
        } else {
            // 通常の処理
            double xMargin = (xRange[1] - xRange[0]) * 0.1;
            double yMargin = (yRange[1] - yRange[0]) * 0.1;
            this.minX = xRange[0] - xMargin;
            this.maxX = xRange[1] + xMargin;
            this.minY = yRange[0] - yMargin;
            this.maxY = yRange[1] + yMargin;
        }
        repaint();
    }

    private void genMinMax(double[] yRange, double[] displayXRange) {
        double xMargin = (displayXRange[1] - displayXRange[0]) * 0.1;
        double yMargin = (yRange[1] - yRange[0]) * 0.1;
        this.minX = displayXRange[0] - xMargin;
        this.maxX = displayXRange[1] + xMargin;
        this.minY = yRange[0] - yMargin;
        this.maxY = yRange[1] + yMargin;
    }

    /**
     * 訓練データを設定
     */
    public void setTrainData(double[] x, double[] y) {
        List<Point2D.Double> newPoints = new ArrayList<>();

        if (currentFunction instanceof datasets.Spiral ||
                currentFunction instanceof datasets.Circle ||
                currentFunction instanceof datasets.Lemniscate ||
                currentFunction instanceof datasets.Limacon) {
            // パラメトリック関数の場合は表示用座標を使用
            double[] displayX = null;
            double[] displayY = null;

            if (currentFunction instanceof datasets.Spiral) {
                datasets.Spiral func = (datasets.Spiral) currentFunction;
                displayX = func.getDisplayTrainX();
                displayY = func.getDisplayTrainY();
            } else if (currentFunction instanceof datasets.Circle) {
                datasets.Circle func = (datasets.Circle) currentFunction;
                displayX = func.getDisplayTrainX();
                displayY = func.getDisplayTrainY();
            } else if (currentFunction instanceof datasets.Lemniscate) {
                datasets.Lemniscate func = (datasets.Lemniscate) currentFunction;
                displayX = func.getDisplayTrainX();
                displayY = func.getDisplayTrainY();
            } else {
                datasets.Limacon func = (datasets.Limacon) currentFunction;
                displayX = func.getDisplayTrainX();
                displayY = func.getDisplayTrainY();
            }

            for (int i = 0; i < displayX.length; i++) {
                newPoints.add(new Point2D.Double(displayX[i], displayY[i]));
            }
        } else {
            // 通常の処理
            for (int i = 0; i < x.length; i++) {
                newPoints.add(new Point2D.Double(x[i], y[i]));
            }
        }

        trainPoints.clear();
        trainPoints.addAll(newPoints);
        repaint();
    }

    /**
     * テストデータを設定
     */
    public void setTestData(double[] x, double[] y) {
        List<Point2D.Double> newPoints = new ArrayList<>();

        if (currentFunction instanceof datasets.Spiral spiral) {
            // Spiralの場合は表示用座標を使用
            double[] displayX = spiral.getDisplayTestX();
            double[] displayY = spiral.getDisplayTestY();

            for (int i = 0; i < displayX.length; i++) {
                newPoints.add(new Point2D.Double(displayX[i], displayY[i]));
            }
        } else {
            // 通常の処理
            for (int i = 0; i < x.length; i++) {
                newPoints.add(new Point2D.Double(x[i], y[i]));
            }
        }

        testPoints.clear();
        testPoints.addAll(newPoints);
        repaint();
    }

    /**
     * 予測結果を更新
     */
    public void updatePredictions(alg.Backprop nn, int epoch) {
        currentEpoch = epoch;
        List<Point2D.Double> newPoints = new ArrayList<>();

        if (currentFunction instanceof datasets.Spiral ||
                currentFunction instanceof datasets.Circle ||
                currentFunction instanceof datasets.Lemniscate ||
                currentFunction instanceof datasets.Limacon) {
            // パラメトリック関数の場合
            double[] tRange = currentFunction.getXRange();

            for (int i = 0; i < PREDICTION_POINTS; i++) {
                double t = tRange[0] + (tRange[1] - tRange[0]) * i / (PREDICTION_POINTS - 1);
                double predictedY = nn.predict(t);  // tを入力として予測
                double x = 0;

                if (currentFunction instanceof datasets.Spiral) {
                    x = ((datasets.Spiral) currentFunction).computeX(t);
                } else if (currentFunction instanceof datasets.Circle) {
                    x = ((datasets.Circle) currentFunction).computeX(t);
                } else if (currentFunction instanceof datasets.Lemniscate) {
                    x = ((datasets.Lemniscate) currentFunction).computeX(t);
                } else if (currentFunction instanceof datasets.Limacon) {
                    x = ((datasets.Limacon) currentFunction).computeX(t);
                }

                newPoints.add(new Point2D.Double(x, predictedY));
            }
        } else {
            // 通常の関数の場合
            for (int i = 0; i < PREDICTION_POINTS; i++) {
                double x = minX + (maxX - minX) * i / (PREDICTION_POINTS - 1);
                double y = nn.predict(x);
                newPoints.add(new Point2D.Double(x, y));
            }
        }

        predictionPoints.clear();
        predictionPoints.addAll(newPoints);
        repaint();
    }


    /**
     * 損失履歴を追加
     */
    public void addLossHistory(int epoch, double loss) {
        lossHistory.add(new Point2D.Double(epoch, loss));
        repaint();
    }

    /**
     * 履歴をクリア
     */
    public void clearHistory() {
        lossHistory.clear();
        currentEpoch = 0;
        repaint();
    }

    /**
     * 予測をクリア
     */
    public void clearPredictions() {
        predictionPoints.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // メイングラフと損失グラフの領域を分割
        int mainGraphWidth = (int)(width * GRAPH_SPLIT_RATIO);
        int lossGraphWidth = width - mainGraphWidth - GRAPH_MARGIN;

        // メイングラフを描画
        drawMainGraph(g2, MARGIN, MARGIN, mainGraphWidth - GRAPH_MARGIN, height - GRAPH_MARGIN);

        // 損失グラフを描画
        drawLossGraph(g2, mainGraphWidth, MARGIN, lossGraphWidth - MARGIN, height - GRAPH_MARGIN);
    }

    /**
     * メイングラフを描画
     */
    private void drawMainGraph(Graphics2D g2, int x, int y, int width, int height) {
        // 背景と枠
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        // タイトル
        g2.setFont(TITLE_FONT);
        String title = currentFunction != null ?
                currentFunction.getName() + " Approximation - Epoch: " + currentEpoch :
                "Function Approximation - Epoch: " + currentEpoch;
        g2.drawString(title, x + MARGIN, y + GRAPH_MARGIN);

        // グラフ領域
        int graphX = x + AXIS_MARGIN;
        int graphY = y + TITLE_HEIGHT;
        int graphWidth = width - AXIS_MARGIN - GRAPH_MARGIN;
        int graphHeight = height - AXIS_MARGIN - GRAPH_MARGIN;

        // 軸を描画
        drawAxes(g2, graphX, graphY, graphWidth, graphHeight);

        // 真の関数を描画
        if (currentFunction != null) {
            g2.setColor(TRUE_FUNCTION_COLOR);
            g2.setStroke(new BasicStroke(THICK_STROKE));
            GeneralPath truePath = new GeneralPath();

            if (currentFunction.isParametric()) {
                drawParametricFunction(g2, truePath, graphX, graphY, graphWidth, graphHeight);
            } else {
                // デカルト座標関数の描画
                drawCartesianFunction(g2, truePath, graphX, graphY, graphWidth, graphHeight);
            }

            g2.draw(truePath);
        }

        // 予測線を描画
        if (!predictionPoints.isEmpty()) {
            g2.setColor(PREDICTION_COLOR);
            g2.setStroke(new BasicStroke(PREDICTION_STROKE));
            GeneralPath predPath = new GeneralPath();
            boolean first = true;

            for (Point2D.Double p : predictionPoints) {
                int sx = graphX + (int)((p.x - minX) / (maxX - minX) * graphWidth);
                int sy = graphY + graphHeight - (int)((p.y - minY) / (maxY - minY) * graphHeight);

                if (first) {
                    predPath.moveTo(sx, sy);
                    first = false;
                } else {
                    predPath.lineTo(sx, sy);
                }
            }
            g2.draw(predPath);
        }

        // 訓練データ点を描画
        g2.setStroke(new BasicStroke(DEFAULT_STROKE));
        for (Point2D.Double p : trainPoints) {
            int sx = graphX + (int)((p.x - minX) / (maxX - minX) * graphWidth);
            int sy = graphY + graphHeight - (int)((p.y - minY) / (maxY - minY) * graphHeight);

            g2.setColor(TRAIN_POINT_COLOR);
            g2.fillOval(sx - POINT_RADIUS, sy - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
            g2.setColor(Color.BLUE);
            g2.drawOval(sx - POINT_RADIUS, sy - POINT_RADIUS, POINT_RADIUS * 2, POINT_RADIUS * 2);
        }

        // 凡例
        drawLegend(g2, graphX + graphWidth - LEGEND_WIDTH - MARGIN, graphY + MARGIN);
    }

    /**
     * パラメトリック関数を描画
     */
    private void drawParametricFunction(Graphics2D g2, GeneralPath path,
                                        int graphX, int graphY, int graphWidth, int graphHeight) {
        double[] tRange = currentFunction.getXRange();
        boolean first = true;

        for (int i = 0; i < TRUE_FUNCTION_POINTS; i++) {
            double t = tRange[0] + (tRange[1] - tRange[0]) * i / (TRUE_FUNCTION_POINTS - 1);
            double px = currentFunction.computeX(t);
            first = computeXY(path, graphX, graphY, graphWidth, graphHeight, first, t, px);
        }
    }

    private boolean computeXY(GeneralPath path, int graphX, int graphY, int graphWidth, int graphHeight, boolean first, double t, double px) {
        double py = currentFunction.compute(t);

        int sx = graphX + (int)((px - minX) / (maxX - minX) * graphWidth);
        int sy = graphY + graphHeight - (int)((py - minY) / (maxY - minY) * graphHeight);

        if (first) {
            path.moveTo(sx, sy);
            first = false;
        } else {
            path.lineTo(sx, sy);
        }
        return first;
    }

    /**
     * デカルト座標関数を描画
     */
    private void drawCartesianFunction(Graphics2D g2, GeneralPath path,
                                       int graphX, int graphY, int graphWidth, int graphHeight) {
        boolean first = true;

        for (int i = 0; i < TRUE_FUNCTION_POINTS; i++) {
            double px = minX + (maxX - minX) * i / (TRUE_FUNCTION_POINTS - 1);
            first = computeXY(path, graphX, graphY, graphWidth, graphHeight, first, px, px);
        }
    }

    /**
     * 凡例を描画
     */
    private void drawLegend(Graphics2D g2, int x, int y) {
        g2.setColor(Color.WHITE);
        g2.fillRect(x - LEGEND_PADDING, y - LEGEND_PADDING, LEGEND_WIDTH, LEGEND_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(x - LEGEND_PADDING, y - LEGEND_PADDING, LEGEND_WIDTH, LEGEND_HEIGHT);

        g2.setFont(LABEL_FONT);

        // True function
        g2.setColor(TRUE_FUNCTION_COLOR);
        g2.setStroke(new BasicStroke(THICK_STROKE));
        g2.drawLine(x, y + MARGIN, x + LEGEND_LINE_LENGTH, y + MARGIN);
        g2.setColor(Color.BLACK);
        g2.drawString("True function", x + LEGEND_LINE_LENGTH + MARGIN, y + MARGIN + LEGEND_PADDING);

        // Prediction
        g2.setColor(PREDICTION_COLOR);
        g2.setStroke(new BasicStroke(PREDICTION_STROKE));
        g2.drawLine(x, y + LEGEND_LINE_HEIGHT + MARGIN, x + LEGEND_LINE_LENGTH, y + LEGEND_LINE_HEIGHT + MARGIN);
        g2.setColor(Color.BLACK);
        g2.drawString("Prediction", x + LEGEND_LINE_LENGTH + MARGIN, y + LEGEND_LINE_HEIGHT + MARGIN + LEGEND_PADDING);

        // Training data
        g2.setStroke(new BasicStroke(DEFAULT_STROKE));
        g2.setColor(Color.BLUE);
        g2.fillOval(x + MARGIN, y + LEGEND_LINE_HEIGHT * 2 + MARGIN - POINT_RADIUS,
                POINT_RADIUS * 2, POINT_RADIUS * 2);
        g2.setColor(Color.BLACK);
        g2.drawString("Training data", x + LEGEND_LINE_LENGTH + MARGIN, y + LEGEND_LINE_HEIGHT * 2 + MARGIN + LEGEND_PADDING);
    }

    /**
     * 損失グラフを描画
     */
    private void drawLossGraph(Graphics2D g2, int x, int y, int width, int height) {
        // 背景と枠
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, width, height);

        // タイトル
        g2.setFont(SUBTITLE_FONT);
        g2.drawString("Training Loss History", x + MARGIN, y + GRAPH_MARGIN);

        if (lossHistory.isEmpty()) return;

        // グラフ領域
        int graphX = x + AXIS_MARGIN - MARGIN;
        int graphY = y + TITLE_HEIGHT;
        int graphWidth = width - AXIS_MARGIN;
        int graphHeight = height - AXIS_MARGIN - GRAPH_MARGIN;

        // 動的な損失範囲の計算
        double maxLoss = 0.0;
        double minLoss = Double.MAX_VALUE;

        // 最初の数個の値から最大値を決定（初期の不安定な値を考慮）
        int initialSamples = Math.min(10, lossHistory.size());
        for (int i = 0; i < initialSamples; i++) {
            maxLoss = Math.max(maxLoss, lossHistory.get(i).y);
        }

        // 全体の最小値を見つける
        for (Point2D.Double p : lossHistory) {
            minLoss = Math.min(minLoss, p.y);
        }

        // 最小値を0に設定し、最大値に10%の余裕を追加
        minLoss = 0.0;
        maxLoss = maxLoss * 1.1;

        // 軸を描画
        g2.setColor(AXIS_COLOR);
        g2.drawLine(graphX, graphY + graphHeight, graphX + graphWidth, graphY + graphHeight);
        g2.drawLine(graphX, graphY, graphX, graphY + graphHeight);

        // Y軸ラベル（動的範囲）
        g2.setFont(AXIS_FONT);
        int numDivisions = 5;
        for (int i = 0; i <= numDivisions; i++) {
            double value = minLoss + (maxLoss - minLoss) * i / numDivisions;
            int sy = graphY + graphHeight - (int)(i * graphHeight / (double)numDivisions);

            // 値に応じて適切なフォーマット
            String label;
            if (maxLoss < 0.01) {
                label = String.format("%.5f", value);
            } else if (maxLoss < 0.1) {
                label = String.format("%.4f", value);
            } else if (maxLoss < 1.0) {
                label = String.format("%.3f", value);
            } else {
                label = String.format("%.2f", value);
            }

            g2.drawString(label, x + LEGEND_PADDING, sy + POINT_RADIUS);
            g2.drawLine(graphX - POINT_RADIUS, sy, graphX, sy);
        }

        // 損失曲線を描画
        if (lossHistory.size() > 1) {
            g2.setColor(LOSS_CURVE_COLOR);
            g2.setStroke(new BasicStroke(THICK_STROKE));
            GeneralPath lossPath = new GeneralPath();

            double maxEpoch = lossHistory.get(lossHistory.size() - 1).x;
            boolean first = true;

            for (Point2D.Double p : lossHistory) {
                int sx = graphX + (int)(p.x / maxEpoch * graphWidth);
                int sy = graphY + graphHeight - (int)((p.y - minLoss) / (maxLoss - minLoss) * graphHeight);

                if (first) {
                    lossPath.moveTo(sx, sy);
                    first = false;
                } else {
                    lossPath.lineTo(sx, sy);
                }
            }
            g2.draw(lossPath);

            // 最初と最後の損失値を表示
            Point2D.Double firstPoint = lossHistory.get(0);
            Point2D.Double lastPoint = lossHistory.get(lossHistory.size() - 1);

            g2.setColor(Color.BLACK);
            g2.setFont(LABEL_FONT);

            // 最初の値（グラフ上部）
            g2.drawString(String.format("Initial: %.4f", firstPoint.y),
                    graphX + 5, graphY + 15);

            // 最終値（グラフ下部）
            g2.drawString(String.format("Final: %.4f", lastPoint.y),
                    graphX + graphWidth - 80, graphY + graphHeight - 10);

            // 減少率を計算して表示
            if (firstPoint.y > 0) {
                double reductionRate = (firstPoint.y - lastPoint.y) / firstPoint.y * 100;
                g2.drawString(String.format("Reduction: %.1f%%", reductionRate),
                        graphX + graphWidth / 2 - 40, graphY + 15);
            }
        }

        // X軸ラベル
        g2.setColor(Color.BLACK);
        g2.drawString("Epoch", graphX + graphWidth / 2 - GRAPH_MARGIN, y + height - MARGIN);

        // グリッド線を追加（オプション）
        g2.setColor(new Color(220, 220, 220));
        g2.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 10.0f,
                new float[]{5.0f}, 0.0f));

        // 水平グリッド線
        for (int i = 1; i < numDivisions; i++) {
            int sy = graphY + graphHeight - (int)(i * graphHeight / (double)numDivisions);
            g2.drawLine(graphX + 1, sy, graphX + graphWidth - 1, sy);
        }

        // 垂直グリッド線（エポック）
        int epochGrids = 5;
        if (lossHistory.size() > 1) {
            double maxEpoch = lossHistory.get(lossHistory.size() - 1).x;
            for (int i = 1; i < epochGrids; i++) {
                int sx = graphX + (int)(i * graphWidth / (double)epochGrids);
                g2.drawLine(sx, graphY + 1, sx, graphY + graphHeight - 1);

                // エポック番号
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                int epochNum = (int)(maxEpoch * i / epochGrids);
                g2.drawString(String.valueOf(epochNum), sx - 10, graphY + graphHeight + 15);
            }
        }
    }

    /**
     * 座標軸を描画
     */
    private void drawAxes(Graphics2D g2, int x, int y, int width, int height) {
        g2.setColor(AXIS_COLOR);
        g2.setStroke(new BasicStroke(DEFAULT_STROKE));

        // 原点の位置を計算
        int zeroX = x + (int)((0 - minX) / (maxX - minX) * width);
        int zeroY = y + height - (int)((0 - minY) / (maxY - minY) * height);

        // 原点が描画範囲内にあるか確認
        boolean drawXAxis = (zeroY >= y && zeroY <= y + height);
        boolean drawYAxis = (zeroX >= x && zeroX <= x + width);

        // X軸（原点が範囲内の場合のみ描画）
        if (drawXAxis) {
            g2.drawLine(x, zeroY, x + width, zeroY);
        }

        // Y軸（原点が範囲内の場合のみ描画）
        if (drawYAxis) {
            g2.drawLine(zeroX, y, zeroX, y + height);
        }

        // 軸の目盛りとラベル
        g2.setFont(AXIS_FONT);

        // X軸の目盛り
        int numXTicks = 7;
        for (int i = 0; i <= numXTicks; i++) {
            double xVal = minX + (maxX - minX) * i / numXTicks;
            int sx = x + (int)(i * width / (double)numXTicks);

            // 目盛り線
            if (drawXAxis) {
                g2.drawLine(sx, zeroY - POINT_RADIUS, sx, zeroY + POINT_RADIUS);
            } else {
                // X軸が見えない場合は下端に目盛りを描画
                g2.drawLine(sx, y + height - POINT_RADIUS * 2,sx,  y + height);
            }

            String label;
            if (Math.abs(xVal) < 0.01) {
                label = "0";
            } else if (currentFunction instanceof datasets.Sin && Math.abs(Math.abs(xVal) - Math.PI) < 0.1) {
                label = xVal > 0 ? "π" : "-π";
            } else {
                label = String.format("%.1f", xVal);
            }

            // ラベルの位置
            int labelY = drawXAxis ? zeroY + 15 : y + height - 5;
            g2.drawString(label, sx - 15, labelY);
        }

        // Y軸の目盛り
        int numYTicks = 5;
        for (int i = 0; i <= numYTicks; i++) {
            double yVal = minY + (maxY - minY) * i / numYTicks;
            int sy = y + height - (int)(i * height / (double)numYTicks);

            // 目盛り線
            if (drawYAxis) {
                g2.drawLine(zeroX - POINT_RADIUS, sy, zeroX + POINT_RADIUS, sy);
            } else {
                // Y軸が見えない場合は左端に目盛りを描画
                g2.drawLine(x, sy, x + POINT_RADIUS * 2, sy);
            }

            // ラベルの位置
            int labelX = drawYAxis ? zeroX - 35 : x + 5;
            g2.drawString(String.format("%.1f", yVal), labelX, sy + POINT_RADIUS);
        }

        // 原点を強調表示（両軸が表示されている場合）
        if (drawXAxis && drawYAxis) {
            g2.setColor(Color.RED);
            g2.fillOval(zeroX - 2, zeroY - 2, 4, 4);
        }
    }
}