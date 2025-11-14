package heroengine.components;

import heroengine.ecs.Component;
import java.awt.Color;

/**
 * スプライトコンポーネント 2Dグラフィックスの描画情報を保持
 */
public class Sprite implements Component {

    public int width;
    public int height;
    public Color color;
    public boolean visible;
    public int zOrder; // 描画順序（大きいほど手前）

    public Sprite(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.visible = true;
        this.zOrder = 0;
    }

    public Sprite(int width, int height) {
        this(width, height, Color.WHITE);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setZOrder(int zOrder) {
        this.zOrder = zOrder;
    }
}
