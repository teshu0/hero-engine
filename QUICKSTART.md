# Hero Engine クイックスタートガイド

## インストール

このプロジェクトをクローンしてビルドします：

```bash
git clone <repository-url>
cd hero-engine
./gradlew build
```

## サンプルゲームの実行

```bash
./gradlew :lib:run
```

または、直接Javaで実行：

```bash
java -cp lib/build/libs/lib.jar heroengine.demo.SampleGame
```

## サンプルゲームの操作方法

- **矢印キー** または **WASD**: プレイヤー（緑の四角）を移動
- **目的**: 動く障害物を避けながら移動

## 最初のゲームを作る

### ステップ1: GameEngineを作成

```java
import heroengine.core.GameEngine;

public class MyGame {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine("My First Game", 800, 600);
        engine.init();
        
        // ... ゲームの設定
        
        engine.start();
    }
}
```

### ステップ2: システムを追加

```java
import heroengine.systems.*;

// 描画システム
engine.addSystem(new RenderSystem(engine.getGamePanel()));

// 移動システム
engine.addSystem(new MovementSystem());

// 衝突判定システム
engine.addSystem(new CollisionSystem());
```

### ステップ3: エンティティを作成

```java
import heroengine.ecs.*;
import heroengine.components.*;
import java.awt.Color;

EntityManager entityManager = engine.getEntityManager();

// プレイヤーを作成
Entity player = entityManager.createEntity();
player.addComponent(new Transform(400, 300));  // 位置
player.addComponent(new Sprite(40, 40, Color.BLUE));  // 見た目
player.addComponent(new Velocity(0, 0));  // 速度
```

### ステップ4: 入力を処理

```java
import heroengine.input.InputManager;
import java.awt.event.KeyEvent;

InputManager inputManager = new InputManager();

// リスナーを登録（engine.init()の後）
engine.getFrame().addKeyListener(inputManager);

// カスタムシステムで入力をチェック
public class PlayerInputSystem extends GameSystem {
    private InputManager input;
    
    public PlayerInputSystem(InputManager input) {
        this.input = input;
    }
    
    @Override
    public void update(float deltaTime) {
        List<Entity> players = entityManager.getEntitiesWith(
            Transform.class, Velocity.class
        );
        
        for (Entity player : players) {
            Velocity vel = player.getComponent(Velocity.class).get();
            
            vel.vx = 0;
            vel.vy = 0;
            
            if (input.isKeyPressed(KeyEvent.VK_LEFT)) vel.vx = -200;
            if (input.isKeyPressed(KeyEvent.VK_RIGHT)) vel.vx = 200;
            if (input.isKeyPressed(KeyEvent.VK_UP)) vel.vy = -200;
            if (input.isKeyPressed(KeyEvent.VK_DOWN)) vel.vy = 200;
        }
    }
}

engine.addSystem(new PlayerInputSystem(inputManager));
```

## 完全な例

```java
package com.example.mygame;

import heroengine.core.GameEngine;
import heroengine.ecs.*;
import heroengine.components.*;
import heroengine.systems.*;
import heroengine.input.InputManager;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.List;

public class SimpleGame {
    public static void main(String[] args) {
        // エンジン作成
        GameEngine engine = new GameEngine("Simple Game", 800, 600);
        engine.init();
        
        // 入力設定
        InputManager inputManager = new InputManager();
        engine.getFrame().addKeyListener(inputManager);
        
        // システム追加
        engine.addSystem(new RenderSystem(engine.getGamePanel()));
        engine.addSystem(new MovementSystem());
        engine.addSystem(new GameSystem() {
            @Override
            public void update(float deltaTime) {
                inputManager.update();
                
                List<Entity> players = entityManager.getEntitiesWith(
                    Transform.class, Velocity.class
                );
                
                for (Entity player : players) {
                    Velocity vel = player.getComponent(Velocity.class).get();
                    vel.vx = 0;
                    vel.vy = 0;
                    
                    float speed = 300;
                    if (inputManager.isKeyPressed(KeyEvent.VK_A)) vel.vx = -speed;
                    if (inputManager.isKeyPressed(KeyEvent.VK_D)) vel.vx = speed;
                    if (inputManager.isKeyPressed(KeyEvent.VK_W)) vel.vy = -speed;
                    if (inputManager.isKeyPressed(KeyEvent.VK_S)) vel.vy = speed;
                }
            }
        });
        
        // プレイヤー作成
        EntityManager em = engine.getEntityManager();
        Entity player = em.createEntity();
        player.addComponent(new Transform(400, 300));
        player.addComponent(new Sprite(50, 50, Color.GREEN));
        player.addComponent(new Velocity());
        
        // 敵作成
        for (int i = 0; i < 5; i++) {
            Entity enemy = em.createEntity();
            enemy.addComponent(new Transform(
                100 + i * 150, 
                100 + (i % 2) * 400
            ));
            enemy.addComponent(new Sprite(30, 30, Color.RED));
            enemy.addComponent(new Velocity(
                50 + i * 10, 
                30 + i * 5
            ));
        }
        
        // 開始
        engine.start();
    }
}
```

## 次のステップ

1. **カスタムコンポーネントを作成**: ゲーム固有のデータを保持
2. **カスタムシステムを作成**: ゲームロジックを実装
3. **衝突判定を追加**: `CollisionSystem`を拡張
4. **サンプルゲームを確認**: `heroengine.demo.SampleGame`

詳細は`README.md`を参照してください。
