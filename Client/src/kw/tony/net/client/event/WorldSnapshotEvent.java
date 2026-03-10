package kw.tony.net.client.event;

import java.util.List;

public class WorldSnapshotEvent {
    private final long snapshotId;
    private final List<BallSnapshot> balls;
    private final List<CollectibleSnapshot> collectibles;
    private final List<PlayerScoreSnapshot> scores;

    public WorldSnapshotEvent(long snapshotId, List<BallSnapshot> balls, List<CollectibleSnapshot> collectibles, List<PlayerScoreSnapshot> scores) {
        this.snapshotId = snapshotId;
        this.balls = balls;
        this.collectibles = collectibles;
        this.scores = scores;
    }

    public long getSnapshotId() {
        return snapshotId;
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
