package kw.tony.net.server.manager;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.net.server.game.GameWorld;
import kw.tony.net.server.listener.ServerListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.ArrayList;

public class ServerManager {
    private final Server server;
    private static ServerManager instance;
    private final GameWorld gameWorld;
    private float simulationAccumulator;
    private float snapshotAccumulator;
    private long snapshotId;

    private ServerManager() {
        this.server = new Server();
        ClassRegister.register(server.getKryo());
        server.addListener(new ServerListener(server, this));
        server.start();
        try {
            server.bind(Constant.TCP_PORT, Constant.UDP_PORT);
        } catch (IOException e) {
            NLog.d(e);
        }

        this.gameWorld = GameWorld.getInstance();
        this.gameWorld.startGame();
    }

    public void initGameData(Connection connection){
        BallInitMessage ballInitMessage = new BallInitMessage();
        ballInitMessage.setPositions(copyBallInfos());
        server.sendToTCP(connection.getID(), ballInitMessage);
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
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

    private void broadcastSnapshot() {
        if (server.getConnections().isEmpty()) {
            return;
        }

        WorldMessage worldMessage = new WorldMessage();
        worldMessage.setSnapshotId(++snapshotId);
        worldMessage.setPositions(copyBallInfos());
        server.sendToAllUDP(worldMessage);
    }

    private ArrayList<BallInfo> copyBallInfos() {
        ArrayList<BallInfo> ballInfos = new ArrayList<BallInfo>();
        Array<GameBallInfo> gameBallInfos = gameWorld.getBallInfos();
        for (GameBallInfo gameBallInfo : gameBallInfos) {
            ballInfos.add(new BallInfo(gameBallInfo.getId(), gameBallInfo.getCurrentX(), gameBallInfo.getCurrentY()));
        }
        return ballInfos;
    }
}
