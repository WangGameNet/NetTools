package kw.tony.shared.constant.message;

import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.bean.CollectibleInfo;
import kw.tony.shared.constant.bean.ScoreInfo;

import java.util.ArrayList;

/**
 * 世界消息
 */
public class WorldMessage {
    private long snapshotId;
    private ArrayList<BallInfo> positions;
    private ArrayList<CollectibleInfo> collectibles;
    private ArrayList<ScoreInfo> scores;

    public WorldMessage(){
        this.positions = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.scores = new ArrayList<>();
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }

    public void setPositions(ArrayList<BallInfo> positions) {
        this.positions = positions;
    }

    public ArrayList<BallInfo> getPositions() {
        return positions;
    }

    public ArrayList<CollectibleInfo> getCollectibles() {
        return collectibles;
    }

    public void setCollectibles(ArrayList<CollectibleInfo> collectibles) {
        this.collectibles = collectibles;
    }

    public ArrayList<ScoreInfo> getScores() {
        return scores;
    }

    public void setScores(ArrayList<ScoreInfo> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "WorldMessage{" +
                "snapshotId=" + snapshotId +
                ", positions=" + positions +
                ", collectibles=" + collectibles +
                ", scores=" + scores +
                '}';
    }
}
