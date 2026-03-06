package kw.tony.shared.constant.message;

import com.badlogic.gdx.math.Vector2;
import kw.tony.shared.constant.bean.BallInfo;

import java.util.ArrayList;

public class WorldMessage {
    private ArrayList<BallInfo> positions;

    public WorldMessage(){
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
