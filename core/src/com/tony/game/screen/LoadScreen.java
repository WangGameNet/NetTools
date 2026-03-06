package com.tony.game.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.kw.gdx.BaseGame;
import com.kw.gdx.asset.Asset;
import com.kw.gdx.screen.BaseScreen;
import com.tony.game.constant.GameConstant;
import kw.tony.net.client.ClientMain;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

import java.util.HashMap;
import java.util.Map;

public class LoadScreen extends BaseScreen {
    private final Map<Integer, BallRenderState> ballStates = new HashMap<Integer, BallRenderState>();
    private long lastSnapshotId = -1L;

    public LoadScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void initView() {
        super.initView();
        GameConstant.clientMain = ClientMain.getInstant();
    }

    @Override
    public void render(float delta) {
        flushNetworkMessages();
        updateBallInterpolation(delta);
        super.render(delta);
    }

    private void flushNetworkMessages() {
        if (GameConstant.clientMain == null) {
            return;
        }

        Object event;
        while ((event = GameConstant.clientMain.pollEvent()) != null) {
            if (event instanceof BallInitMessage) {
                applyBallInitMessage((BallInitMessage) event);
            } else if (event instanceof TestMesssage) {
                applyTestMessage((TestMesssage) event);
            }
        }

        WorldMessage worldMessage = GameConstant.clientMain.consumeLatestWorldMessage();
        if (worldMessage != null) {
            applyWorldMessage(worldMessage);
        }
    }

    private void applyBallInitMessage(BallInitMessage ballInitMessage) {
        resetBallStates();
        lastSnapshotId = -1L;
        for (BallInfo position : ballInitMessage.getPositions()) {
            BallRenderState ballRenderState = getOrCreateBallState(position.getBallId(), position.getPosx(), position.getPosy());
            ballRenderState.snapTo(position.getPosx(), position.getPosy());
        }
    }

    private void applyWorldMessage(WorldMessage worldMessage) {
        if (worldMessage.getSnapshotId() <= lastSnapshotId) {
            return;
        }
        lastSnapshotId = worldMessage.getSnapshotId();

        for (BallInfo position : worldMessage.getPositions()) {
            BallRenderState ballRenderState = getOrCreateBallState(position.getBallId(), position.getPosx(), position.getPosy());
            ballRenderState.beginInterpolation(position.getPosx(), position.getPosy(), Constant.SNAPSHOT_INTERVAL_SECONDS);
        }
    }

    private void applyTestMessage(TestMesssage testMesssage) {
        // Reserved for future reliable UI events.
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
        addActor(image);
        ballRenderState = new BallRenderState(image, x, y);
        ballStates.put(ballId, ballRenderState);
        return ballRenderState;
    }

    private void resetBallStates() {
        for (BallRenderState ballRenderState : ballStates.values()) {
            ballRenderState.image.remove();
        }
        ballStates.clear();
    }

    @Override
    public void dispose() {
        if (GameConstant.clientMain != null) {
            GameConstant.clientMain.clearInbox();
        }
        resetBallStates();
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
    }
}
