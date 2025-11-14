# Hero Engine - 軽量2Dゲームフレームワーク

SwingベースのECS（Entity Component System）アーキテクチャを採用した軽量な2Dゲームフレームワークライブラリです。

## 特徴

- **ECSアーキテクチャ**: エンティティ、コンポーネント、システムによる柔軟な設計
- **Swingベース**: Java標準ライブラリのみで動作し、追加の依存関係が不要
- **軽量**: シンプルで理解しやすいコードベース
- **拡張性**: 独自のコンポーネントやシステムを簡単に追加可能
- **2D描画**: スプライト、トランスフォーム、衝突判定などの基本機能を搭載

## アーキテクチャ

### ECSの概念

- **Entity（エンティティ）**: ゲームオブジェクトの識別子として機能
- **Component（コンポーネント）**: データのみを保持（位置、速度、スプライトなど）
- **System（システム）**: コンポーネントに基づいてロジックを実行

### 主要なクラス

#### コアクラス
- `GameEngine`: ゲームループとシステム管理
- `EntityManager`: エンティティの生成・削除・管理
- `Entity`: コンポーネントのコンテナ
- `Component`: データ保持用のインターフェース
- `GameSystem`: ロジック実行の基底クラス

#### 標準コンポーネント
- `Transform`: 位置、回転、スケール
- `Sprite`: 描画情報（サイズ、色、zオーダー）
- `ImageSprite`: 画像スプライト（画像ファイルを読み込んで表示）
- `Velocity`: 速度
- `BoxCollider`: 矩形の当たり判定
- `Text`: テキスト表示

#### 標準システム
- `RenderSystem`: Swingで描画
- `MovementSystem`: 速度に基づいて位置を更新
- `CollisionSystem`: 衝突判定

#### 入力
- `InputManager`: キーボードとマウスの入力管理

## 使用方法

### 基本的な使い方

```java
// 1. ゲームエンジンを作成
GameEngine engine = new GameEngine("My Game", 800, 600);

// 2. エンジンを初期化（ウィンドウが作成されます）
engine.init();

// 3. システムを追加
engine.addSystems(
    new RenderSystem(engine.getGamePanel()),
    new MovementSystem(),
    new CollisionSystem()
);

// 4. エンティティを作成
EntityManager entityManager = engine.getEntityManager();
Entity player = entityManager.createEntity();
player.addComponents(
    new Sprite(40, 40, Color.GREEN),
    new Transform(400, 300),
    new Velocity(100, 50)
);

// 5. ゲームを開始
engine.start();
```

### カスタムコンポーネントの作成

```java
public class Health implements Component {
    public int current;
    public int max;
    
    public Health(int max) {
        this.max = max;
        this.current = max;
    }
}
```

### カスタムシステムの作成

```java
public class HealthSystem extends GameSystem {
    @Override
    public void update(float deltaTime) {
        List<Entity> entities = entityManager.getEntitiesWith(Health.class);
        
        for (Entity entity : entities) {
            Health health = entity.getComponent(Health.class).get();
            
            if (health.current <= 0) {
                entityManager.removeEntity(entity);
            }
        }
    }
}
```

### 入力の処理

```java
InputManager inputManager = new InputManager();

// リスナーを登録
engine.getFrame().addKeyListener(inputManager);
engine.getGamePanel().addMouseListener(inputManager.getMouseAdapter());

// カスタムシステムで入力をチェック
public class PlayerControlSystem extends GameSystem {
    private InputManager inputManager;
    
    public PlayerControlSystem(InputManager inputManager) {
        this.inputManager = inputManager;
    }
    
    @Override
    public void update(float deltaTime) {
        if (inputManager.isKeyPressed(KeyEvent.VK_SPACE)) {
            // ジャンプ処理
        }
    }
}
```

## サンプル集

### 1. MinimalSample - 最小限のサンプル（17行）

最もシンプルな例。画面中央に緑の四角を1つ表示します。

```bash
java -cp lib/build/libs/lib.jar heroengine.demo.MinimalSample
```

### 2. HelloWorld - "Hello, World"表示

ピクセルアートで"HELLO!"を表示するサンプル。

```bash
java -cp lib/build/libs/lib.jar heroengine.demo.HelloWorld
```

### 3. SampleGame - フル機能デモ

操作可能なプレイヤー、動く障害物、衝突判定を含む完全なデモ。

```bash
./gradlew :lib:run
```

**操作方法：**
- 矢印キー/WASDでプレイヤー（緑の四角）を操作
- 動く障害物（色付きの四角）が画面内を移動
- 衝突すると色が変わる
- 画面端に壁がある

### 4. ImageSpriteDemo - 画像表示デモ

画像を読み込んで表示し、キーボードで移動できるデモ。

```bash
java -cp lib/build/libs/lib.jar heroengine.demo.ImageSpriteDemo
```

**操作方法：**
- 矢印キーで中央の画像を移動
- ESCキーで終了
- 半透明や回転のサンプルも表示

## ビルド

```bash
./gradlew build
```

## プロジェクト構造

```
lib/src/main/java/org/example/heroengine/
├── ecs/              # ECSコアクラス
│   ├── Component.java
│   ├── Entity.java
│   ├── EntityManager.java
│   └── GameSystem.java
├── components/       # 標準コンポーネント
│   ├── Transform.java
│   ├── Sprite.java
│   ├── ImageSprite.java
│   ├── Text.java
│   ├── Velocity.java
│   └── BoxCollider.java
├── systems/          # 標準システム
│   ├── RenderSystem.java
│   ├── MovementSystem.java
│   └── CollisionSystem.java
├── core/            # コアエンジン
│   └── GameEngine.java
├── input/           # 入力管理
│   └── InputManager.java
└── demo/            # サンプル
    └── SampleGame.java
```

## 拡張例

### パーティクルシステムの追加

```java
public class Particle implements Component {
    public float lifetime;
    public float age;
}

public class ParticleSystem extends GameSystem {
    @Override
    public void update(float deltaTime) {
        List<Entity> particles = entityManager.getEntitiesWith(Particle.class);
        
        for (Entity entity : particles) {
            Particle particle = entity.getComponent(Particle.class).get();
            particle.age += deltaTime;
            
            if (particle.age >= particle.lifetime) {
                entityManager.removeEntity(entity);
            }
        }
    }
}
```

### スプライトシートの読み込み

`ImageSprite`コンポーネントが実装されており、画像ファイルを読み込んで表示できます。

```java
// 画像ファイルから読み込み
Entity imageEntity = entityManager.createEntity();
ImageSprite sprite = new ImageSprite("path/to/image.png");
imageEntity.addComponents(
    new Transform(400, 300),
    sprite
);

// リソースファイルから読み込み
ImageSprite sprite2 = new ImageSprite(
    getClass().getResourceAsStream("/images/player.png")
);

// サイズ変更
sprite.setSize(100, 100);

// 透明度設定
sprite.setAlpha(0.5f); // 50%透明

// 描画順序
sprite.setZOrder(10); // 大きいほど手前に描画
```

## ライセンス

このプロジェクトは自由に使用・改変できます。

## 今後の拡張案

- [x] テクスチャ/画像スプライトのサポート
- [ ] アニメーションシステム
- [ ] サウンド再生機能
- [ ] タイルマップサポート
- [ ] カメラシステム
- [ ] パーティクルエフェクト
- [ ] シーン管理
- [ ] 物理エンジンの統合
