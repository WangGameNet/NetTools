package com.tony.game.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kw.gdx.BaseGame;
import com.kw.gdx.asset.Asset;
import com.kw.gdx.screen.BaseScreen;
import com.tony.game.block.BlockActor;
import kw.tony.net.client.NetworkEventSubscriber;
import kw.tony.net.client.NetworkService;
import kw.tony.net.client.NetworkServiceProvider;
import kw.tony.net.client.event.*;
import kw.tony.shared.constant.Constant;

import java.util.HashMap;
import java.util.Map;

public class LoadScreen extends BaseScreen implements NetworkEventSubscriber {
    private final Map<Integer, BallRenderState> ballStates;
    private NetworkService networkService;
    private long lastSnapshotId = -1L;

    public LoadScreen(BaseGame game) {
        super(game);
        this.ballStates = new HashMap<Integer, BallRenderState>();
    }

    @Override
    public void show() {
        super.show();
        networkService = resolveNetworkService();
        networkService.subscribe(this);

        stage.addListener(new ClickListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                int clientId = networkService.getClientId();
                BallSnapshot ballSnapshot = new BallSnapshot(clientId,x,y);
                LoadScreen.this.getNetworkService().sendReliable(ballSnapshot);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                int clientId = networkService.getClientId();
                BallSnapshot ballSnapshot = new BallSnapshot(clientId,x,y);
                LoadScreen.this.getNetworkService().sendReliable(ballSnapshot);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                int clientId = networkService.getClientId();
                BallSnapshot ballSnapshot = new BallSnapshot(clientId,x,y);
                LoadScreen.this.getNetworkService().sendReliable(ballSnapshot);

            }
        });
    }

    private NetworkService getNetworkService() {
        return networkService;
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
        lastSnapshotId = -1L;
    }

    @Override
    public void onReconnecting() {
    }

    @Override
    public void onInitialWorldState(InitialWorldStateEvent initialWorldStateEvent) {
        resetBallStates();
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

        for (BallSnapshot ballSnapshot : worldSnapshotEvent.getBalls()) {
            BallRenderState ballRenderState = getOrCreateBallState(ballSnapshot.getBallId(), ballSnapshot.getX(), ballSnapshot.getY());
            ballRenderState.beginInterpolation(ballSnapshot.getX(), ballSnapshot.getY(), Constant.SNAPSHOT_INTERVAL_SECONDS);
        }
    }

    @Override
    public void onTestMessage(TestMessageEvent testMessageEvent) {
        // Reserved for future reliable UI events.
    }

    @Override
    public void onRemoveMessage(RemoveIdEvent removeIdEvent) {
        BallRenderState ballRenderState = ballStates.remove(removeIdEvent.getId());
        if (ballRenderState!=null){
            ballRenderState.removeImage();
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

        BlockActor image = new BlockActor();
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
        if (networkService != null) {
            networkService.unsubscribe(this);
        }
        resetBallStates();
        super.dispose();
    }

    private static class BallRenderState {
        private final BlockActor image;
        private float startX;
        private float startY;
        private float targetX;
        private float targetY;
        private float elapsed;
        private float duration;

        private BallRenderState(BlockActor image, float x, float y) {
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

        public void removeImage() {
            image.remove();
        }
    }
}
