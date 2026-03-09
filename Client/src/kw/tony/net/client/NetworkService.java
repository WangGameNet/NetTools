package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import kw.tony.net.client.event.AvailableServersChangedEvent;
import kw.tony.net.client.event.BallSnapshot;
import kw.tony.net.client.event.ConnectionFailedEvent;
import kw.tony.net.client.event.DiscoveredServer;
import kw.tony.net.client.event.InitialWorldStateEvent;
import kw.tony.net.client.event.TestMessageEvent;
import kw.tony.net.client.event.WorldSnapshotEvent;
import kw.tony.net.client.listener.ClientListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkService {
    private static final int DISCOVERY_BUFFER_SIZE = 256;
    private static final int DISCOVERY_POLL_TIMEOUT_MILLIS = 200;

    private final Client client;
    private final ClientListener clientListener;
    private final ExecutorService connectionExecutor;
    private final ExecutorService discoveryExecutor;
    private final AtomicBoolean connecting;
    private final AtomicBoolean discovering;
    private final ConcurrentLinkedQueue<Object> eventQueue;
    private final AtomicReference<WorldMessage> latestWorldMessage;
    private final Set<NetworkEventSubscriber> subscribers;

    private volatile ConnectionLifecycleState connectionLifecycleState = ConnectionLifecycleState.IDLE;
    private volatile boolean running;
    private volatile DiscoveredServer requestedServer;
    private volatile AvailableServersChangedEvent latestAvailableServersChangedEvent;
    private volatile InitialWorldStateEvent currentInitialWorldStateEvent;
    private volatile WorldSnapshotEvent currentWorldSnapshotEvent;
    private volatile ConnectionFailedEvent lastConnectionFailedEvent;
    private int clientId;

    public NetworkService() {
        this.client = new Client();
        this.clientListener = new ClientListener(this);
        this.connectionExecutor = Executors.newSingleThreadExecutor();
        this.discoveryExecutor = Executors.newSingleThreadExecutor();
        this.connecting = new AtomicBoolean(false);
        this.discovering = new AtomicBoolean(false);
        this.eventQueue = new ConcurrentLinkedQueue<Object>();
        this.latestWorldMessage = new AtomicReference<WorldMessage>();
        this.subscribers = new CopyOnWriteArraySet<NetworkEventSubscriber>();
        this.latestAvailableServersChangedEvent = new AvailableServersChangedEvent(Collections.<DiscoveredServer>emptyList());
        ClassRegister.register(client.getKryo());
        client.addListener(clientListener);
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        client.start();
    }

    public void subscribe(NetworkEventSubscriber subscriber) {
        if (subscriber != null && subscribers.add(subscriber)) {
            dispatchCurrentState(subscriber);
        }
    }

    public void unsubscribe(NetworkEventSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.remove(subscriber);
        }
    }

    public void update() {
        Object event;
        while ((event = eventQueue.poll()) != null) {
            dispatchQueuedEvent(event);
        }

        WorldMessage worldMessage = latestWorldMessage.getAndSet(null);
        if (worldMessage != null) {
            WorldSnapshotEvent worldSnapshotEvent = toWorldSnapshotEvent(worldMessage);
            currentWorldSnapshotEvent = worldSnapshotEvent;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onWorldSnapshot(worldSnapshotEvent);
            }
        }
    }

    public void refreshAvailableServers() {
        if (!running || !discovering.compareAndSet(false, true)) {
            return;
        }

        discoveryExecutor.execute(() -> {
            LinkedHashMap<String, DiscoveredServer> discoveredServers = new LinkedHashMap<String, DiscoveredServer>();
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setBroadcast(true);
                socket.setSoTimeout(DISCOVERY_POLL_TIMEOUT_MILLIS);

                byte[] requestBytes = Constant.DISCOVERY_REQUEST_TOKEN.getBytes(StandardCharsets.UTF_8);
                sendDiscoveryPacket(socket, requestBytes, InetAddress.getByName("127.0.0.1"));
                broadcastDiscoveryPackets(socket, requestBytes);

                long deadline = System.currentTimeMillis() + Constant.DISCOVERY_TIMEOUT_MILLIS;
                while (System.currentTimeMillis() < deadline) {
                    DatagramPacket responsePacket = new DatagramPacket(new byte[DISCOVERY_BUFFER_SIZE], DISCOVERY_BUFFER_SIZE);
                    try {
                        socket.receive(responsePacket);
                    } catch (SocketTimeoutException ignored) {
                        continue;
                    }

                    DiscoveredServer discoveredServer = parseDiscoveryResponse(responsePacket);
                    if (discoveredServer != null) {
                        discoveredServers.put(discoveredServer.getDisplayAddress(), discoveredServer);
                    }
                }
            } catch (IOException ignored) {
                // Discovery is best effort.
            } finally {
                discovering.set(false);
            }

            AvailableServersChangedEvent availableServersChangedEvent = new AvailableServersChangedEvent(
                    Collections.unmodifiableList(new ArrayList<DiscoveredServer>(discoveredServers.values()))
            );
            latestAvailableServersChangedEvent = availableServersChangedEvent;
            eventQueue.offer(availableServersChangedEvent);
        });
    }

    public void connect(DiscoveredServer discoveredServer) {
        if (!running || discoveredServer == null || !connecting.compareAndSet(false, true)) {
            return;
        }

        requestedServer = discoveredServer;
        clearWorldState();
        lastConnectionFailedEvent = null;
        eventQueue.offer(new ConnectionStateEvent(ConnectionLifecycleState.CONNECTING, discoveredServer));

        connectionExecutor.execute(() -> {
            try {
                if (client.isConnected()) {
                    client.close();
                }
                client.connect(5000, discoveredServer.getHost(), discoveredServer.getTcpPort(), discoveredServer.getUdpPort());
            } catch (IOException e) {
                ConnectionFailedEvent connectionFailedEvent = new ConnectionFailedEvent(
                        discoveredServer,
                        e.getMessage() == null ? "Connect failed" : e.getMessage()
                );
                lastConnectionFailedEvent = connectionFailedEvent;
                eventQueue.offer(connectionFailedEvent);
            } finally {
                connecting.set(false);
            }
        });
    }

    public void sendReliable(Object message) {
        client.sendTCP(message);
    }

    public void dispose() {
        running = false;
        clearEventBuffers();
        subscribers.clear();
        client.stop();
        connectionExecutor.shutdownNow();
        discoveryExecutor.shutdownNow();
    }

    public void onNetworkMessage(Object object) {
        if (object instanceof WorldMessage) {
            offerWorldMessage((WorldMessage) object);
        } else {
            eventQueue.offer(object);
        }
    }

    public void onDisconnected() {
        clearWorldState();
        eventQueue.offer(new ConnectionStateEvent(ConnectionLifecycleState.DISCONNECTED, requestedServer));
    }

    public void onConnected(Connection connection) {
        this.clientId = connection.getID();
        lastConnectionFailedEvent = null;
        eventQueue.offer(new ConnectionStateEvent(ConnectionLifecycleState.CONNECTED, requestedServer));
    }

    private void dispatchQueuedEvent(Object event) {
        if (event instanceof AvailableServersChangedEvent) {
            AvailableServersChangedEvent availableServersChangedEvent = (AvailableServersChangedEvent) event;
            latestAvailableServersChangedEvent = availableServersChangedEvent;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onAvailableServersChanged(availableServersChangedEvent);
            }
            return;
        }

        if (event instanceof ConnectionStateEvent) {
            dispatchConnectionStateEvent((ConnectionStateEvent) event);
            return;
        }

        if (event instanceof ConnectionFailedEvent) {
            ConnectionFailedEvent connectionFailedEvent = (ConnectionFailedEvent) event;
            connectionLifecycleState = ConnectionLifecycleState.CONNECTION_FAILED;
            lastConnectionFailedEvent = connectionFailedEvent;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onConnectionFailed(connectionFailedEvent);
            }
            return;
        }

        if (event instanceof BallInitMessage) {
            InitialWorldStateEvent initialWorldStateEvent = toInitialWorldStateEvent((BallInitMessage) event);
            currentInitialWorldStateEvent = initialWorldStateEvent;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onInitialWorldState(initialWorldStateEvent);
            }
            return;
        }

        if (event instanceof TestMesssage) {
            TestMessageEvent testMessageEvent = toTestMessageEvent((TestMesssage) event);
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onTestMessage(testMessageEvent);
            }
        }
    }

    private void dispatchConnectionStateEvent(ConnectionStateEvent connectionStateEvent) {
        connectionLifecycleState = connectionStateEvent.state;
        if (connectionStateEvent.state == ConnectionLifecycleState.CONNECTING && connectionStateEvent.server != null) {
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onConnecting(connectionStateEvent.server);
            }
            return;
        }

        if (connectionStateEvent.state == ConnectionLifecycleState.CONNECTED) {
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onConnected();
            }
            return;
        }

        if (connectionStateEvent.state == ConnectionLifecycleState.DISCONNECTED) {
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onDisconnected();
            }
            return;
        }

        if (connectionStateEvent.state == ConnectionLifecycleState.RECONNECTING) {
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onReconnecting();
            }
        }
    }

    private void dispatchCurrentState(NetworkEventSubscriber subscriber) {
        if (latestAvailableServersChangedEvent != null) {
            subscriber.onAvailableServersChanged(latestAvailableServersChangedEvent);
        }

        if (connectionLifecycleState == ConnectionLifecycleState.CONNECTING && requestedServer != null) {
            subscriber.onConnecting(requestedServer);
        } else if (connectionLifecycleState == ConnectionLifecycleState.CONNECTED) {
            subscriber.onConnected();
        } else if (connectionLifecycleState == ConnectionLifecycleState.DISCONNECTED) {
            subscriber.onDisconnected();
        } else if (connectionLifecycleState == ConnectionLifecycleState.RECONNECTING) {
            subscriber.onReconnecting();
        } else if (connectionLifecycleState == ConnectionLifecycleState.CONNECTION_FAILED && lastConnectionFailedEvent != null) {
            subscriber.onConnectionFailed(lastConnectionFailedEvent);
        }

        if (currentInitialWorldStateEvent != null) {
            subscriber.onInitialWorldState(currentInitialWorldStateEvent);
        }
        if (currentWorldSnapshotEvent != null) {
            subscriber.onWorldSnapshot(currentWorldSnapshotEvent);
        }
    }

    private void clearEventBuffers() {
        eventQueue.clear();
        latestWorldMessage.set(null);
    }

    private void clearWorldState() {
        clearEventBuffers();
        currentInitialWorldStateEvent = null;
        currentWorldSnapshotEvent = null;
    }

    private void offerWorldMessage(WorldMessage worldMessage) {
        while (true) {
            WorldMessage currentMessage = latestWorldMessage.get();
            if (currentMessage != null && currentMessage.getSnapshotId() >= worldMessage.getSnapshotId()) {
                return;
            }
            if (latestWorldMessage.compareAndSet(currentMessage, worldMessage)) {
                return;
            }
        }
    }

    private void sendDiscoveryPacket(DatagramSocket socket, byte[] requestBytes, InetAddress targetAddress) throws IOException {
        DatagramPacket requestPacket = new DatagramPacket(
                requestBytes,
                requestBytes.length,
                targetAddress,
                Constant.DISCOVERY_PORT
        );
        socket.send(requestPacket);
    }

    private void broadcastDiscoveryPackets(DatagramSocket socket, byte[] requestBytes) throws IOException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                continue;
            }

            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast != null) {
                    sendDiscoveryPacket(socket, requestBytes, broadcast);
                }
            }
        }
    }

    private DiscoveredServer parseDiscoveryResponse(DatagramPacket responsePacket) {
        String payload = new String(
                responsePacket.getData(),
                responsePacket.getOffset(),
                responsePacket.getLength(),
                StandardCharsets.UTF_8
        );
        String[] parts = payload.split("\\|");
        if (parts.length != 4 || !Constant.DISCOVERY_RESPONSE_TOKEN.equals(parts[0])) {
            return null;
        }

        try {
            return new DiscoveredServer(
                    parts[1],
                    responsePacket.getAddress().getHostAddress(),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private InitialWorldStateEvent toInitialWorldStateEvent(BallInitMessage ballInitMessage) {
        return new InitialWorldStateEvent(toBallSnapshots(ballInitMessage.getPositions()));
    }

    private WorldSnapshotEvent toWorldSnapshotEvent(WorldMessage worldMessage) {
        return new WorldSnapshotEvent(worldMessage.getSnapshotId(), toBallSnapshots(worldMessage.getPositions()));
    }

    private TestMessageEvent toTestMessageEvent(TestMesssage testMesssage) {
        return new TestMessageEvent(testMesssage.getValue(), testMesssage.getName());
    }

    private List<BallSnapshot> toBallSnapshots(List<BallInfo> ballInfos) {
        ArrayList<BallSnapshot> ballSnapshots = new ArrayList<BallSnapshot>(ballInfos.size());
        for (BallInfo ballInfo : ballInfos) {
            ballSnapshots.add(new BallSnapshot(ballInfo.getBallId(), ballInfo.getPosx(), ballInfo.getPosy()));
        }
        return Collections.unmodifiableList(ballSnapshots);
    }

    public int getClientId() {
        return clientId;
    }

    private static class ConnectionStateEvent {
        private final ConnectionLifecycleState state;
        private final DiscoveredServer server;

        private ConnectionStateEvent(ConnectionLifecycleState state, DiscoveredServer server) {
            this.state = state;
            this.server = server;
        }
    }

    private enum ConnectionLifecycleState {
        IDLE,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        RECONNECTING,
        CONNECTION_FAILED
    }
}
