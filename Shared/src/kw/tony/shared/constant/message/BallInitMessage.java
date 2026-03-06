package kw.tony.shared.constant.message;

import kw.tony.shared.constant.bean.BallInfo;

import java.util.ArrayList;

public class BallInitMessage {
    private ArrayList<BallInfo> positions;

    public BallInitMessage(){
        this.positions = new ArrayList<>();
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
                "positions=" + positions +
                '}';
    }
}
