package kw.tony.net.server.event;

import java.util.List;

public class WorldSnapshot {
    private final long snapshotId;
    private final List<BallState> balls;

    public WorldSnapshot(long snapshotId, List<BallState> balls) {
        this.snapshotId = snapshotId;
        this.balls = balls;
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public List<BallState> getBalls() {
        return balls;
    }
}
