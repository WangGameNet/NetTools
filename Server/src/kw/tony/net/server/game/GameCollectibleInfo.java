package kw.tony.net.server.game;

public class GameCollectibleInfo {
    private final int id;
    private float x;
    private float y;

    public GameCollectibleInfo(int id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
