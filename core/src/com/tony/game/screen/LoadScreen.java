package com.tony.game.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kw.gdx.BaseGame;
import com.kw.gdx.asset.Asset;
import com.kw.gdx.screen.BaseScreen;
import kw.tony.net.client.NetworkEventSubscriber;
import kw.tony.net.client.NetworkService;
import kw.tony.net.client.NetworkServiceProvider;
import kw.tony.net.client.event.*;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LoadScreen extends BaseScreen implements NetworkEventSubscriber {
    private static final float LOCAL_SNAPSHOT_CORRECTION_DISTANCE = 24f;

    private final Map<Integer, BallRenderState> ballStates;
    private final Map<Integer, Image> collectibleActors;
    private NetworkService networkService;
    private long lastSnapshotId = -1L;
    private ClickListener dragListener;
    private BitmapFont scoreFont;
    private Label scoreLabel;

    public LoadScreen(BaseGame game) {
        super(game);
        this.ballStates = new HashMap<Integer, BallRenderState>();
        this.collectibleActors = new HashMap<Integer, Image>();
    }

    @Override
    public void initView() {
        super.initView();
        networkService = resolveNetworkService();
        scoreFont = Asset.getAsset().loadBitFont("Manrope-ExtraBold_40_1.fnt");
        scoreFont.getData().setScale(1.3f);

        scoreLabel = new Label("Scores", new Label.LabelStyle(scoreFont, Color.WHITE));
        Table hudTable = new Table();
        hudTable.setFillParent(true);
        hudTable.top().left();
        hudTable.pad(24f, 24f, 0f, 0f);
        hudTable.add(scoreLabel).left();
        addActor(hudTable);

        dragListener = new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                updateLocalBallPosition(x, y, true);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                updateLocalBallPosition(x, y, false);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateLocalBallPosition(x, y, true);
            }
        };
        stage.addListener(dragListener);
    }

    @Override
    public void show() {
        super.show();
        networkService = resolveNetworkService();
        networkService.subscribe(this);
    }

    @Override
    public void hide() {
        if (networkService != null) {
            networkService.unsubscribe(this);
        }
        super.hide();
    }

    @Override
    public void render(float delta) {
        updateBallInterpolation(delta);
        super.render(delta);
    }

    private NetworkService resolveNetworkService() {
        if (game instanceof NetworkServiceProvider) {
            return ((NetworkServiceProvider) game).getNetworkService();
        }
        throw new IllegalStateException("Game does not provide a NetworkService");
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {
        resetBallStates();
        resetCollectibles();
        updateScoreLabel(Collections.<PlayerScoreSnapshot>emptyList());
        lastSnapshotId = -1L;
        setScreen(ConnectScreen.class);
    }

    @Override
    public void onReconnecting() {
    }

    @Override
    public void onInitialWorldState(InitialWorldStateEvent initialWorldStateEvent) {
        resetBallStates();
        syncCollectibles(initialWorldStateEvent.getCollectibles());
        updateScoreLabel(initialWorldStateEvent.getScores());
        lastSnapshotId = -1L;
        for (BallSnapshot ballSnapshot : initialWorldStateEvent.getBalls()) {
            BallRenderState ballRenderState = getOrCreateBallState(ballSnapshot.getBallId(), ballSnapshot.getX(), ballSnapshot.getY());
            ballRenderState.snapTo(ballSnapshot.getX(), ballSnapshot.getY());
        }
    }

    @Override
    public void onWorldSnapshot(WorldSnapshotEvent worldSnapshotEvent) {
        if (worldSnapshotEvent.getSnapshotId() <= lastSnapshotId) {
            return;
        }
        lastSnapshotId = worldSnapshotEvent.getSnapshotId();
        syncCollectibles(worldSnapshotEvent.getCollectibles());
        updateScoreLabel(worldSnapshotEvent.getScores());

        for (BallSnapshot ballSnapshot : worldSnapshotEvent.getBalls()) {
            BallRenderState ballRenderState = getOrCreateBallState(ballSnapshot.getBallId(), ballSnapshot.getX(), ballSnapshot.getY());
            if (ballSnapshot.getBallId() == networkService.getClientId()) {
                if (ballRenderState.isFarFrom(ballSnapshot.getX(), ballSnapshot.getY(), LOCAL_SNAPSHOT_CORRECTION_DISTANCE)) {
                    ballRenderState.snapTo(ballSnapshot.getX(), ballSnapshot.getY());
                }
                continue;
            }
            ballRenderState.beginInterpolation(ballSnapshot.getX(), ballSnapshot.getY(), Constant.SNAPSHOT_INTERVAL_SECONDS);
        }
    }

    @Override
    public void onTestMessage(TestMessageEvent testMessageEvent) {
        // Reserved for future reliable UI events.
    }

    @Override
    public void onRemoveMessage(RemoveMessageEvent removeMessageEvent) {
        BallRenderState ballRenderState = ballStates.get(removeMessageEvent.getClientId());
        if (ballRenderState != null) {
            ballRenderState.image.remove();
            ballStates.remove(removeMessageEvent.getClientId());
        }
    }

    private void updateBallInterpolation(float delta) {
        for (BallRenderState ballRenderState : ballStates.values()) {
            ballRenderState.update(delta);
        }
    }

    private BallRenderState getOrCreateBallState(int ballId, float x, float y) {
        BallRenderState ballRenderState = ballStates.get(ballId);
        if (ballRenderState != null) {
            return ballRenderState;
        }

        Image image = new Image(Asset.getAsset().getTexture("white.png"));
        image.setSize(Constant.PLAYER_SIZE, Constant.PLAYER_SIZE);
        addActor(image);
        ballRenderState = new BallRenderState(image, x, y);
        ballStates.put(ballId, ballRenderState);
        if (ballId == networkService.getClientId()) {
            image.setColor(Color.RED);
        } else {
            image.setColor(Color.WHITE);
        }
        return ballRenderState;
    }

    private void resetBallStates() {
        for (BallRenderState ballRenderState : ballStates.values()) {
            ballRenderState.image.remove();
        }
        ballStates.clear();
    }

    private void syncCollectibles(List<CollectibleSnapshot> collectibles) {
        Set<Integer> remainingCollectibleIds = new HashSet<Integer>(collectibleActors.keySet());
        for (CollectibleSnapshot collectibleSnapshot : collectibles) {
            Image collectibleActor = collectibleActors.get(collectibleSnapshot.getCollectibleId());
            if (collectibleActor == null) {
                collectibleActor = new Image(Asset.getAsset().getTexture("white.png"));
                collectibleActor.setSize(Constant.COLLECTIBLE_SIZE, Constant.COLLECTIBLE_SIZE);
                collectibleActor.setColor(0.25f, 0.9f, 0.35f, 1f);
                addActor(collectibleActor);
                collectibleActors.put(collectibleSnapshot.getCollectibleId(), collectibleActor);
            }
            collectibleActor.setPosition(collectibleSnapshot.getX(), collectibleSnapshot.getY());
            remainingCollectibleIds.remove(collectibleSnapshot.getCollectibleId());
        }

        for (Integer collectibleId : remainingCollectibleIds) {
            Image collectibleActor = collectibleActors.remove(collectibleId);
            if (collectibleActor != null) {
                collectibleActor.remove();
            }
        }
    }

    private void resetCollectibles() {
        for (Image collectibleActor : collectibleActors.values()) {
            collectibleActor.remove();
        }
        collectibleActors.clear();
    }

    private void updateScoreLabel(List<PlayerScoreSnapshot> scores) {
        if (scoreLabel == null) {
            return;
        }

        if (scores.isEmpty()) {
            scoreLabel.setText("Scores");
            return;
        }

        ArrayList<PlayerScoreSnapshot> sortedScores = new ArrayList<PlayerScoreSnapshot>(scores);
        final int localClientId = networkService == null ? -1 : networkService.getClientId();
        Collections.sort(sortedScores, new Comparator<PlayerScoreSnapshot>() {
            @Override
            public int compare(PlayerScoreSnapshot left, PlayerScoreSnapshot right) {
                if (left.getPlayerId() == localClientId && right.getPlayerId() != localClientId) {
                    return -1;
                }
                if (right.getPlayerId() == localClientId && left.getPlayerId() != localClientId) {
                    return 1;
                }
                return Integer.compare(left.getPlayerId(), right.getPlayerId());
            }
        });

        StringBuilder scoreText = new StringBuilder("Scores");
        for (PlayerScoreSnapshot score : sortedScores) {
            scoreText.append('\n');
            if (score.getPlayerId() == localClientId) {
                scoreText.append("You");
            } else {
                scoreText.append("Player ").append(score.getPlayerId());
            }
            scoreText.append(": ").append(score.getScore());
        }
        scoreLabel.setText(scoreText.toString());
    }

    private void updateLocalBallPosition(float x, float y, boolean reliable) {
        int clientId = networkService.getClientId();
        if (clientId <= 0) {
            return;
        }

        float clampedX = Math.max(0f, Math.min(x, Constant.WORLD_WIDTH - Constant.PLAYER_SIZE));
        float clampedY = Math.max(0f, Math.min(y, Constant.WORLD_HEIGHT - Constant.HUD_HEIGHT - Constant.PLAYER_SIZE));

        BallRenderState ballRenderState = getOrCreateBallState(clientId, clampedX, clampedY);
        ballRenderState.snapTo(clampedX, clampedY);

        BallInfo ballInfo = new BallInfo();
        ballInfo.setPosx(clampedX);
        ballInfo.setPosy(clampedY);
        ballInfo.setBallId(clientId);

        if (reliable) {
            networkService.sendReliable(ballInfo);
        } else {
            networkService.sendUnreliable(ballInfo);
        }
    }

    @Override
    public void dispose() {
        if (networkService != null) {
            networkService.unsubscribe(this);
        }
        resetBallStates();
        resetCollectibles();
        if (scoreFont != null) {
            scoreFont.dispose();
            scoreFont = null;
        }
        super.dispose();
    }

    private static class BallRenderState {
        private final Image image;
        private float startX;
        private float startY;
        private float targetX;
        private float targetY;
        private float elapsed;
        private float duration;

        private BallRenderState(Image image, float x, float y) {
            this.image = image;
            snapTo(x, y);
        }

        private void snapTo(float x, float y) {
            this.startX = x;
            this.startY = y;
            this.targetX = x;
            this.targetY = y;
            this.elapsed = 0f;
            this.duration = 0f;
            image.setPosition(x, y);
        }

        private void beginInterpolation(float x, float y, float duration) {
            this.startX = image.getX();
            this.startY = image.getY();
            this.targetX = x;
            this.targetY = y;
            this.elapsed = 0f;
            this.duration = duration;

            if (duration <= 0f) {
                snapTo(x, y);
            }
        }

        private void update(float delta) {
            if (duration <= 0f || elapsed >= duration) {
                return;
            }

            elapsed = Math.min(duration, elapsed + delta);
            float alpha = duration == 0f ? 1f : elapsed / duration;
            image.setPosition(
                    MathUtils.lerp(startX, targetX, alpha),
                    MathUtils.lerp(startY, targetY, alpha)
            );
        }

        private boolean isFarFrom(float x, float y, float maxDistance) {
            float deltaX = image.getX() - x;
            float deltaY = image.getY() - y;
            return deltaX * deltaX + deltaY * deltaY > maxDistance * maxDistance;
        }
    }
}
