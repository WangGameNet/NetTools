package kw.tony.net.server.event;

import java.util.List;

public class InitialWorldState {
    private final List<BallState> balls;

    public InitialWorldState(List<BallState> balls) {
        this.balls = balls;
    }

    public List<BallState> getBalls() {
        return balls;
    }
}
