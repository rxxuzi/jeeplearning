package ui.cnn;

import javax.swing.*;

/**
 * CNN手書き数字認識アプリケーションのランチャー
 * スタンドアロンで実行可能
 */
public class Launcher {

    private GUI gui;

    /**
     * コンストラクタ
     */
    public Launcher() {
        this.gui = new GUI();
    }

    /**
     * GUIを取得
     */
    public GUI getGUI() {
        return gui;
    }

    /**
     * アプリケーションを起動
     */
    public void launch() {
        // システムのルックアンドフィールを使用
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            gui.setVisible(true);
        });
    }

    /**
     * スタンドアロン実行用メインメソッド
     */
    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.launch();
    }
}