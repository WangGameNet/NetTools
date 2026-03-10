package kw.tony.net.client.event;

public class PlayerScoreSnapshot {
    private final int playerId;
    private final int score;

    public PlayerScoreSnapshot(int playerId, int score) {
        this.playerId = playerId;
        this.score = score;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getScore() {
        return score;
    }
}
