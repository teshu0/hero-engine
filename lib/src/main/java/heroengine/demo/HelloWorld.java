package heroengine.demo;

import heroengine.components.Text;
import heroengine.components.Transform;
import heroengine.core.GameEngine;
import heroengine.ecs.Entity;
import heroengine.systems.RenderSystem;

import java.awt.*;

/**
 * 最もシンプルなHero Engineのサンプル 画面中央に"Hello, World!"を表示
 */
public class HelloWorld {

    public static void main(String[] args) {
        // ゲームエンジンを作成
        GameEngine engine = new GameEngine("Hello, World - Hero Engine", 800, 600);

        // エンジンを初期化
        engine.init();

        // 描画システムを追加
        engine.addSystems(new RenderSystem(engine.getGamePanel()));

        // "Hello, World!" テキストを作成
        Entity helloText = engine.getEntityManager().createEntity();
        helloText.addComponents(
                new Transform(400, 300),
                new Text(
                        "Hello, World!",
                        new Font("SansSerif", Font.BOLD, 48),
                        Color.GREEN
                )
        );

        // ゲームを開始
        engine.start();
    }
}
