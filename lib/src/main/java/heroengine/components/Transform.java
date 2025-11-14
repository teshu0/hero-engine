package heroengine.components;

import heroengine.ecs.Component;

/**
 * トランスフォームコンポーネント エンティティの位置、回転、スケールを保持
 */
public class Transform implements Component {

    public float x;
    public float y;
    public float rotation; // ラジアン
    public float scaleX;
    public float scaleY;

    public Transform() {
        this(0, 0);
    }

    public Transform(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotation = 0;
        this.scaleX = 1.0f;
        this.scaleY = 1.0f;
    }

    public void translate(float dx, float dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void rotate(float angle) {
        this.rotation += angle;
    }

    public void setScale(float scale) {
        this.scaleX = scale;
        this.scaleY = scale;
    }

    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
