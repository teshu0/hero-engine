package heroengine.systems;

import heroengine.components.ImageSprite;
import heroengine.components.Sprite;
import heroengine.components.Text;
import heroengine.components.Transform;
import heroengine.ecs.Entity;
import heroengine.ecs.GameSystem;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
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
        // すべての描画対象エンティティを収集してzOrderでソート
        List<RenderableEntity> renderables = new ArrayList<>();

        // 通常のスプライトを追加
        List<Entity> spriteEntities = entityManager.getEntitiesWith(Transform.class, Sprite.class);
        for (Entity entity : spriteEntities) {
            Sprite sprite = entity.getComponent(Sprite.class).get();
            if (sprite.visible) {
                renderables.add(new RenderableEntity(entity, sprite.zOrder, RenderType.SPRITE));
            }
        }

        // 画像スプライトを追加
        List<Entity> imageSpriteEntities = entityManager.getEntitiesWith(Transform.class, ImageSprite.class);
        for (Entity entity : imageSpriteEntities) {
            ImageSprite imageSprite = entity.getComponent(ImageSprite.class).get();
            if (imageSprite.visible) {
                renderables.add(new RenderableEntity(entity, imageSprite.zOrder, RenderType.IMAGE_SPRITE));
            }
        }

        // zOrderでソート（小さい順=奥から描画）
        renderables.sort(Comparator.comparingInt(r -> r.zOrder));

        // 描画
        for (RenderableEntity renderable : renderables) {
            Transform transform = renderable.entity.getComponent(Transform.class).get();

            // 変換行列を保存
            AffineTransform oldTransform = g2d.getTransform();
            Composite oldComposite = g2d.getComposite();

            // 変換を適用
            AffineTransform newTransform = new AffineTransform();
            newTransform.translate(transform.x, transform.y);
            newTransform.rotate(transform.rotation);
            newTransform.scale(transform.scaleX, transform.scaleY);
            g2d.transform(newTransform);

            if (renderable.type == RenderType.SPRITE) {
                // 通常のスプライトを描画
                Sprite sprite = renderable.entity.getComponent(Sprite.class).get();
                g2d.setColor(sprite.color);
                g2d.fillRect(
                        -sprite.width / 2,
                        -sprite.height / 2,
                        sprite.width,
                        sprite.height
                );
            } else if (renderable.type == RenderType.IMAGE_SPRITE) {
                // 画像スプライトを描画
                ImageSprite imageSprite = renderable.entity.getComponent(ImageSprite.class).get();

                // 透明度を設定
                if (imageSprite.alpha < 1.0f) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, imageSprite.alpha));
                }

                g2d.drawImage(
                        imageSprite.image,
                        -imageSprite.width / 2,
                        -imageSprite.height / 2,
                        imageSprite.width,
                        imageSprite.height,
                        null
                );
            }

            // 変換を元に戻す
            g2d.setComposite(oldComposite);
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

    /**
     * 描画可能なエンティティの情報を保持
     */
    private static class RenderableEntity {

        Entity entity;
        int zOrder;
        RenderType type;

        RenderableEntity(Entity entity, int zOrder, RenderType type) {
            this.entity = entity;
            this.zOrder = zOrder;
            this.type = type;
        }
    }

    /**
     * 描画タイプ
     */
    private enum RenderType {
        SPRITE,
        IMAGE_SPRITE
    }
}
