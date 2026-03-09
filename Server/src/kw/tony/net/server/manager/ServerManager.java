package kw.tony.net.server.manager;

import com.badlogic.gdx.utils.Array;
import kw.tony.net.server.ServerNetworkService;
import kw.tony.net.server.ServerNetworkSubscriber;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.net.server.event.BallState;
import kw.tony.net.server.event.ClientConnectedEvent;
import kw.tony.net.server.event.ClientDisconnectedEvent;
import kw.tony.net.server.event.ClientTestMessageReceivedEvent;
import kw.tony.net.server.event.InitialWorldState;
import kw.tony.net.server.event.TestMessagePayload;
import kw.tony.net.server.event.WorldSnapshot;
import kw.tony.net.server.game.GameWorld;
import kw.tony.shared.constant.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerManager implements ServerNetworkSubscriber {
    private final ServerNetworkService serverNetworkService;
    private final GameWorld gameWorld;

    private float simulationAccumulator;
    private float snapshotAccumulator;
    private long snapshotId;

    public ServerManager(ServerNetworkService serverNetworkService, GameWorld gameWorld) {
        this.serverNetworkService = serverNetworkService;
        this.gameWorld = gameWorld;
        this.gameWorld.startGame();
    }

    public void update(float deltaTime) {
        float clampedDeltaTime = Math.min(deltaTime, Constant.MAX_FRAME_DELTA_SECONDS);
        simulationAccumulator += clampedDeltaTime;
        snapshotAccumulator += clampedDeltaTime;

        while (simulationAccumulator >= Constant.WORLD_STEP_SECONDS) {
            gameWorld.update(Constant.WORLD_STEP_SECONDS);
            simulationAccumulator -= Constant.WORLD_STEP_SECONDS;
        }

        while (snapshotAccumulator >= Constant.SNAPSHOT_INTERVAL_SECONDS) {
            broadcastSnapshot();
            snapshotAccumulator -= Constant.SNAPSHOT_INTERVAL_SECONDS;
        }
    }

    @Override
    public void onClientConnected(ClientConnectedEvent clientConnectedEvent) {
        gameWorld.createBall(clientConnectedEvent);
        serverNetworkService.sendInitialWorldState(
                clientConnectedEvent.getClientId(),
                new InitialWorldState(copyBallStates())
        );
    }

    @Override
    public void onClientDisconnected(ClientDisconnectedEvent clientDisconnectedEvent) {
        // Reserved for future session cleanup.
        gameWorld.removeBall(clientDisconnectedEvent);
        serverNetworkService.sendRemoveBallState(clientDisconnectedEvent.getClientId());
    }

    @Override
    public void onTestMessageReceived(ClientTestMessageReceivedEvent clientTestMessageReceivedEvent) {
        serverNetworkService.broadcastTestMessage(new TestMessagePayload(
                clientTestMessageReceivedEvent.getValue(),
                "xxxxxxxxx"
        ));
    }

    private void broadcastSnapshot() {
        serverNetworkService.broadcastWorldSnapshot(new WorldSnapshot(++snapshotId, copyBallStates()));
    }

    private List<BallState> copyBallStates() {
        Array<GameBallInfo> gameBallInfos = gameWorld.getBallInfos();
        ArrayList<BallState> ballStates = new ArrayList<BallState>(gameBallInfos.size);
        for (GameBallInfo gameBallInfo : gameBallInfos) {
            ballStates.add(new BallState(
                    gameBallInfo.getId(),
                    gameBallInfo.getCurrentX(),
                    gameBallInfo.getCurrentY()
            ));
        }
        return Collections.unmodifiableList(ballStates);
    }

    @Override
    public void onUpdateBallPos(BallState event) {
        gameWorld.updateBallPos(event);
    }
}
