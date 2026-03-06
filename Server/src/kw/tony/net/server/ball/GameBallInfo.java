package kw.tony.net.server.ball;

public class GameBallInfo {
    private int id;
    private float x;
    private float y;
    private float targetX;
    private float targetY;
    private float currentX;
    private float currentY;

    public float getCurrentX() {
        return currentX;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public float getTargetX() {
        return targetX;
    }

    public void setTargetX(float targetX) {
        this.targetX = targetX;
    }

    public float getTargetY() {
        return targetY;
    }

    public void setTargetY(float targetY) {
        this.targetY = targetY;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.currentX = x;
        this.currentY = y;
    }

    public void setTarget(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public String toString() {
        return "GameBallInfo{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public void calBallCurrentPos(float progress) {
        float clampedProgress = Math.max(0f, Math.min(1f, progress));
        currentX = x + (targetX - x) * clampedProgress;
        currentY = y + (targetY - y) * clampedProgress;
    }
}
