package ui.ra;

import datasets.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Objects;

public class GUI extends JFrame {

    private final Viewer viewer;
    private final JLabel statusLabel;
    private final JProgressBar progressBar;
    private final JButton startButton;
    private final JButton stopButton;
    private final JComboBox<String> functionSelector;
    private final JSlider noiseSlider;
    private final JLabel noiseLabel;

    private Fn currentFn;

    // ActionListenerを保持するフィールド
    private ActionListener startAction;
    private ActionListener stopAction;

    public GUI() {
        super("Jeeplearning - Regression Analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ビューアーコンポーネント
        viewer = new Viewer();
        add(viewer, BorderLayout.CENTER);

        // コントロールパネル（上部）
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));

        // 関数選択
        controlPanel.add(new JLabel("Function:"));
        functionSelector = new JComboBox<>(new String[]{
                "Sin(x)",
                "Quadratic",
                "Circle",
                "Spiral",
                "Lemniscate",
                "Limacon",
                "Gaussian",
                "Damped Oscillation",
                "Step Function",
                "Chirp Signal",
                "Sawtooth Wave",
                "Absolute Value"
        });
        functionSelector.setPreferredSize(new Dimension(140, 25));
        controlPanel.add(functionSelector);

        // ノイズレベル調整
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("Noise:"));
        noiseSlider = new JSlider(0, 100, 20); // 0～1.0を0～100で表現（初期値0.2）
        noiseSlider.setPreferredSize(new Dimension(150, 25));
        noiseSlider.setMajorTickSpacing(25);
        noiseSlider.setMinorTickSpacing(5);
        noiseSlider.setPaintTicks(true);
        controlPanel.add(noiseSlider);

        noiseLabel = new JLabel("0.20");
        noiseLabel.setPreferredSize(new Dimension(40, 25));
        controlPanel.add(noiseLabel);

        // ノイズスライダーのリスナー
        noiseSlider.addChangeListener(e -> {
            double noise = noiseSlider.getValue() / 100.0;
            noiseLabel.setText(String.format("%.2f", noise));
        });

        // Startボタン
        controlPanel.add(Box.createHorizontalStrut(20));
        startButton = new JButton("Start Training");
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        controlPanel.add(startButton);

        // Stopボタン
        stopButton = new JButton("Stop");
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.BLACK);
        stopButton.setFocusPainted(false);
        stopButton.setEnabled(false);
        controlPanel.add(stopButton);

        add(controlPanel, BorderLayout.NORTH);

        // ステータスパネル（下部）
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        statusLabel = new JLabel("Ready - Select a function and click 'Start Training'");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(200, 20));
        statusPanel.add(progressBar, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // ウィンドウ設定
        setSize(1200, 850);
        setLocationRelativeTo(null);
    }

    public Viewer getViewer() {
        return viewer;
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void setProgress(int progress) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(progress);
            progressBar.setString(progress + "%");
        });
    }

    public void setStartEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> startButton.setEnabled(enabled));
    }

    public void setStopEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> stopButton.setEnabled(enabled));
    }

    public void setStartAction(ActionListener action) {
        // ActionListenerを保持
        this.startAction = action;

        // 既存のリスナーを削除
        for (ActionListener al : startButton.getActionListeners()) {
            startButton.removeActionListener(al);
        }
        startButton.addActionListener(action);
    }

    public void setStopAction(ActionListener action) {
        // ActionListenerを保持
        this.stopAction = action;

        // 既存のリスナーを削除
        for (ActionListener al : stopButton.getActionListeners()) {
            stopButton.removeActionListener(al);
        }
        stopButton.addActionListener(action);
    }

    public double getNoiseRate() {
        return noiseSlider.getValue() / 100.0;
    }

    public Fn getSelectedFunction() {
        String selected = (String) functionSelector.getSelectedItem();
        Fn function = switch (Objects.requireNonNull(selected)) {
            case "Circle" -> new Circle();
            case "Spiral" -> new Spiral();
            case "Lemniscate" -> new Lemniscate();
            case "Limacon" -> new Limacon();
            case "Quadratic" -> new Quadratic();
            case "Gaussian" -> new Gaussian();
            case "Damped Oscillation" -> new DampedOscillation();
            case "Step Function" -> new StepFunction();
            case "Chirp Signal" -> new Chirp();
            case "Sawtooth Wave" -> new SawtoothWave();
            case "Absolute Value" -> new AbsoluteValue();
            default -> new Sin();
        };

        // データセット生成
        function.generateDataset(Launcher.TRAIN_SIZE, Launcher.TEST_SIZE, getNoiseRate());
        currentFn = function;
        return function;
    }

    public Fn getCurrentFunction() {
        return currentFn;
    }

    // 保持されたActionListenerを返す
    public ActionListener getStartAction() {
        return startAction;
    }

    public ActionListener getStopAction() {
        return stopAction;
    }

    // 古いメソッドとの互換性のため
    public void setReloadEnabled(boolean enabled) {
        setStartEnabled(enabled);
    }

    public void setReloadAction(ActionListener action) {
        setStartAction(action);
    }

    public ActionListener getReloadAction() {
        return getStartAction();
    }
}