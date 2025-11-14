package heroengine.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * 入力マネージャー キーボードとマウスの入力を管理
 */
public class InputManager extends KeyAdapter {

    private final Set<Integer> pressedKeys;
    private final Set<Integer> justPressedKeys;
    private final Set<Integer> justReleasedKeys;

    private int mouseX;
    private int mouseY;
    private boolean mousePressed;
    private boolean mouseJustPressed;
    private boolean mouseJustReleased;

    public InputManager() {
        this.pressedKeys = new HashSet<>();
        this.justPressedKeys = new HashSet<>();
        this.justReleasedKeys = new HashSet<>();
    }

    /**
     * フレームの最後に呼び出して、justPressed/justReleasedをクリア
     */
    public void update() {
        justPressedKeys.clear();
        justReleasedKeys.clear();
        mouseJustPressed = false;
        mouseJustReleased = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!pressedKeys.contains(keyCode)) {
            justPressedKeys.add(keyCode);
        }
        pressedKeys.add(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        pressedKeys.remove(keyCode);
        justReleasedKeys.add(keyCode);
    }

    /**
     * キーが押されているか
     */
    public boolean isKeyPressed(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * キーがこのフレームで押されたか
     */
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    /**
     * キーがこのフレームで離されたか
     */
    public boolean isKeyJustReleased(int keyCode) {
        return justReleasedKeys.contains(keyCode);
    }

    /**
     * マウスイベントリスナーを取得
     */
    public MouseAdapter getMouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePressed = true;
                mouseJustPressed = true;
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePressed = false;
                mouseJustReleased = true;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMouseJustPressed() {
        return mouseJustPressed;
    }

    public boolean isMouseJustReleased() {
        return mouseJustReleased;
    }
}
