package kw.tony.shared.constant;

public interface Constant {
    int TCP_PORT = 1234;
    int UDP_PORT = 1235;
    String SERVER_HOST = "localhost";

    float WORLD_STEP_SECONDS = 1f / 60f;
    float SNAPSHOT_INTERVAL_SECONDS = 1f / 20f;
    float BALL_MOVE_INTERVAL_SECONDS = 1.5f;
    float MAX_FRAME_DELTA_SECONDS = 0.25f;
}
