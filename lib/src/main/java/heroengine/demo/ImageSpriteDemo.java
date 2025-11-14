package heroengine.demo;

import heroengine.components.ImageSprite;
import heroengine.components.Transform;
import heroengine.components.Velocity;
import heroengine.core.GameEngine;
import heroengine.ecs.Entity;
import heroengine.ecs.GameSystem;
import heroengine.input.InputManager;
import heroengine.systems.MovementSystem;
import heroengine.systems.RenderSystem;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * ImageSpriteコンポーネントのデモ 画像を読み込んで表示し、キーボードで移動できます
 */
public class ImageSpriteDemo {

    public static void main(String[] args) {
        GameEngine engine = new GameEngine("ImageSprite Demo", 800, 600);
        engine.init();

        try {
            // サンプル画像を生成（実際の使用では画像ファイルを読み込みます）
            BufferedImage sampleImage = createSampleImage(100, 100);

            // 画像エンティティを作成
            Entity imageEntity = engine.getEntityManager().spawn(
                    new Transform(400, 300),
                    new ImageSprite(sampleImage),
                    new Velocity(0, 0)
            );
            imageEntity.getComponent(ImageSprite.class).get().setZOrder(1);

            // 2つ目の画像エンティティ（半透明）
            BufferedImage sampleImage2 = createSampleImage2(80, 80);
            Entity imageEntity2 = engine.getEntityManager().spawn(
                    new Transform(300, 200),
                    new ImageSprite(sampleImage2)
            );
            ImageSprite imageSprite2 = imageEntity2.getComponent(ImageSprite.class).get();
            imageSprite2.setAlpha(0.7f); // 70%の不透明度
            imageSprite2.setZOrder(2);

            // 3つ目の画像エンティティ（回転サンプル）
            BufferedImage sampleImage3 = createSampleImage3(60, 60);
            Transform transform3 = new Transform(500, 400);
            transform3.rotation = (float) (Math.PI / 4); // 45度回転
            Entity imageEntity3 = engine.getEntityManager().spawn(
                    transform3,
                    new ImageSprite(sampleImage3)
            );
            imageEntity3.getComponent(ImageSprite.class).get().setZOrder(0);

            // システムを追加
            Velocity velocity = imageEntity.getComponent(Velocity.class).get();
            engine.addSystems(
                    new RenderSystem(engine.getGamePanel()),
                    new MovementSystem(),
                    new GameSystem() {
                @Override
                public void update(float deltaTime) {
                    InputManager input = engine.getInputManager();

                    // キーボード入力で速度を制御
                    float speed = 200.0f;
                    velocity.vx = 0;
                    velocity.vy = 0;

                    if (input.isKeyPressed(KeyEvent.VK_LEFT)) {
                        velocity.vx = -speed;
                    }
                    if (input.isKeyPressed(KeyEvent.VK_RIGHT)) {
                        velocity.vx = speed;
                    }
                    if (input.isKeyPressed(KeyEvent.VK_UP)) {
                        velocity.vy = -speed;
                    }
                    if (input.isKeyPressed(KeyEvent.VK_DOWN)) {
                        velocity.vy = speed;
                    }

                    // ESCキーで終了
                    if (input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
                        engine.stop();
                    }

                    // 回転アニメーション
                    transform3.rotation += deltaTime;
                }
            }
            );

            // 操作説明用テキスト
            System.out.println("=== ImageSprite Demo ===");
            System.out.println("矢印キー: 中央の画像を移動");
            System.out.println("ESC: 終了");

            // ゲームループを開始
            engine.start();

        } catch (Exception e) {
            System.err.println("画像の作成に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * サンプル画像を生成（青いグラデーション円）
     */
    private static BufferedImage createSampleImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 背景を透明に
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);

        // グラデーション円を描画
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 2;

        for (int r = radius; r > 0; r--) {
            float ratio = (float) r / radius;
            int alpha = 255;
            int blue = (int) (255 * ratio);
            g.setColor(new Color(0, 100, blue, alpha));
            g.fillOval(centerX - r, centerY - r, r * 2, r * 2);
        }

        g.dispose();
        return image;
    }

    /**
     * サンプル画像を生成（緑の星型）
     */
    private static BufferedImage createSampleImage2(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 背景を透明に
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);

        // 星型を描画
        int centerX = width / 2;
        int centerY = height / 2;
        int outerRadius = Math.min(width, height) / 2;
        int innerRadius = outerRadius / 2;

        int[] xPoints = new int[10];
        int[] yPoints = new int[10];

        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + (2 * Math.PI * i / 10);
            int radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = centerX + (int) (radius * Math.cos(angle));
            yPoints[i] = centerY - (int) (radius * Math.sin(angle));
        }

        g.setColor(new Color(50, 200, 50));
        g.fillPolygon(xPoints, yPoints, 10);

        g.dispose();
        return image;
    }

    /**
     * サンプル画像を生成（赤い四角）
     */
    private static BufferedImage createSampleImage3(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // 背景を透明に
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, width, height);

        // グラデーション四角を描画
        g.setColor(new Color(255, 0, 0));
        g.fillRect(5, 5, width - 10, height - 10);
        g.setColor(new Color(200, 0, 0));
        g.fillRect(10, 10, width - 20, height - 20);

        g.dispose();
        return image;
    }

    /**
     * 実際の画像ファイルを読み込む例
     */
    public static void loadImageFromFile() throws IOException {
        GameEngine engine = new GameEngine("Image From File", 800, 600);
        engine.init();

        // 画像ファイルから読み込み
        // 方法1: ファイルパスから読み込み
        ImageSprite imageSprite = new ImageSprite("path/to/your/image.png");
        engine.getEntityManager().spawn(
                new Transform(400, 300),
                imageSprite
        );

        // 方法2: リソースから読み込み
        // ImageSprite imageSprite = new ImageSprite(
        //     ImageSpriteDemo.class.getResourceAsStream("/images/sprite.png")
        // );
        engine.addSystems(
                new RenderSystem(engine.getGamePanel())
        );

        engine.start();
    }
}
