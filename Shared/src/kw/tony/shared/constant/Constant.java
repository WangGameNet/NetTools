package kw.tony.shared.constant;

public interface Constant {
    int TCP_PORT = 1234;
    int UDP_PORT = 1235;
    int DISCOVERY_PORT = 1236;
    float WORLD_WIDTH = 1080f;
    float WORLD_HEIGHT = 1920f;
    float HUD_HEIGHT = 180f;
    float PLAYER_SIZE = 96f;
    float COLLECTIBLE_SIZE = 40f;
    float COLLECTIBLE_PICKUP_DISTANCE = 72f;
    int COLLECTIBLE_COUNT = 8;
    String SERVER_HOST = "localhost";
    String DISCOVERY_REQUEST_TOKEN = "NETTOOLS_DISCOVER_REQUEST";
    String DISCOVERY_RESPONSE_TOKEN = "NETTOOLS_DISCOVER_RESPONSE";
    int DISCOVERY_TIMEOUT_MILLIS = 1200;
    float WORLD_STEP_SECONDS = 1f / 60f;
    float SNAPSHOT_INTERVAL_SECONDS = 1f / 60f;
    float BALL_MOVE_INTERVAL_SECONDS = 1.5f;
    float MAX_FRAME_DELTA_SECONDS = 0.25f;
}
