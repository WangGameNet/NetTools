package kw.tony.net.server.event;

public class CollectibleState {
    private final int collectibleId;
    private final float x;
    private final float y;

    public CollectibleState(int collectibleId, float x, float y) {
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
