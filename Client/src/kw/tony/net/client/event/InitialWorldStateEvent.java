package kw.tony.net.client.event;

import java.util.List;

public class InitialWorldStateEvent {
    private final List<BallSnapshot> balls;
    private final List<CollectibleSnapshot> collectibles;
    private final List<PlayerScoreSnapshot> scores;

    public InitialWorldStateEvent(List<BallSnapshot> balls, List<CollectibleSnapshot> collectibles, List<PlayerScoreSnapshot> scores) {
        this.balls = balls;
        this.collectibles = collectibles;
        this.scores = scores;
    }

    public List<BallSnapshot> getBalls() {
        return balls;
    }

    public List<CollectibleSnapshot> getCollectibles() {
        return collectibles;
    }

    public List<PlayerScoreSnapshot> getScores() {
        return scores;
    }
}
