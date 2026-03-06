package kw.tony.net.client.event;

import java.util.List;

public class WorldSnapshotEvent {
    private final long snapshotId;
    private final List<BallSnapshot> balls;

    public WorldSnapshotEvent(long snapshotId, List<BallSnapshot> balls) {
        this.snapshotId = snapshotId;
        this.balls = balls;
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public List<BallSnapshot> getBalls() {
        return balls;
    }
}
