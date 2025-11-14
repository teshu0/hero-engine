package heroengine.ecs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * エンティティマネージャー すべてのエンティティを管理し、コンポーネントでのフィルタリングを提供
 */
public class EntityManager {

    private final List<Entity> entities;
    private final List<Entity> entitiesToAdd;
    private final List<Entity> entitiesToRemove;

    public EntityManager() {
        this.entities = new ArrayList<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }

    /**
     * 新しいエンティティを作成
     */
    public Entity createEntity() {
        Entity entity = new Entity();
        entitiesToAdd.add(entity);
        return entity;
    }

    /**
     * エンティティを削除
     */
    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    /**
     * すべてのエンティティを削除
     */
    public void clear() {
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
    }

    /**
     * 追加・削除待ちのエンティティを処理 フレームの最初か最後に呼ぶ
     */
    public void refresh() {
        // 削除処理
        for (Entity entity : entitiesToRemove) {
            entity.clearComponents();
            entities.remove(entity);
        }
        entitiesToRemove.clear();

        // 追加処理
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();
    }

    /**
     * すべてのアクティブなエンティティを取得
     */
    public List<Entity> getEntities() {
        return entities.stream()
                .filter(Entity::isActive)
                .collect(Collectors.toList());
    }

    /**
     * 指定したコンポーネントを持つエンティティを取得
     */
    @SafeVarargs
    public final List<Entity> getEntitiesWith(Class<? extends Component>... componentClasses) {
        return entities.stream()
                .filter(Entity::isActive)
                .filter(entity -> {
                    for (Class<? extends Component> componentClass : componentClasses) {
                        if (!entity.hasComponent(componentClass)) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * エンティティの数を取得
     */
    public int getEntityCount() {
        return entities.size();
    }
}
