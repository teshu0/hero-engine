package heroengine.systems;

import heroengine.components.BoxCollider;
import heroengine.components.Transform;
import heroengine.ecs.Entity;
import heroengine.ecs.GameSystem;

import java.util.List;

/**
 * コリジョンシステム 矩形の衝突判定を実行
 */
public class CollisionSystem extends GameSystem {

    @Override
    public void update(float deltaTime) {
        List<Entity> entities = entityManager.getEntitiesWith(Transform.class, BoxCollider.class);

        // 総当たりで衝突判定
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityA = entities.get(i);
                Entity entityB = entities.get(j);

                if (checkCollision(entityA, entityB)) {
                    onCollision(entityA, entityB);
                }
            }
        }
    }

    /**
     * 2つのエンティティの衝突判定
     */
    private boolean checkCollision(Entity a, Entity b) {
        Transform transA = a.getComponent(Transform.class).get();
        Transform transB = b.getComponent(Transform.class).get();
        BoxCollider colA = a.getComponent(BoxCollider.class).get();
        BoxCollider colB = b.getComponent(BoxCollider.class).get();

        float leftA = transA.x + colA.offsetX - colA.width / 2;
        float rightA = transA.x + colA.offsetX + colA.width / 2;
        float topA = transA.y + colA.offsetY - colA.height / 2;
        float bottomA = transA.y + colA.offsetY + colA.height / 2;

        float leftB = transB.x + colB.offsetX - colB.width / 2;
        float rightB = transB.x + colB.offsetX + colB.width / 2;
        float topB = transB.y + colB.offsetY - colB.height / 2;
        float bottomB = transB.y + colB.offsetY + colB.height / 2;

        return !(rightA < leftB || rightB < leftA || bottomA < topB || bottomB < topA);
    }

    /**
     * 衝突時の処理（オーバーライド可能）
     */
    protected void onCollision(Entity a, Entity b) {
        // 派生クラスでオーバーライドして使用
    }
}
