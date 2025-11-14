package heroengine.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * エンティティクラス 一意のIDを持ち、複数のコンポーネントを保持する
 */
public class Entity {

    private static long nextId = 0;

    private final long id;
    private final Map<Class<? extends Component>, Component> components;
    private boolean active;

    public Entity() {
        this.id = nextId++;
        this.components = new HashMap<>();
        this.active = true;
    }

    public long getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * コンポーネントを追加（単数・複数両対応）
     */
    @SafeVarargs
    public final <T extends Component> Entity addComponents(T... components) {
        for (T component : components) {
            this.components.put(component.getClass(), component);
        }
        return this;
    }

    /**
     * コンポーネントを取得
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> componentClass) {
        return Optional.ofNullable((T) components.get(componentClass));
    }

    /**
     * コンポーネントを持っているか確認
     */
    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return components.containsKey(componentClass);
    }

    /**
     * コンポーネントを削除
     */
    public <T extends Component> void removeComponent(Class<T> componentClass) {
        components.remove(componentClass);
    }

    /**
     * すべてのコンポーネントを削除
     */
    public void clearComponents() {
        components.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity entity = (Entity) o;
        return id == entity.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
