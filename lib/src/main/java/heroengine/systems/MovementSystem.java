package heroengine.systems;

import heroengine.components.Transform;
import heroengine.components.Velocity;
import heroengine.ecs.Entity;
import heroengine.ecs.GameSystem;

import java.util.List;

/**
 * 移動システム VelocityコンポーネントをもとにTransformを更新
 */
public class MovementSystem extends GameSystem {

    @Override
    public void update(float deltaTime) {
        List<Entity> entities = entityManager.getEntitiesWith(Transform.class, Velocity.class);

        for (Entity entity : entities) {
            Transform transform = entity.getComponent(Transform.class).get();
            Velocity velocity = entity.getComponent(Velocity.class).get();

            transform.x += velocity.vx * deltaTime;
            transform.y += velocity.vy * deltaTime;
        }
    }
}
