package kw.tony.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import kw.tony.net.server.event.BallState;
import kw.tony.net.server.event.ClientConnectedEvent;
import kw.tony.net.server.event.ClientDisconnectedEvent;
import kw.tony.net.server.event.ClientTestMessageReceivedEvent;
import kw.tony.net.server.event.CollectibleState;
import kw.tony.net.server.event.InitialWorldState;
import kw.tony.net.server.event.PlayerScoreState;
import kw.tony.net.server.event.TestMessagePayload;
import kw.tony.net.server.event.WorldSnapshot;
import kw.tony.net.server.listener.ServerListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.bean.CollectibleInfo;
import kw.tony.shared.constant.bean.ScoreInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.RemoveMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerNetworkService {
    private final Server server;
    private final ServerListener serverListener;
    private final ServerDiscoveryService serverDiscoveryService;
    private final ConcurrentLinkedQueue<Object> inboundEventQueue;
    private final Set<ServerNetworkSubscriber> subscribers;
    private volatile boolean running;

    public ServerNetworkService() {
        this.server = new Server();
        this.serverListener = new ServerListener(this);
        this.serverDiscoveryService = new ServerDiscoveryService();
        this.inboundEventQueue = new ConcurrentLinkedQueue<Object>();
        this.subscribers = new CopyOnWriteArraySet<ServerNetworkSubscriber>();
        ClassRegister.register(server.getKryo());
        server.addListener(serverListener);
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        server.start();
        serverDiscoveryService.start();
        try {
            server.bind(Constant.TCP_PORT, Constant.UDP_PORT);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to bind server ports", e);
        }
    }

    public void stop() {
        running = false;
        inboundEventQueue.clear();
        subscribers.clear();
        serverDiscoveryService.stop();
        server.stop();
    }

    public void subscribe(ServerNetworkSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    public void unsubscribe(ServerNetworkSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.remove(subscriber);
        }
    }

    public void update() {
        Object event;
        while ((event = inboundEventQueue.poll()) != null) {
            dispatchInboundEvent(event);
        }
    }

    public void sendInitialWorldState(int clientId, InitialWorldState initialWorldState) {
        BallInitMessage ballInitMessage = new BallInitMessage();
        ballInitMessage.setPositions(toBallInfos(initialWorldState.getBalls()));
        ballInitMessage.setCollectibles(toCollectibleInfos(initialWorldState.getCollectibles()));
        ballInitMessage.setScores(toScoreInfos(initialWorldState.getScores()));
        server.sendToTCP(clientId, ballInitMessage);
    }

    public void broadcastWorldSnapshot(WorldSnapshot worldSnapshot) {
        if (server.getConnections().isEmpty()) {
            return;
        }

        WorldMessage worldMessage = new WorldMessage();
        worldMessage.setSnapshotId(worldSnapshot.getSnapshotId());
        worldMessage.setPositions(toBallInfos(worldSnapshot.getBalls()));
        worldMessage.setCollectibles(toCollectibleInfos(worldSnapshot.getCollectibles()));
        worldMessage.setScores(toScoreInfos(worldSnapshot.getScores()));
        server.sendToAllUDP(worldMessage);
    }

    public void sendRemoveBallState(int clientId){
        RemoveMessage message = new RemoveMessage();
        message.setClientId(clientId);
        server.sendToAllTCP(message);
    }

    public void broadcastTestMessage(TestMessagePayload testMessagePayload) {
        TestMesssage testMesssage = new TestMesssage();
        testMesssage.setValue(testMessagePayload.getValue());
        testMesssage.setName(testMessagePayload.getName());
        server.sendToAllTCP(testMesssage);
    }

    public void onClientConnected(Connection connection) {
        inboundEventQueue.offer(new ClientConnectedEvent(connection.getID()));
    }

    public void onClientDisconnected(Connection connection) {
        inboundEventQueue.offer(new ClientDisconnectedEvent(connection.getID()));
    }

    public void onMessageReceived(Connection connection, Object object) {
        if (object instanceof TestMesssage) {
            TestMesssage testMesssage = (TestMesssage) object;
            inboundEventQueue.offer(new ClientTestMessageReceivedEvent(
                    connection.getID(),
                    testMesssage.getValue(),
                    testMesssage.getName()
            ));
            return;
        }

        if (object instanceof BallInfo){
            BallInfo info = (BallInfo) object;
            BallState ballState = new BallState(info.getBallId(),info.getPosx(),info.getPosy());
            inboundEventQueue.offer(ballState);
            return;
        }
    }

    private void dispatchInboundEvent(Object event) {
        if (event instanceof ClientConnectedEvent) {
            ClientConnectedEvent clientConnectedEvent = (ClientConnectedEvent) event;
            for (ServerNetworkSubscriber subscriber : subscribers) {
                subscriber.onClientConnected(clientConnectedEvent);
            }
            return;
        }

        if (event instanceof ClientDisconnectedEvent) {
            ClientDisconnectedEvent clientDisconnectedEvent = (ClientDisconnectedEvent) event;
            for (ServerNetworkSubscriber subscriber : subscribers) {
                subscriber.onClientDisconnected(clientDisconnectedEvent);
            }
            return;
        }

        if (event instanceof ClientTestMessageReceivedEvent) {
            ClientTestMessageReceivedEvent clientTestMessageReceivedEvent = (ClientTestMessageReceivedEvent) event;
            for (ServerNetworkSubscriber subscriber : subscribers) {
                subscriber.onTestMessageReceived(clientTestMessageReceivedEvent);
            }
            return;
        }

        if (event instanceof BallState){
            for (ServerNetworkSubscriber subscriber : subscribers) {
                subscriber.onUpdateBallPos((BallState)event);
            }
        }


    }

    private ArrayList<BallInfo> toBallInfos(List<BallState> ballStates) {
        ArrayList<BallInfo> ballInfos = new ArrayList<BallInfo>(ballStates.size());
        for (BallState ballState : ballStates) {
            ballInfos.add(new BallInfo(ballState.getBallId(), ballState.getX(), ballState.getY()));
        }
        return ballInfos;
    }

    private ArrayList<CollectibleInfo> toCollectibleInfos(List<CollectibleState> collectibleStates) {
        ArrayList<CollectibleInfo> collectibleInfos = new ArrayList<CollectibleInfo>(collectibleStates.size());
        for (CollectibleState collectibleState : collectibleStates) {
            collectibleInfos.add(new CollectibleInfo(
                    collectibleState.getCollectibleId(),
                    collectibleState.getX(),
                    collectibleState.getY()
            ));
        }
        return collectibleInfos;
    }

    private ArrayList<ScoreInfo> toScoreInfos(List<PlayerScoreState> scoreStates) {
        ArrayList<ScoreInfo> scoreInfos = new ArrayList<ScoreInfo>(scoreStates.size());
        for (PlayerScoreState scoreState : scoreStates) {
            scoreInfos.add(new ScoreInfo(scoreState.getPlayerId(), scoreState.getScore()));
        }
        return scoreInfos;
    }
}
