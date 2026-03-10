package kw.tony.shared.constant.message;

import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.bean.CollectibleInfo;
import kw.tony.shared.constant.bean.ScoreInfo;

import java.util.ArrayList;

/**
 * 初始化ball
 */
public class BallInitMessage extends Message {
    private ArrayList<BallInfo> positions;
    private ArrayList<CollectibleInfo> collectibles;
    private ArrayList<ScoreInfo> scores;

    public BallInitMessage(){
        this.positions = new ArrayList<>();
        this.collectibles = new ArrayList<>();
        this.scores = new ArrayList<>();
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
                "positions=" + positions +
                ", collectibles=" + collectibles +
                ", scores=" + scores +
                '}';
    }
}
