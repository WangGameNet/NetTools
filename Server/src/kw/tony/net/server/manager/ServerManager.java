package kw.tony.net.server.manager;

import com.badlogic.gdx.utils.Array;
import kw.tony.net.server.ServerNetworkService;
import kw.tony.net.server.ServerNetworkSubscriber;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.net.server.event.*;
import kw.tony.net.server.game.GameWorld;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.RemoveMessage;

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
        //更新是延后的
        while (snapshotAccumulator >= Constant.SNAPSHOT_INTERVAL_SECONDS) {
            broadcastSnapshot();
            snapshotAccumulator -= Constant.SNAPSHOT_INTERVAL_SECONDS;
        }
    }

    @Override
    public void onClientConnected(ClientConnectedEvent clientConnectedEvent) {
        gameWorld.createGame(clientConnectedEvent.getClientId());
        serverNetworkService.sendInitialWorldState(
                clientConnectedEvent.getClientId(),
                new InitialWorldState(copyBallStates())
        );
    }

    @Override
    public void onClientDisconnected(ClientDisconnectedEvent clientDisconnectedEvent) {
        // Reserved for future session cleanup.
        gameWorld.removeBall(clientDisconnectedEvent.getClientId());
        RemoveIdState removeMessage = new RemoveIdState();
        removeMessage.setRemoveId(clientDisconnectedEvent.getClientId());
        serverNetworkService.broadcastRemoveMessage(removeMessage);

    }

    @Override
    public void onTestMessageReceived(ClientTestMessageReceivedEvent clientTestMessageReceivedEvent) {
        serverNetworkService.broadcastTestMessage(new TestMessagePayload(
                clientTestMessageReceivedEvent.getValue(),
                "xxxxxxxxx"
        ));
    }

    @Override
    public void onSendBallMessage(Object event) {
        BallInfo ballInfo = (BallInfo) event;
        BallState ballState = new BallState(ballInfo.getBallId(), ballInfo.getPosx(),ballInfo.getPosy());
        gameWorld.updateBallPos(ballState);
    }

    private void broadcastSnapshot() {
        serverNetworkService.broadcastWorldSnapshot(new WorldSnapshot(++snapshotId % 1000, copyBallStates()));
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
}
