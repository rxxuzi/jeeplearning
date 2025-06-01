package main;

import datasets.SimpleDigitGenerator;

public class Tests {
    public static void printImage(double[][] image) {
        for (int y = 0; y < image.length; y++) {
            for (int x = 0; x < image[0].length; x++) {
                if (image[y][x] > 0.7) {
                    System.out.print("█");
                } else if (image[y][x] > 0.3) {
                    System.out.print("▓");
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
        var generator = new SimpleDigitGenerator();

        // 各数字を生成して表示
        for (int digit = 0; digit <= 9; digit++) {
            System.out.println("\n=== Digit " + digit + " ===");
            double[][] image = generator.generateDigit(digit, 0.1);
            printImage(image);
        }
    }
}
