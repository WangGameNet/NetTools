package kw.tony.net.server.manager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.net.server.game.GameWorld;
import kw.tony.net.server.listener.ServerListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.ArrayList;

public class ServerManager {
    private Server server;
    private static ServerManager instance;
    private GameWorld gameWorld;

    private ServerManager() {
        this.server = new Server();
        ClassRegister.register(server.getKryo());
        server.addListener(new ServerListener(server,this));
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
        ArrayList<BallInfo> ballInfos = new ArrayList<BallInfo>();
        Array<GameBallInfo> gameBallInfos = gameWorld.getBallInfos();
        for (GameBallInfo gameBallInfo : gameBallInfos) {
            ballInfos.add(new BallInfo(gameBallInfo.getId(),gameBallInfo.getCurrentX(),gameBallInfo.getCurrentY()));
        }
        ballInitMessage.setPositions(ballInfos);
        System.out.println("send data "+ ballInfos);
        server.sendToTCP(connection.getID(),ballInitMessage);
    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void update(float deltaTime) {
        WorldMessage worldMessage = new WorldMessage();
        ArrayList<BallInfo> ballInfos = new ArrayList<BallInfo>();
        gameWorld.update(deltaTime);
        Array<GameBallInfo> gameBallInfos = gameWorld.getBallInfos();
        for (GameBallInfo gameBallInfo : gameBallInfos) {
            ballInfos.add(new BallInfo(gameBallInfo.getId(),gameBallInfo.getCurrentX(),gameBallInfo.getCurrentY()));
        }
        worldMessage.setPositions(ballInfos);
        server.sendToAllTCP(worldMessage);
    }
}
