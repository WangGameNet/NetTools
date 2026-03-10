package kw.tony.net.server.event;

public class PlayerScoreState {
    private final int playerId;
    private final int score;

    public PlayerScoreState(int playerId, int score) {
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
