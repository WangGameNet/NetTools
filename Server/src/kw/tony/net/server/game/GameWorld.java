package kw.tony.net.server.game;

import com.badlogic.gdx.utils.Array;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.net.server.event.BallState;
import kw.tony.net.server.event.ClientConnectedEvent;
import kw.tony.net.server.event.ClientDisconnectedEvent;
import kw.tony.shared.constant.Constant;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class GameWorld {
    private static final float WORLD_PADDING = 40f;

    private final Array<GameBallInfo> ballInfos;
    private final Array<GameCollectibleInfo> collectibleInfos;
    private final Map<Integer, Integer> playerScores;
    private final Random random;

    public GameWorld(){
        ballInfos = new Array<GameBallInfo>();
        collectibleInfos = new Array<GameCollectibleInfo>();
        playerScores = new LinkedHashMap<Integer, Integer>();
        random = new Random();
    }

    /**
     * init  ball
     */
    public void startGame(){
        collectibleInfos.clear();
        for (int i = 0; i < Constant.COLLECTIBLE_COUNT; i++) {
            collectibleInfos.add(new GameCollectibleInfo(i, randomX(Constant.COLLECTIBLE_SIZE), randomY(Constant.COLLECTIBLE_SIZE)));
        }
    }

    public Array<GameBallInfo> getBallInfos() {
        return ballInfos;
    }

    public Array<GameCollectibleInfo> getCollectibleInfos() {
        return collectibleInfos;
    }

    public Map<Integer, Integer> getPlayerScores() {
        return playerScores;
    }

    public void update(float delta){
    }

    public void createBall(ClientConnectedEvent clientConnectedEvent) {
        GameBallInfo gameBallInfo = new GameBallInfo();
        gameBallInfo.setId(clientConnectedEvent.getClientId());
        float startX = randomX(Constant.PLAYER_SIZE);
        float startY = randomY(Constant.PLAYER_SIZE);
        gameBallInfo.setPosition(startX, startY);
        gameBallInfo.setTarget(startX, startY);
        ballInfos.add(gameBallInfo);
        playerScores.put(clientConnectedEvent.getClientId(), 0);
    }

    public void removeBall(ClientDisconnectedEvent clientDisconnectedEvent) {
        GameBallInfo info = null;
        for (GameBallInfo ballInfo : ballInfos) {
            if (ballInfo.getId() == clientDisconnectedEvent.getClientId()) {
                info = ballInfo;
                break;
            }
        }
        if (info!=null) {
            ballInfos.removeValue(info, false);
        }
        playerScores.remove(clientDisconnectedEvent.getClientId());
    }

    public void updateBallPos(BallState event) {
        for (GameBallInfo ballInfo : ballInfos) {
            if (ballInfo.getId() == event.getBallId()) {
                float clampedX = clampX(event.getX(), Constant.PLAYER_SIZE);
                float clampedY = clampY(event.getY(), Constant.PLAYER_SIZE);
                ballInfo.setCurrentX(clampedX);
                ballInfo.setCurrentY(clampedY);
                checkCollectibleCollision(ballInfo);
                return;
            }
        }
    }

    private void checkCollectibleCollision(GameBallInfo ballInfo) {
        float pickupDistance = Constant.COLLECTIBLE_PICKUP_DISTANCE;
        float pickupDistanceSquared = pickupDistance * pickupDistance;

        for (GameCollectibleInfo collectibleInfo : collectibleInfos) {
            float deltaX = ballInfo.getCurrentX() - collectibleInfo.getX();
            float deltaY = ballInfo.getCurrentY() - collectibleInfo.getY();
            if (deltaX * deltaX + deltaY * deltaY <= pickupDistanceSquared) {
                Integer currentScore = playerScores.get(ballInfo.getId());
                playerScores.put(ballInfo.getId(), currentScore == null ? 1 : currentScore + 1);
                collectibleInfo.setX(randomX(Constant.COLLECTIBLE_SIZE));
                collectibleInfo.setY(randomY(Constant.COLLECTIBLE_SIZE));
            }
        }
    }

    private float randomX(float size) {
        return WORLD_PADDING + random.nextFloat() * Math.max(1f, Constant.WORLD_WIDTH - WORLD_PADDING * 2f - size);
    }

    private float randomY(float size) {
        float usableHeight = Constant.WORLD_HEIGHT - Constant.HUD_HEIGHT - WORLD_PADDING * 2f - size;
        return WORLD_PADDING + random.nextFloat() * Math.max(1f, usableHeight);
    }

    private float clampX(float x, float size) {
        return Math.max(0f, Math.min(x, Constant.WORLD_WIDTH - size));
    }

    private float clampY(float y, float size) {
        return Math.max(0f, Math.min(y, Constant.WORLD_HEIGHT - Constant.HUD_HEIGHT - size));
    }
}
