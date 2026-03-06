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


    @Override
    public String toString() {
        return "GameBallInfo{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public void calBallCurrentPos(float time) {
        currentX = x + (targetX - x) * time / 1.5f;
        currentY = y + (targetY - y) * time / 1.5f;
    }
}
