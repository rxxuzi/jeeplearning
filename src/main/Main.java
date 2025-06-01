package main;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    private JPanel mainPanel;
    private CardLayout cardLayout;

    // 各機能のランチャー
    private ui.ra.Launcher regressionLauncher;
    private ui.cnn.Launcher cnnLauncher;

    // パネル
    private JPanel welcomePanel;
    private JPanel regressionPanel;
    private JPanel cnnPanel;

    public Main() {
        super("Jeeplearning - Deep Learning in Pure Java");
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        // システムのルックアンドフィールを使用
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // メニューバーの設定
        setJMenuBar(createMenuBar());

        // カードレイアウトでパネルを切り替え
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // ウェルカムパネル
        welcomePanel = createWelcomePanel();
        mainPanel.add(welcomePanel, "welcome");

        // プレースホルダーパネル（遅延初期化用）
        mainPanel.add(new JPanel(), "regression");
        mainPanel.add(new JPanel(), "cnn");

        add(mainPanel);

        // 初期表示はウェルカムパネル
        cardLayout.show(mainPanel, "welcome");
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Fileメニュー
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // Applicationメニュー
        JMenu appMenu = new JMenu("Applications");
        appMenu.setMnemonic('A');

        JMenuItem homeItem = new JMenuItem("Home");
        homeItem.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
        homeItem.addActionListener(e -> showPanel("welcome"));
        appMenu.add(homeItem);

        appMenu.addSeparator();

        JMenuItem regressionItem = new JMenuItem("Function Regression");
        regressionItem.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        regressionItem.addActionListener(e -> showRegressionAnalysis());
        appMenu.add(regressionItem);

        JMenuItem cnnItem = new JMenuItem("Digit Recognition (CNN)");
        cnnItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        cnnItem.addActionListener(e -> showCNN());
        appMenu.add(cnnItem);

        menuBar.add(appMenu);

        // Windowメニュー
        JMenu windowMenu = new JMenu("Window");
        windowMenu.setMnemonic('W');

        JMenuItem fullscreenItem = new JMenuItem("Toggle Fullscreen");
        fullscreenItem.setAccelerator(KeyStroke.getKeyStroke("F11"));
        fullscreenItem.addActionListener(e -> toggleFullscreen());
        windowMenu.add(fullscreenItem);

        menuBar.add(windowMenu);

        // Helpメニュー
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem tutorialItem = new JMenuItem("Tutorial");
        tutorialItem.addActionListener(e -> showTutorial());
        helpMenu.add(tutorialItem);

        helpMenu.addSeparator();

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 240, 240));

        // タイトル部分
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        // タイトル
        titlePanel.add(Box.createVerticalStrut(50));

        JLabel titleLabel = new JLabel("Jeeplearning");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Deep Learning in Pure Java");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.NORTH);

        // 機能選択ボタン
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 回帰分析ボタン
        JButton regressionButton = createFeatureButton(
                "Function Regression",
                "Neural network regression analysis for various mathematical functions",
                new Color(52, 152, 219)
        );
        regressionButton.addActionListener(e -> showRegressionAnalysis());
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(regressionButton, gbc);

        // CNN手書き認識ボタン
        JButton cnnButton = createFeatureButton(
                "Digit Recognition (CNN)",
                "Handwritten digit recognition using Convolutional Neural Networks",
                new Color(46, 204, 113)
        );
        cnnButton.addActionListener(e -> showCNN());
        gbc.gridx = 1;
        gbc.gridy = 0;
        buttonPanel.add(cnnButton, gbc);

        // 将来の機能用プレースホルダー
        JButton autoencoderButton = createFeatureButton(
                "Image Reconstruction",
                "Coming soon: Self-supervised learning with autoencoders",
                new Color(155, 89, 182)
        );
        autoencoderButton.setEnabled(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        buttonPanel.add(autoencoderButton, gbc);

        JButton ganButton = createFeatureButton(
                "Image Generation",
                "Coming soon: Generative Adversarial Networks",
                new Color(241, 196, 15)
        );
        ganButton.setEnabled(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        buttonPanel.add(ganButton, gbc);

        panel.add(buttonPanel, BorderLayout.CENTER);

        // フッター
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));

        JLabel footerLabel = new JLabel("Pure Java implementation - No external ML libraries required");
        footerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        footerLabel.setForeground(new Color(120, 120, 120));
        footerPanel.add(footerLabel);

        JLabel versionLabel = new JLabel("Version 1.0");
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(150, 150, 150));
        footerPanel.add(Box.createVerticalStrut(5));
        footerPanel.add(versionLabel);

        footerPanel.add(Box.createVerticalStrut(20));
        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createFeatureButton(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(320, 160));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(10));

        JTextArea descLabel = new JTextArea(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setLineWrap(true);
        descLabel.setWrapStyleWord(true);
        descLabel.setOpaque(false);
        descLabel.setEditable(false);
        descLabel.setFocusable(false);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descLabel);

        button.add(contentPanel);

        // スタイリング
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ホバーエフェクト
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }

    /**
     * 回帰分析を表示
     */
    private void showRegressionAnalysis() {
        // 遅延初期化
        if (regressionLauncher == null) {
            regressionLauncher = new ui.ra.Launcher();
            Container contentPane = regressionLauncher.getGUI().getContentPane();
            if (contentPane instanceof JPanel) {
                regressionPanel = (JPanel) contentPane;
            } else {
                regressionPanel = new JPanel(new BorderLayout());
                regressionPanel.add(contentPane);
            }
            mainPanel.add(regressionPanel, "regression");
        }

        showPanel("regression");

        // 自動開始は削除（手動でStartボタンを押す）
    }

    /**
     * CNN手書き認識を表示
     */
    private void showCNN() {
        // 遅延初期化
        if (cnnLauncher == null) {
            cnnLauncher = new ui.cnn.Launcher();
            Container contentPane = cnnLauncher.getGUI().getContentPane();
            if (contentPane instanceof JPanel) {
                cnnPanel = (JPanel) contentPane;
            } else {
                cnnPanel = new JPanel(new BorderLayout());
                cnnPanel.add(contentPane);
            }
            mainPanel.add(cnnPanel, "cnn");
        }

        showPanel("cnn");
    }

    /**
     * フルスクリーン切り替え
     */
    private void toggleFullscreen() {
        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (device.isFullScreenSupported()) {
            if (device.getFullScreenWindow() == null) {
                device.setFullScreenWindow(this);
            } else {
                device.setFullScreenWindow(null);
            }
        }
    }

    private void showTutorial() {
        String tutorial = "Welcome to Jeeplearning!\n\n" +
                "1. Function Regression:\n" +
                "   - Select a function from the dropdown\n" +
                "   - Adjust noise level with the slider\n" +
                "   - Click 'Reload' to retrain\n\n" +
                "2. Digit Recognition:\n" +
                "   - Draw a digit with your mouse\n" +
                "   - See real-time predictions\n" +
                "   - Train the model for better accuracy\n\n" +
                "Use Ctrl+R for Regression, Ctrl+D for Digit Recognition";

        JOptionPane.showMessageDialog(this, tutorial, "Tutorial",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String message = "Jeeplearning v1.0\n\n" +
                "A pure Java implementation of deep learning algorithms\n" +
                "including neural networks, CNNs, and more.\n\n" +
                "Features:\n" +
                "• Function Regression with Neural Networks\n" +
                "• Handwritten Digit Recognition with CNN\n" +
                "• No external ML libraries required\n\n" +
                "Created as an educational project to understand\n" +
                "deep learning from first principles.\n\n" +
                "© 2025 Jeeplearning Project";

        JOptionPane.showMessageDialog(this, message, "About Jeeplearning",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}