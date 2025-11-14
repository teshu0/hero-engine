package heroengine.ecs;

/**
 * システムの基底クラス エンティティとコンポーネントに対してロジックを実行する
 */
public abstract class GameSystem {

    protected EntityManager entityManager;
    private boolean enabled = true;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * システムの初期化
     */
    public void init() {
        // オーバーライド可能
    }

    /**
     * システムの更新（毎フレーム呼ばれる）
     *
     * @param deltaTime 前フレームからの経過時間（秒）
     */
    public abstract void update(float deltaTime);

    /**
     * システムの終了処理
     */
    public void shutdown() {
        // オーバーライド可能
    }
}
