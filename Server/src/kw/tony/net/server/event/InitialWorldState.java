package kw.tony.net.server.event;

import java.util.List;

public class InitialWorldState {
    private final List<BallState> balls;
    private final List<CollectibleState> collectibles;
    private final List<PlayerScoreState> scores;

    public InitialWorldState(List<BallState> balls, List<CollectibleState> collectibles, List<PlayerScoreState> scores) {
        this.balls = balls;
        this.collectibles = collectibles;
        this.scores = scores;
    }

    public List<BallState> getBalls() {
        return balls;
    }

    public List<CollectibleState> getCollectibles() {
        return collectibles;
    }

    public List<PlayerScoreState> getScores() {
        return scores;
    }
}
