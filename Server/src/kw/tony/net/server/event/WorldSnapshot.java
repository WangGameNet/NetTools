package kw.tony.net.server.event;

import java.util.List;

public class WorldSnapshot {
    private final long snapshotId;
    private final List<BallState> balls;
    private final List<CollectibleState> collectibles;
    private final List<PlayerScoreState> scores;

    public WorldSnapshot(long snapshotId, List<BallState> balls, List<CollectibleState> collectibles, List<PlayerScoreState> scores) {
        this.snapshotId = snapshotId;
        this.balls = balls;
        this.collectibles = collectibles;
        this.scores = scores;
    }

    public long getSnapshotId() {
        return snapshotId;
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
