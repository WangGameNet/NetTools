package kw.tony.net.client.event;

public class CollectibleSnapshot {
    private final int collectibleId;
    private final float x;
    private final float y;

    public CollectibleSnapshot(int collectibleId, float x, float y) {
        this.collectibleId = collectibleId;
        this.x = x;
        this.y = y;
    }

    public int getCollectibleId() {
        return collectibleId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
