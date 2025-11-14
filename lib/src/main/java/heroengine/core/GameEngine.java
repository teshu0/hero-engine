package heroengine.core;

import heroengine.ecs.EntityManager;
import heroengine.ecs.GameSystem;
import heroengine.input.InputManager;
import heroengine.systems.RenderSystem;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ゲームエンジンのメインクラス ゲームループとシステム管理を担当
 */
public class GameEngine {

    private final String title;
    private final int width;
    private final int height;

    private JFrame frame;
    private GamePanel gamePanel;
    private EntityManager entityManager;
    private List<GameSystem> systems;
    private RenderSystem renderSystem;
    private InputManager inputManager;

    private boolean running;
    private Thread gameThread;
    private final int targetFPS;

    public GameEngine(String title, int width, int height) {
        this(title, width, height, 60);
    }

    public GameEngine(String title, int width, int height, int targetFPS) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.targetFPS = targetFPS;
        this.systems = new ArrayList<>();
        this.entityManager = new EntityManager();
        this.inputManager = new InputManager();
        this.running = false;
    }

    /**
     * エンジンの初期化
     */
    public void init() {
        // Swingの初期化（同期実行）
        try {
            SwingUtilities.invokeAndWait(() -> {
                frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);

                gamePanel = new GamePanel();
                gamePanel.setPreferredSize(new Dimension(width, height));
                gamePanel.setBackground(Color.BLACK);

                frame.add(gamePanel);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

                // 入力リスナーを登録
                frame.addKeyListener(inputManager);
                gamePanel.addMouseListener(inputManager.getMouseAdapter());
                gamePanel.addMouseMotionListener(inputManager.getMouseAdapter());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // すべてのシステムを初期化
        for (GameSystem system : systems) {
            system.setEntityManager(entityManager);
            system.init();
        }
    }

    /**
     * システムを追加
     */
    public void addSystems(GameSystem... systems) {
        for (GameSystem system : systems) {

            this.systems.add(system);
            system.setEntityManager(entityManager);

            // 既に初期化済みの場合は、追加されたシステムも初期化
            if (frame != null) {
                system.init();
            }

            // RenderSystemは特別に保持
            if (system instanceof RenderSystem) {
                renderSystem = (RenderSystem) system;
            }
        }
    }

    /**
     * エンティティマネージャーを取得
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * ゲームループを開始
     */
    public void start() {
        if (running) {
            return;
        }

        running = true;
        gameThread = new Thread(this::gameLoop);
        gameThread.start();
    }

    /**
     * ゲームループを停止
     */
    public void stop() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // すべてのシステムをシャットダウン
        for (GameSystem system : systems) {
            system.shutdown();
        }
    }

    /**
     * ゲームループ
     */
    private void gameLoop() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / targetFPS;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;

            if (delta >= 1) {
                float deltaTime = (float) delta / targetFPS;
                update(deltaTime);
                delta = 0;
            }

            // CPU使用率を下げる
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新処理
     */
    private void update(float deltaTime) {
        // エンティティの追加・削除を反映
        entityManager.refresh();

        // すべてのシステムを更新
        for (GameSystem system : systems) {
            if (system.isEnabled()) {
                system.update(deltaTime);
            }
        }

        // 入力状態をクリア（フレームの最後に実行）
        inputManager.update();
    }

    /**
     * ゲームパネル（描画用）
     */
    private class GamePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (renderSystem != null) {
                Graphics2D g2d = (Graphics2D) g;

                // アンチエイリアス有効化
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                renderSystem.render(g2d);
            }
        }
    }

    public JFrame getFrame() {
        return frame;
    }

    public JPanel getGamePanel() {
        return gamePanel;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
