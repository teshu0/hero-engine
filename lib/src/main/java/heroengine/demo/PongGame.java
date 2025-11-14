package heroengine.demo;

import heroengine.components.BoxCollider;
import heroengine.components.Sprite;
import heroengine.components.Text;
import heroengine.components.Transform;
import heroengine.components.Velocity;
import heroengine.core.GameEngine;
import heroengine.ecs.Component;
import heroengine.ecs.Entity;
import heroengine.ecs.EntityManager;
import heroengine.ecs.GameSystem;
import heroengine.input.InputManager;
import heroengine.systems.CollisionSystem;
import heroengine.systems.MovementSystem;
import heroengine.systems.RenderSystem;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Pongゲームのサンプル実装 矢印キーで左側のパドルを操作し、AIが右側のパドルを操作します
 */
public class PongGame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int PADDLE_WIDTH = 15;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 15;
    private static final float PADDLE_SPEED = 400f;
    private static final float INITIAL_BALL_SPEED = 300f;

    private static int playerScore = 0;
    private static int aiScore = 0;

    public static void main(String[] args) {
        // ゲームエンジンを作成
        GameEngine engine = new GameEngine("Pong Game", WIDTH, HEIGHT);

        // エンジンの初期化
        engine.init();

        // 入力マネージャーを取得
        InputManager inputManager = engine.getInputManager();

        // システムを追加
        engine.addSystems(
                new RenderSystem(engine.getGamePanel()),
                new MovementSystem(),
                new PlayerControlSystem(inputManager),
                new AIControlSystem(),
                new BallBounceSystem(),
                new PongCollisionSystem(),
                new ScoreSystem()
        );

        // エンティティを作成
        createGameEntities(engine.getEntityManager());

        // ゲームを開始
        engine.start();
    }

    /**
     * ゲームエンティティを作成
     */
    private static void createGameEntities(EntityManager entityManager) {
        // 左側のパドル（プレイヤー）
        entityManager.spawn(
                new Transform(30, HEIGHT / 2),
                new Sprite(PADDLE_WIDTH, PADDLE_HEIGHT, Color.WHITE),
                new Velocity(),
                new BoxCollider(PADDLE_WIDTH, PADDLE_HEIGHT),
                new PlayerPaddleTag()
        );

        // 右側のパドル（AI）
        entityManager.spawn(
                new Transform(WIDTH - 30, HEIGHT / 2),
                new Sprite(PADDLE_WIDTH, PADDLE_HEIGHT, Color.WHITE),
                new Velocity(),
                new BoxCollider(PADDLE_WIDTH, PADDLE_HEIGHT),
                new AIPaddleTag()
        );

        // ボール
        entityManager.spawn(
                new Transform(WIDTH / 2, HEIGHT / 2),
                new Sprite(BALL_SIZE, BALL_SIZE, Color.WHITE),
                new Velocity(INITIAL_BALL_SPEED, INITIAL_BALL_SPEED * 0.5f),
                new BoxCollider(BALL_SIZE, BALL_SIZE),
                new BallTag()
        );

        // 中央線
        for (int i = 0; i < HEIGHT; i += 20) {
            entityManager.spawn(
                    new Transform(WIDTH / 2, i + 10),
                    new Sprite(4, 10, new Color(100, 100, 100))
            );
        }

        // スコアテキスト
        entityManager.spawn(
                new Transform(WIDTH / 2, 50),
                new Text("0  :  0", new Font("SansSerif", Font.BOLD, 40), Color.WHITE),
                new ScoreDisplayTag()
        );
    }

    /**
     * スコアをリセット
     */
    private static void resetBall(Entity ball) {
        Transform transform = ball.getComponent(Transform.class).get();
        Velocity velocity = ball.getComponent(Velocity.class).get();

        transform.setPosition(WIDTH / 2, HEIGHT / 2);

        // ランダムな方向にボールを発射
        float angle = (float) (Math.random() * Math.PI / 3 - Math.PI / 6); // -30度から30度
        float direction = Math.random() < 0.5 ? 1 : -1;
        velocity.set(INITIAL_BALL_SPEED * direction, INITIAL_BALL_SPEED * (float) Math.sin(angle));
    }

    // ========== タグコンポーネント ==========
    private static class PlayerPaddleTag implements Component {
    }

    private static class AIPaddleTag implements Component {
    }

    private static class BallTag implements Component {
    }

    private static class ScoreDisplayTag implements Component {
    }

    // ========== ゲームシステム ==========
    /**
     * プレイヤー操作システム
     */
    private static class PlayerControlSystem extends GameSystem {

        private final InputManager inputManager;

        public PlayerControlSystem(InputManager inputManager) {
            this.inputManager = inputManager;
        }

        @Override
        public void update(float deltaTime) {
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, Velocity.class, PlayerPaddleTag.class)) {
                Velocity velocity = entity.getComponent(Velocity.class).get();
                Transform transform = entity.getComponent(Transform.class).get();

                float vy = 0;

                if (inputManager.isKeyPressed(KeyEvent.VK_UP) || inputManager.isKeyPressed(KeyEvent.VK_W)) {
                    vy = -PADDLE_SPEED;
                }
                if (inputManager.isKeyPressed(KeyEvent.VK_DOWN) || inputManager.isKeyPressed(KeyEvent.VK_S)) {
                    vy = PADDLE_SPEED;
                }

                velocity.set(0, vy);

                // パドルが画面外に出ないように制限
                float halfHeight = PADDLE_HEIGHT / 2f;
                if (transform.y < halfHeight) {
                    transform.y = halfHeight;
                }
                if (transform.y > HEIGHT - halfHeight) {
                    transform.y = HEIGHT - halfHeight;
                }
            }
        }
    }

    /**
     * AI操作システム
     */
    private static class AIControlSystem extends GameSystem {

        private static final float AI_SPEED = 350f;
        private static final float AI_REACTION_ZONE = 30f;

        @Override
        public void update(float deltaTime) {
            // ボールの位置を取得
            Entity ball = null;
            for (Entity entity : entityManager.getEntitiesWith(BallTag.class)) {
                ball = entity;
                break;
            }

            if (ball == null) {
                return;
            }

            Transform ballTransform = ball.getComponent(Transform.class).get();

            // AIパドルを制御
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, Velocity.class, AIPaddleTag.class)) {
                Transform transform = entity.getComponent(Transform.class).get();
                Velocity velocity = entity.getComponent(Velocity.class).get();

                float targetY = ballTransform.y;
                float diff = targetY - transform.y;

                // 反応ゾーン内なら動かない
                if (Math.abs(diff) < AI_REACTION_ZONE) {
                    velocity.set(0, 0);
                } else if (diff > 0) {
                    velocity.set(0, AI_SPEED);
                } else {
                    velocity.set(0, -AI_SPEED);
                }

                // パドルが画面外に出ないように制限
                float halfHeight = PADDLE_HEIGHT / 2f;
                if (transform.y < halfHeight) {
                    transform.y = halfHeight;
                }
                if (transform.y > HEIGHT - halfHeight) {
                    transform.y = HEIGHT - halfHeight;
                }
            }
        }
    }

    /**
     * ボールの壁バウンスシステム
     */
    private static class BallBounceSystem extends GameSystem {

        @Override
        public void update(float deltaTime) {
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, Velocity.class, BallTag.class)) {
                Transform transform = entity.getComponent(Transform.class).get();
                Velocity velocity = entity.getComponent(Velocity.class).get();

                float halfSize = BALL_SIZE / 2f;

                // 上下の壁でバウンス
                if (transform.y - halfSize < 0) {
                    transform.y = halfSize;
                    velocity.vy = Math.abs(velocity.vy);
                }
                if (transform.y + halfSize > HEIGHT) {
                    transform.y = HEIGHT - halfSize;
                    velocity.vy = -Math.abs(velocity.vy);
                }
            }
        }
    }

    /**
     * Pong用の衝突システム
     */
    private static class PongCollisionSystem extends CollisionSystem {

        @Override
        protected void onCollision(Entity a, Entity b) {
            Entity ball = null;
            Entity paddle = null;

            if (a.hasComponent(BallTag.class)) {
                ball = a;
                paddle = b;
            } else if (b.hasComponent(BallTag.class)) {
                ball = b;
                paddle = a;
            }

            // ボールとパドルの衝突
            if (ball != null && paddle != null
                    && (paddle.hasComponent(PlayerPaddleTag.class) || paddle.hasComponent(AIPaddleTag.class))) {

                Velocity ballVelocity = ball.getComponent(Velocity.class).get();
                Transform ballTransform = ball.getComponent(Transform.class).get();
                Transform paddleTransform = paddle.getComponent(Transform.class).get();

                // ボールの速度を反転
                ballVelocity.vx = -ballVelocity.vx;

                // パドルのどの位置に当たったかで角度を変える
                float relativeY = (ballTransform.y - paddleTransform.y) / (PADDLE_HEIGHT / 2f);
                ballVelocity.vy = relativeY * INITIAL_BALL_SPEED * 0.8f;

                // 速度を少し上げる
                float speedMultiplier = 1.05f;
                ballVelocity.vx *= speedMultiplier;
                ballVelocity.vy *= speedMultiplier;

                // 最大速度制限
                float maxSpeed = INITIAL_BALL_SPEED * 2;
                float speed = ballVelocity.getSpeed();
                if (speed > maxSpeed) {
                    ballVelocity.vx = (ballVelocity.vx / speed) * maxSpeed;
                    ballVelocity.vy = (ballVelocity.vy / speed) * maxSpeed;
                }
            }
        }
    }

    /**
     * スコアリングシステム
     */
    private static class ScoreSystem extends GameSystem {

        @Override
        public void update(float deltaTime) {
            for (Entity entity : entityManager.getEntitiesWith(Transform.class, BallTag.class)) {
                Transform transform = entity.getComponent(Transform.class).get();

                // 左側に出た場合（AIの得点）
                if (transform.x < 0) {
                    aiScore++;
                    updateScoreDisplay();
                    resetBall(entity);
                }

                // 右側に出た場合（プレイヤーの得点）
                if (transform.x > WIDTH) {
                    playerScore++;
                    updateScoreDisplay();
                    resetBall(entity);
                }
            }
        }

        private void updateScoreDisplay() {
            for (Entity entity : entityManager.getEntitiesWith(Text.class, ScoreDisplayTag.class)) {
                Text text = entity.getComponent(Text.class).get();
                text.setText(playerScore + "  :  " + aiScore);
            }
        }
    }
}
