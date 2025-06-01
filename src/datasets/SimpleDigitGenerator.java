package datasets;

import main.CNN;
import java.util.Random;

/**
 * 改良版 手書き数字画像生成器
 * より多様で現実的な手書き数字を生成
 */
public class SimpleDigitGenerator {

    private final Random rand;
    private final int imageSize = CNN.IMAGE_SIZE;
    private final int center = CNN.IMAGE_SIZE / 2;

    // 描画スタイルのバリエーション
    private enum DrawStyle {
        NORMAL,      // 通常
        THICK,       // 太い
        THIN,        // 細い
        ROUNDED,     // 丸みを帯びた
        ANGULAR      // 角張った
    }

    public SimpleDigitGenerator() {
        this.rand = new Random();
    }

    public SimpleDigitGenerator(long seed) {
        this.rand = new Random(seed);
    }

    /**
     * 数字の画像を生成
     * @param digit 数字（0-9）
     * @param noise ノイズレベル（0.0-1.0）
     * @return 画像配列（0.0-1.0の値）
     */
    public double[][] generateDigit(int digit, double noise) {
        double[][] image = new double[imageSize][imageSize];

        // 背景を白（0.0）で初期化
        for (int i = 0; i < imageSize; i++) {
            for (int j = 0; j < imageSize; j++) {
                image[i][j] = 0.0;
            }
        }

        // ランダムなスタイルを選択
        DrawStyle style = DrawStyle.values()[rand.nextInt(DrawStyle.values().length)];

        // 数字を描画
        drawDigit(image, digit, style);

        // ランダムな変形を追加
        image = applyRandomTransform(image);

        // ノイズを追加
        if (noise > 0) {
            addNoise(image, noise);
        }

        // スムージング
        image = smoothImage(image);

        return image;
    }

    /**
     * 数字を描画（スタイル付き）
     */
    private void drawDigit(double[][] image, int digit, DrawStyle style) {
        // 数字の中心位置をランダムに少しずらす
        int offsetX = rand.nextInt(5) - 2;
        int offsetY = rand.nextInt(5) - 2;

        // スタイルに応じた線の太さ
        double thickness = getThickness(style);

        switch (digit) {
            case 0:
                drawZero(image, offsetX, offsetY, style, thickness);
                break;
            case 1:
                drawOne(image, offsetX, offsetY, style, thickness);
                break;
            case 2:
                drawTwo(image, offsetX, offsetY, style, thickness);
                break;
            case 3:
                drawThree(image, offsetX, offsetY, style, thickness);
                break;
            case 4:
                drawFour(image, offsetX, offsetY, style, thickness);
                break;
            case 5:
                drawFive(image, offsetX, offsetY, style, thickness);
                break;
            case 6:
                drawSix(image, offsetX, offsetY, style, thickness);
                break;
            case 7:
                drawSeven(image, offsetX, offsetY, style, thickness);
                break;
            case 8:
                drawEight(image, offsetX, offsetY, style, thickness);
                break;
            case 9:
                drawNine(image, offsetX, offsetY, style, thickness);
                break;
        }
    }

    /**
     * スタイルに応じた線の太さを取得
     */
    private double getThickness(DrawStyle style) {
        switch (style) {
            case THICK:
                return 1.5 + rand.nextDouble() * 0.5;
            case THIN:
                return 0.6 + rand.nextDouble() * 0.3;
            case NORMAL:
            default:
                return 1.0 + rand.nextDouble() * 0.3;
        }
    }

    /**
     * 楕円を描画（改良版）
     */
    private void drawEllipse(double[][] img, int cx, int cy, double radiusX, double radiusY,
                             double thickness, boolean filled) {
        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                double dx = (x - cx) / radiusX;
                double dy = (y - cy) / radiusY;
                double distance = dx * dx + dy * dy;

                if (filled) {
                    if (distance <= 1.0) {
                        img[y][x] = 1.0;
                    }
                } else {
                    // 輪郭のみ
                    double innerRadius = Math.max(0, 1.0 - thickness / Math.min(radiusX, radiusY));
                    if (distance <= 1.0 && distance >= innerRadius * innerRadius) {
                        double intensity = 1.0 - Math.abs(distance - ((1.0 + innerRadius * innerRadius) / 2)) * 2;
                        img[y][x] = Math.max(img[y][x], intensity);
                    }
                }
            }
        }
    }

    /**
     * 線を描画（改良版）
     */
    private void drawLine(double[][] img, int x1, int y1, int x2, int y2, double thickness) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            // 太さを考慮して周囲のピクセルも描画
            for (int ty = -1; ty <= 1; ty++) {
                for (int tx = -1; tx <= 1; tx++) {
                    int px = x1 + tx;
                    int py = y1 + ty;
                    if (px >= 0 && px < imageSize && py >= 0 && py < imageSize) {
                        double distance = Math.sqrt(tx * tx + ty * ty);
                        if (distance <= thickness / 2) {
                            double intensity = 1.0 - distance / (thickness / 2);
                            img[py][px] = Math.max(img[py][px], intensity);
                        }
                    }
                }
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    /**
     * 曲線を描画
     */
    private void drawCurve(double[][] img, int startX, int startY, int endX, int endY,
                           int controlX, int controlY, double thickness) {
        // 2次ベジェ曲線
        for (double t = 0; t <= 1.0; t += 0.01) {
            double x = (1-t)*(1-t)*startX + 2*(1-t)*t*controlX + t*t*endX;
            double y = (1-t)*(1-t)*startY + 2*(1-t)*t*controlY + t*t*endY;

            int px = (int)Math.round(x);
            int py = (int)Math.round(y);

            if (px >= 0 && px < imageSize && py >= 0 && py < imageSize) {
                // 太さを適用
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = px + dx;
                        int ny = py + dy;
                        if (nx >= 0 && nx < imageSize && ny >= 0 && ny < imageSize) {
                            double distance = Math.sqrt(dx * dx + dy * dy);
                            if (distance <= thickness / 2) {
                                img[ny][nx] = Math.max(img[ny][nx], 1.0 - distance / thickness);
                            }
                        }
                    }
                }
            }
        }
    }

    // 各数字の描画メソッド（改良版）
    private void drawZero(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;
        double radiusX = 6.0 + rand.nextDouble() * 2;
        double radiusY = 8.0 + rand.nextDouble() * 2;

        if (style == DrawStyle.ANGULAR) {
            // 角張った0（菱形風）
            int top = cy - 8;
            int bottom = cy + 8;
            int left = cx - 6;
            int right = cx + 6;

            drawLine(img, left + 2, top, right - 2, top, thickness);
            drawLine(img, right - 2, top, right, top + 2, thickness);
            drawLine(img, right, top + 2, right, bottom - 2, thickness);
            drawLine(img, right, bottom - 2, right - 2, bottom, thickness);
            drawLine(img, right - 2, bottom, left + 2, bottom, thickness);
            drawLine(img, left + 2, bottom, left, bottom - 2, thickness);
            drawLine(img, left, bottom - 2, left, top + 2, thickness);
            drawLine(img, left, top + 2, left + 2, top, thickness);
        } else {
            // 通常の楕円
            drawEllipse(img, cx, cy, radiusX, radiusY, thickness * 1.5, false);
        }
    }

    private void drawOne(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int x = center + ox;
        int startY = center - 8 + oy;
        int endY = center + 8 + oy;

        // メインの縦線
        if (style == DrawStyle.ROUNDED) {
            // わずかに曲がった1
            int controlX = x + rand.nextInt(3) - 1;
            drawCurve(img, x, startY, x, endY, controlX, center + oy, thickness);
        } else {
            drawLine(img, x, startY, x, endY, thickness);
        }

        // 上部の斜め線
        drawLine(img, x - 3, startY + 2, x, startY, thickness * 0.8);

        // ベース（オプション）
        if (rand.nextDouble() > 0.5) {
            drawLine(img, x - 3, endY, x + 3, endY, thickness * 0.7);
        }
    }

    private void drawTwo(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の曲線
        drawCurve(img, cx - 6, cy - 6, cx + 6, cy - 6, cx, cy - 10, thickness);
        drawCurve(img, cx + 6, cy - 6, cx + 6, cy - 2, cx + 8, cy - 4, thickness);

        // 中央から下への斜線
        if (style == DrawStyle.ANGULAR) {
            // 直線的な2
            drawLine(img, cx + 6, cy - 2, cx - 6, cy + 8, thickness);
        } else {
            // 曲線的な2
            drawCurve(img, cx + 6, cy - 2, cx - 6, cy + 8, cx, cy + 2, thickness);
        }

        // 下部の横線
        drawLine(img, cx - 6, cy + 8, cx + 6, cy + 8, thickness);
    }

    private void drawThree(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の曲線
        drawCurve(img, cx - 5, cy - 8, cx + 5, cy - 8, cx, cy - 10, thickness);
        drawCurve(img, cx + 5, cy - 8, cx + 5, cy - 2, cx + 7, cy - 5, thickness);

        // 中央の曲線
        drawCurve(img, cx + 5, cy - 2, cx - 2, cy, cx + 2, cy - 1, thickness);
        drawCurve(img, cx - 2, cy, cx + 5, cy + 2, cx + 2, cy + 1, thickness);

        // 下部の曲線
        drawCurve(img, cx + 5, cy + 2, cx + 5, cy + 8, cx + 7, cy + 5, thickness);
        drawCurve(img, cx + 5, cy + 8, cx - 5, cy + 8, cx, cy + 10, thickness);
    }

    private void drawFour(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 左の斜め線
        if (style == DrawStyle.ANGULAR) {
            drawLine(img, cx - 4, cy - 8, cx - 4, cy + 2, thickness);
        } else {
            drawLine(img, cx - 4, cy - 8, cx - 6, cy + 2, thickness);
        }

        // 横線
        drawLine(img, cx - 6, cy + 2, cx + 6, cy + 2, thickness);

        // 右の縦線
        drawLine(img, cx + 3, cy - 8, cx + 3, cy + 8, thickness);
    }

    private void drawFive(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の横線
        drawLine(img, cx - 6, cy - 8, cx + 6, cy - 8, thickness);

        // 左の縦線
        drawLine(img, cx - 6, cy - 8, cx - 6, cy - 1, thickness);

        // 中央の曲線
        if (style == DrawStyle.ROUNDED) {
            drawCurve(img, cx - 6, cy - 1, cx + 5, cy, cx - 2, cy - 2, thickness);
        } else {
            drawLine(img, cx - 6, cy - 1, cx + 5, cy - 1, thickness);
        }

        // 右の曲線
        drawCurve(img, cx + 5, cy, cx + 5, cy + 6, cx + 7, cy + 3, thickness);

        // 下部の曲線
        drawCurve(img, cx + 5, cy + 6, cx - 5, cy + 8, cx, cy + 9, thickness);
        drawCurve(img, cx - 5, cy + 8, cx - 6, cy + 5, cx - 7, cy + 6, thickness * 0.8);
    }

    private void drawSix(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の曲線から始まる
        drawCurve(img, cx + 4, cy - 8, cx - 4, cy - 6, cx, cy - 9, thickness);

        // 左の曲線（長い）
        drawCurve(img, cx - 4, cy - 6, cx - 6, cy + 6, cx - 5, cy, thickness);

        // 下部の円
        drawEllipse(img, cx, cy + 3, 5.5, 5.5, thickness * 1.2, false);
    }

    private void drawSeven(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の横線
        drawLine(img, cx - 6, cy - 8, cx + 6, cy - 8, thickness);

        // 斜めの線
        if (style == DrawStyle.ANGULAR) {
            drawLine(img, cx + 6, cy - 8, cx - 2, cy + 8, thickness);
        } else {
            // わずかに曲がった7
            drawCurve(img, cx + 6, cy - 8, cx - 2, cy + 8, cx + 2, cy, thickness);
        }

        // 短い横線（オプション）
        if (rand.nextDouble() > 0.6) {
            drawLine(img, cx - 2, cy, cx + 2, cy, thickness * 0.7);
        }
    }

    private void drawEight(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 8の特徴的なくびれを表現
        double topRadiusX = 4.5 + rand.nextDouble();
        double topRadiusY = 4.5 + rand.nextDouble();
        double bottomRadiusX = 5.0 + rand.nextDouble();
        double bottomRadiusY = 5.0 + rand.nextDouble();

        if (style == DrawStyle.ANGULAR) {
            // 角張った8
            // 上部
            drawLine(img, cx - 4, cy - 5, cx + 4, cy - 5, thickness);
            drawLine(img, cx + 4, cy - 5, cx + 4, cy - 1, thickness);
            drawLine(img, cx + 4, cy - 1, cx - 4, cy - 1, thickness);
            drawLine(img, cx - 4, cy - 1, cx - 4, cy - 5, thickness);

            // 下部
            drawLine(img, cx - 5, cy + 1, cx + 5, cy + 1, thickness);
            drawLine(img, cx + 5, cy + 1, cx + 5, cy + 6, thickness);
            drawLine(img, cx + 5, cy + 6, cx - 5, cy + 6, thickness);
            drawLine(img, cx - 5, cy + 6, cx - 5, cy + 1, thickness);
        } else {
            // 通常の8（くびれあり）
            // 上部の円
            drawEllipse(img, cx, cy - 3, topRadiusX, topRadiusY, thickness * 1.2, false);

            // 下部の円（少し大きめ）
            drawEllipse(img, cx, cy + 3, bottomRadiusX, bottomRadiusY, thickness * 1.2, false);

            // くびれ部分の接続を改善
            double connectThickness = thickness * 0.8;
            // 左側の接続
            drawCurve(img, (int) (cx - topRadiusX + 1), cy - 1,
                    (int) (cx - bottomRadiusX + 1), cy + 1,
                    (int) (cx - topRadiusX * 0.7), cy, connectThickness);
            // 右側の接続
            drawCurve(img, (int) (cx + topRadiusX - 1), cy - 1,
                    (int) (cx + bottomRadiusX - 1), cy + 1,
                    (int) (cx + topRadiusX * 0.7), cy, connectThickness);
        }
    }

    private void drawNine(double[][] img, int ox, int oy, DrawStyle style, double thickness) {
        int cx = center + ox;
        int cy = center + oy;

        // 上部の円
        drawEllipse(img, cx, cy - 3, 5.5, 5.5, thickness * 1.2, false);

        // 右の曲線（長い）
        drawCurve(img, cx + 4, cy + 2, cx + 4, cy + 8, cx + 5, cy + 5, thickness);

        // 下部の曲線
        if (style == DrawStyle.ROUNDED) {
            drawCurve(img, cx + 4, cy + 8, cx - 4, cy + 6, cx, cy + 9, thickness);
        } else {
            drawLine(img, cx + 4, cy + 8, cx - 2, cy + 8, thickness);
        }
    }

    /**
     * ランダムな変形を適用（改良版）
     */
    private double[][] applyRandomTransform(double[][] image) {
        // 回転
        double angle = (rand.nextDouble() - 0.5) * 0.4;  // ±20度程度
        image = rotate(image, angle);

        // スケーリング
        double scale = 0.85 + rand.nextDouble() * 0.3;  // 0.85-1.15倍
        if (Math.abs(scale - 1.0) > 0.05) {
            image = scale(image, scale);
        }

        // 歪み（スキュー）
        if (rand.nextDouble() > 0.7) {
            image = skew(image, (rand.nextDouble() - 0.5) * 0.2);
        }

        // 弾性変形
        if (rand.nextDouble() > 0.8) {
            image = elasticDeform(image);
        }

        return image;
    }

    /**
     * スキュー変換
     */
    private double[][] skew(double[][] image, double factor) {
        double[][] skewed = new double[imageSize][imageSize];
        int cx = imageSize / 2;
        int cy = imageSize / 2;

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                int srcX = x - (int)((y - cy) * factor);

                if (srcX >= 0 && srcX < imageSize) {
                    skewed[y][x] = image[y][srcX];
                }
            }
        }

        return skewed;
    }

    /**
     * 弾性変形（簡易版）
     */
    private double[][] elasticDeform(double[][] image) {
        double[][] deformed = new double[imageSize][imageSize];
        double amplitude = 2.0;
        double frequency = 0.1;

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                double dx = amplitude * Math.sin(frequency * y);
                double dy = amplitude * Math.sin(frequency * x);

                int srcX = (int)(x + dx);
                int srcY = (int)(y + dy);

                if (srcX >= 0 && srcX < imageSize && srcY >= 0 && srcY < imageSize) {
                    deformed[y][x] = image[srcY][srcX];
                }
            }
        }

        return deformed;
    }

    /**
     * 画像を回転
     */
    private double[][] rotate(double[][] image, double angle) {
        double[][] rotated = new double[imageSize][imageSize];
        int cx = imageSize / 2;
        int cy = imageSize / 2;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                // 回転前の座標を計算
                int dx = x - cx;
                int dy = y - cy;
                double srcX = cos * dx + sin * dy + cx;
                double srcY = -sin * dx + cos * dy + cy;

                // バイリニア補間
                int x0 = (int)Math.floor(srcX);
                int y0 = (int)Math.floor(srcY);
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                if (x0 >= 0 && x1 < imageSize && y0 >= 0 && y1 < imageSize) {
                    double fx = srcX - x0;
                    double fy = srcY - y0;

                    double val = (1 - fx) * (1 - fy) * image[y0][x0] +
                            fx * (1 - fy) * image[y0][x1] +
                            (1 - fx) * fy * image[y1][x0] +
                            fx * fy * image[y1][x1];

                    rotated[y][x] = val;
                }
            }
        }

        return rotated;
    }

    /**
     * 画像をスケーリング（バイリニア補間付き）
     */
    private double[][] scale(double[][] image, double factor) {
        double[][] scaled = new double[imageSize][imageSize];
        int cx = imageSize / 2;
        int cy = imageSize / 2;

        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                double srcX = (x - cx) / factor + cx;
                double srcY = (y - cy) / factor + cy;

                // バイリニア補間
                int x0 = (int)Math.floor(srcX);
                int y0 = (int)Math.floor(srcY);
                int x1 = x0 + 1;
                int y1 = y0 + 1;

                if (x0 >= 0 && x1 < imageSize && y0 >= 0 && y1 < imageSize) {
                    double fx = srcX - x0;
                    double fy = srcY - y0;

                    double val = (1 - fx) * (1 - fy) * image[y0][x0] +
                            fx * (1 - fy) * image[y0][x1] +
                            (1 - fx) * fy * image[y1][x0] +
                            fx * fy * image[y1][x1];

                    scaled[y][x] = val;
                }
            }
        }

        return scaled;
    }

    /**
     * ノイズを追加（改良版）
     */
    private void addNoise(double[][] image, double noiseLevel) {
        // ガウシアンノイズ
        for (int y = 0; y < imageSize; y++) {
            for (int x = 0; x < imageSize; x++) {
                double noise = rand.nextGaussian() * noiseLevel * 0.5;
                image[y][x] = Math.max(0.0, Math.min(1.0, image[y][x] + noise));
            }
        }

        // ソルト＆ペッパーノイズ（低確率）
        if (rand.nextDouble() > 0.8) {
            int numPoints = (int)(imageSize * imageSize * noiseLevel * 0.01);
            for (int i = 0; i < numPoints; i++) {
                int x = rand.nextInt(imageSize);
                int y = rand.nextInt(imageSize);
                image[y][x] = rand.nextDouble() > 0.5 ? 1.0 : 0.0;
            }
        }
    }

    /**
     * 画像をスムージング（改良版ガウシアンフィルタ）
     */
    private double[][] smoothImage(double[][] image) {
        double[][] smoothed = new double[imageSize][imageSize];
        double[][] kernel = {
                {0.0625, 0.125, 0.0625},
                {0.125,  0.25,  0.125},
                {0.0625, 0.125, 0.0625}
        };

        for (int y = 1; y < imageSize - 1; y++) {
            for (int x = 1; x < imageSize - 1; x++) {
                double sum = 0.0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        sum += image[y + ky][x + kx] * kernel[ky + 1][kx + 1];
                    }
                }
                smoothed[y][x] = sum;
            }
        }

        // 境界をコピー
        for (int i = 0; i < imageSize; i++) {
            smoothed[0][i] = image[0][i];
            smoothed[imageSize-1][i] = image[imageSize-1][i];
            smoothed[i][0] = image[i][0];
            smoothed[i][imageSize-1] = image[i][imageSize-1];
        }

        return smoothed;
    }

    /**
     * 画像をコンソールに表示（デバッグ用）
     */
    public static void printImage(double[][] image) {
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                if (image[y][x] > 0.8) {
                    System.out.print("█");
                } else if (image[y][x] > 0.6) {
                    System.out.print("▓");
                } else if (image[y][x] > 0.3) {
                    System.out.print("▒");
                } else if (image[y][x] > 0.1) {
                    System.out.print("░");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }

    /**
     * テスト用メイン
     */
    public static void main(String[] args) {
        SimpleDigitGenerator generator = new SimpleDigitGenerator();

        // 各数字を複数回生成して表示
        for (int digit = 0; digit <= 9; digit++) {
            System.out.println("\n=== Digit " + digit + " ===");
            for (int i = 0; i < 3; i++) {
                System.out.println("\nVariation " + (i + 1) + ":");
                double[][] image = generator.generateDigit(digit, 0.1);
                printImage(image);
            }
        }
    }
}