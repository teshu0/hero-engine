package heroengine.components;

import heroengine.ecs.Component;

/**
 * ベロシティコンポーネント エンティティの速度を保持
 */
public class Velocity implements Component {

    public float vx;
    public float vy;

    public Velocity() {
        this(0, 0);
    }

    public Velocity(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void set(float vx, float vy) {
        this.vx = vx;
        this.vy = vy;
    }

    public void add(float dvx, float dvy) {
        this.vx += dvx;
        this.vy += dvy;
    }

    public float getSpeed() {
        return (float) Math.sqrt(vx * vx + vy * vy);
    }
}
