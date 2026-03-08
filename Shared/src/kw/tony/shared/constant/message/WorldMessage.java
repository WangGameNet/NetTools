package kw.tony.shared.constant.message;

import kw.tony.shared.constant.bean.BallInfo;

import java.util.ArrayList;

public class WorldMessage extends Message{
    private long snapshotId;
    private ArrayList<BallInfo> positions;

    public WorldMessage(){
        this.positions = new ArrayList<>();
    }

    public long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(long snapshotId) {
        this.snapshotId = snapshotId;
    }

    public void setPositions(ArrayList<BallInfo> positions) {
        this.positions = positions;
    }

    public ArrayList<BallInfo> getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "WorldMessage{" +
                "snapshotId=" + snapshotId +
                ", positions=" + positions +
                '}';
    }
}
