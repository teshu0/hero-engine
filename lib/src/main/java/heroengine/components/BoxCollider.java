package heroengine.components;

import heroengine.ecs.Component;

/**
 * コライダーコンポーネント 矩形の当たり判定を保持
 */
public class BoxCollider implements Component {

    public float width;
    public float height;
    public float offsetX;
    public float offsetY;
    public boolean isTrigger; // トリガーの場合は物理衝突しない

    public BoxCollider(float width, float height) {
        this(width, height, 0, 0);
    }

    public BoxCollider(float width, float height, float offsetX, float offsetY) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.isTrigger = false;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
}
