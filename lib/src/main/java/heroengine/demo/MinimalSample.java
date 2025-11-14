package heroengine.demo;

import heroengine.components.Sprite;
import heroengine.components.Transform;
import heroengine.core.GameEngine;
import heroengine.ecs.Entity;
import heroengine.systems.RenderSystem;

import java.awt.*;

/**
 * 最小限のHero Engineサンプル 単純に矩形を1つ表示するだけ
 */
public class MinimalSample {

    public static void main(String[] args) {
        // 1. エンジンを作成
        GameEngine engine = new GameEngine("Minimal Sample", 800, 600);

        // 2. 初期化
        engine.init();

        // 3. 描画システムを追加
        engine.addSystems(new RenderSystem(engine.getGamePanel()));

        // 4. エンティティを1つ作成
        engine.getEntityManager().spawn(
                new Transform(400, 300), // 画面中央
                new Sprite(100, 100, Color.GREEN) // 緑の四角
        );

        // 5. 開始
        engine.start();
    }
}
