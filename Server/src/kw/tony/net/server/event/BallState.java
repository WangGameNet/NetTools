package kw.tony.net.server.event;

public class BallState {
    private final int ballId;
    private final float x;
    private final float y;

    public BallState(int ballId, float x, float y) {
        this.ballId = ballId;
        this.x = x;
        this.y = y;
    }

    public int getBallId() {
        return ballId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
