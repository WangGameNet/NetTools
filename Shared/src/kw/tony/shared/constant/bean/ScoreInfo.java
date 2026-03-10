package kw.tony.shared.constant.bean;

public class ScoreInfo {
    private int playerId;
    private int score;

    public ScoreInfo() {
    }

    public ScoreInfo(int playerId, int score) {
        this.playerId = playerId;
        this.score = score;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
