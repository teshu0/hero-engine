package heroengine.demo;

import heroengine.components.BoxCollider;
import heroengine.components.Sprite;
import heroengine.components.Transform;
import heroengine.components.Velocity;
import heroengine.core.GameEngine;
import heroengine.ecs.Entity;
import heroengine.ecs.EntityManager;
import heroengine.input.InputManager;
import heroengine.systems.CollisionSystem;
import heroengine.systems.MovementSystem;
import heroengine.systems.RenderSystem;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * サンプルゲームデモ キーボードで操作できるプレイヤーと動く障害物
 */
public class SampleGame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        // ゲームエンジンを作成
        GameEngine engine = new GameEngine("Hero Engine - Sample Game", WIDTH, HEIGHT);

        // エンジンの初期化（この時点でウィンドウとパネルが作成される）
        engine.init();

        // 入力マネージャーを取得
        InputManager inputManager = engine.getInputManager();

        // システムを追加
        engine.addSystems(
                new RenderSystem(engine.getGamePanel()),
                new MovementSystem(),
                new PlayerControlSystem(inputManager),
                new CollisionSystem() {
            @Override
            protected void onCollision(Entity a, Entity b) {
                // 衝突時にプレイヤーの色を変更
                if (a.hasComponent(PlayerTag.class)) {
                    a.getComponent(Sprite.class).get().setColor(Color.RED);
                }
                if (b.hasComponent(PlayerTag.class)) {
                    b.getComponent(Sprite.class).get().setColor(Color.RED);
                }
            }
        }
        );

        // エンティティを作成
        createEntities(engine.getEntityManager());

        // ゲームを開始
        engine.start();
    }

    /**
     * ゲームエンティティを作成
     */
    private static void createEntities(EntityManager entityManager) {
        // プレイヤーエンティティ
        Entity player = entityManager.createEntity();
        player.addComponents(
                new Transform(WIDTH / 2, HEIGHT / 2),
                new Sprite(40, 40, Color.GREEN),
                new Velocity(),
                new BoxCollider(40, 40),
                new PlayerTag()
        );

        // 障害物1
        Entity obstacle1 = entityManager.createEntity();
        obstacle1.addComponents(
                new Transform(200, 150),
                new Sprite(60, 60, Color.BLUE),
                new Velocity(50, 30),
                new BoxCollider(60, 60)
        );

        // 障害物2
        Entity obstacle2 = entityManager.createEntity();
        obstacle2.addComponents(
                new Transform(600, 400),
                new Sprite(80, 40, Color.YELLOW),
                new Velocity(-70, -40),
                new BoxCollider(80, 40)
        );

        // 障害物3
        Entity obstacle3 = entityManager.createEntity();
        obstacle3.addComponents(
                new Transform(400, 500),
                new Sprite(50, 50, Color.MAGENTA),
                new Velocity(60, -60),
                new BoxCollider(50, 50)
        );

        // 静的な壁
        createWall(entityManager, WIDTH / 2, 50, WIDTH - 100, 20);
        createWall(entityManager, WIDTH / 2, HEIGHT - 50, WIDTH - 100, 20);
        createWall(entityManager, 50, HEIGHT / 2, 20, HEIGHT - 100);
        createWall(entityManager, WIDTH - 50, HEIGHT / 2, 20, HEIGHT - 100);
    }

    /**
     * 壁を作成
     */
    private static void createWall(EntityManager entityManager, float x, float y, int width, int height) {
        Entity wall = entityManager.createEntity();
        wall.addComponents(
                new Transform(x, y),
                new Sprite(width, height, Color.GRAY),
                new BoxCollider(width, height)
        );
    }

    /**
     * プレイヤータグコンポーネント（識別用）
     */
    private static class PlayerTag implements heroengine.ecs.Component {
    }

    /**
     * プレイヤー操作システム
     */
    private static class PlayerControlSystem extends heroengine.ecs.GameSystem {

        private final InputManager inputManager;
        private static final float SPEED = 200f;

        public PlayerControlSystem(InputManager inputManager) {
            this.inputManager = inputManager;
        }

        @Override
        public void update(float deltaTime) {
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, Velocity.class, PlayerTag.class)) {
                Velocity velocity = entity.getComponent(Velocity.class).get();
                Sprite sprite = entity.getComponent(Sprite.class).orElse(null);

                // キー入力で速度を設定
                float vx = 0;
                float vy = 0;

                if (inputManager.isKeyPressed(KeyEvent.VK_LEFT) || inputManager.isKeyPressed(KeyEvent.VK_A)) {
                    vx -= SPEED;
                }
                if (inputManager.isKeyPressed(KeyEvent.VK_RIGHT) || inputManager.isKeyPressed(KeyEvent.VK_D)) {
                    vx += SPEED;
                }
                if (inputManager.isKeyPressed(KeyEvent.VK_UP) || inputManager.isKeyPressed(KeyEvent.VK_W)) {
                    vy -= SPEED;
                }
                if (inputManager.isKeyPressed(KeyEvent.VK_DOWN) || inputManager.isKeyPressed(KeyEvent.VK_S)) {
                    vy += SPEED;
                }

                velocity.set(vx, vy);

                // 移動していたら色を戻す
                if (sprite != null && (vx != 0 || vy != 0)) {
                    sprite.setColor(Color.GREEN);
                }
            }

            // 障害物の壁バウンス
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, Velocity.class)) {
                if (entity.hasComponent(PlayerTag.class)) {
                    continue; // プレイヤーはスキップ
                }

                Transform transform = entity.getComponent(Transform.class).get();
                Velocity velocity = entity.getComponent(Velocity.class).get();
                Sprite sprite = entity.getComponent(Sprite.class).orElse(null);

                if (sprite != null) {
                    float halfWidth = sprite.width / 2f;
                    float halfHeight = sprite.height / 2f;

                    // 画面端でバウンス
                    if (transform.x - halfWidth < 0 || transform.x + halfWidth > WIDTH) {
                        velocity.vx = -velocity.vx;
                    }
                    if (transform.y - halfHeight < 0 || transform.y + halfHeight > HEIGHT) {
                        velocity.vy = -velocity.vy;
                    }
                }
            }
        }
    }
}
