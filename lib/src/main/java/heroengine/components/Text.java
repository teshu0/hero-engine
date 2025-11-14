package heroengine.components;

import heroengine.ecs.Component;

import java.awt.*;

/**
 * テキストコンポーネント 文字列を描画するための情報を保持
 */
public class Text implements Component {

    public String text;
    public Font font;
    public Color color;
    public boolean visible;

    public Text(String text) {
        this(text, new Font("SansSerif", Font.BOLD, 24), Color.WHITE);
    }

    public Text(String text, Font font, Color color) {
        this.text = text;
        this.font = font;
        this.color = color;
        this.visible = true;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
