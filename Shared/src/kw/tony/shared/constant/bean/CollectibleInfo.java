package kw.tony.shared.constant.bean;

public class CollectibleInfo {
    private int collectibleId;
    private float posx;
    private float posy;

    public CollectibleInfo() {
    }

    public CollectibleInfo(int collectibleId, float posx, float posy) {
        this.collectibleId = collectibleId;
        this.posx = posx;
        this.posy = posy;
    }

    public int getCollectibleId() {
        return collectibleId;
    }

    public void setCollectibleId(int collectibleId) {
        this.collectibleId = collectibleId;
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
}
