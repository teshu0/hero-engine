package heroengine.systems;

import heroengine.components.Sprite;
import heroengine.components.Text;
import heroengine.components.Transform;
import heroengine.ecs.Entity;
import heroengine.ecs.GameSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.List;

/**
 * レンダリングシステム Swingを使用してエンティティを描画
 */
public class RenderSystem extends GameSystem {

    private JPanel renderPanel;

    public RenderSystem(JPanel renderPanel) {
        this.renderPanel = renderPanel;
    }

    @Override
    public void update(float deltaTime) {
        renderPanel.repaint();
    }

    /**
     * 描画処理（JPanel#paintComponent から呼ばれる）
     */
    public void render(Graphics2D g2d) {
        // スプライトの描画
        List<Entity> spriteEntities = entityManager.getEntitiesWith(Transform.class, Sprite.class);

        // zOrderでソート（小さい順=奥から描画）
        spriteEntities.sort(Comparator.comparingInt(e
                -> e.getComponent(Sprite.class).get().zOrder
        ));

        for (Entity entity : spriteEntities) {
            Transform transform = entity.getComponent(Transform.class).get();
            Sprite sprite = entity.getComponent(Sprite.class).get();

            if (!sprite.visible) {
                continue;
            }

            // 変換行列を保存
            AffineTransform oldTransform = g2d.getTransform();

            // 変換を適用
            AffineTransform newTransform = new AffineTransform();
            newTransform.translate(transform.x, transform.y);
            newTransform.rotate(transform.rotation);
            newTransform.scale(transform.scaleX, transform.scaleY);
            g2d.transform(newTransform);

            // 描画（中心が原点）
            g2d.setColor(sprite.color);
            g2d.fillRect(
                    -sprite.width / 2,
                    -sprite.height / 2,
                    sprite.width,
                    sprite.height
            );

            // 変換を元に戻す
            g2d.setTransform(oldTransform);
        }

        // テキストの描画
        List<Entity> textEntities = entityManager.getEntitiesWith(Transform.class, Text.class);

        for (Entity entity : textEntities) {
            Transform transform = entity.getComponent(Transform.class).get();
            Text text = entity.getComponent(Text.class).get();

            if (!text.visible) {
                continue;
            }

            // フォントと色を設定
            g2d.setFont(text.font);
            g2d.setColor(text.color);

            // テキストのサイズを取得
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text.text);
            int textHeight = fm.getHeight();

            // 中心基準で描画
            int x = (int) (transform.x - textWidth / 2);
            int y = (int) (transform.y + textHeight / 2 - fm.getDescent());

            g2d.drawString(text.text, x, y);
        }
    }
}
