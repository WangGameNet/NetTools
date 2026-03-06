package kw.tony.net.client.event;

import java.util.List;

public class InitialWorldStateEvent {
    private final List<BallSnapshot> balls;

    public InitialWorldStateEvent(List<BallSnapshot> balls) {
        this.balls = balls;
    }

    public List<BallSnapshot> getBalls() {
        return balls;
    }
}
