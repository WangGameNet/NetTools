package kw.tony.shared.constant.bean;

public class BallInfo {
    private int ballId;
    private float posx;
    private float posy;
    public BallInfo(){}

    public BallInfo(int ballId, float x ,float y) {
        this.ballId = ballId;
        this.posx = x;
        this.posy = y;
    }

    public int getBallId() {
        return ballId;
    }

    public void setBallId(int ballId) {
        this.ballId = ballId;
    }

    public float getPosx() {
        return posx;
    }

    public void setPosx(float posx) {
        this.posx = posx;
    }

    public float getPosy() {
        return posy;
    }

    public void setPosy(float posy) {
        this.posy = posy;
    }

    @Override
    public String toString() {
        return "BallInfo{" +
                "ballId=" + ballId +
                ", posx=" + posx +
                ", posy=" + posy +
                '}';
    }
}
