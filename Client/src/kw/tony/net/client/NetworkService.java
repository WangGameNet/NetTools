package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import kw.tony.net.client.listener.ClientListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class NetworkService {
    private static final long RECONNECT_DELAY_MILLIS = 2000L;

    private final Client client;
    private final ClientListener clientListener;
    private final ExecutorService connectionExecutor;
    private final AtomicBoolean connecting;
    private final ConcurrentLinkedQueue<Object> eventQueue;
    private final AtomicReference<WorldMessage> latestWorldMessage;
    private final Set<NetworkEventSubscriber> subscribers;

    private volatile boolean running;

    public NetworkService() {
        this.client = new Client();
        this.clientListener = new ClientListener(this);
        this.connectionExecutor = Executors.newSingleThreadExecutor();
        this.connecting = new AtomicBoolean(false);
        this.eventQueue = new ConcurrentLinkedQueue<Object>();
        this.latestWorldMessage = new AtomicReference<WorldMessage>();
        this.subscribers = new CopyOnWriteArraySet<NetworkEventSubscriber>();
        ClassRegister.register(client.getKryo());
        client.addListener(clientListener);
    }

    public void start() {
        if (running) {
            return;
        }
        running = true;
        client.start();
        connect();
    }

    public void subscribe(NetworkEventSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.add(subscriber);
        }
    }

    public void unsubscribe(NetworkEventSubscriber subscriber) {
        if (subscriber != null) {
            subscribers.remove(subscriber);
        }
    }

    public void update() {
        if (subscribers.isEmpty()) {
            return;
        }

        Object event;
        while ((event = eventQueue.poll()) != null) {
            dispatchReliableEvent(event);
        }

        WorldMessage worldMessage = latestWorldMessage.getAndSet(null);
        if (worldMessage != null) {
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onWorldMessage(worldMessage);
            }
        }
    }

    public void sendReliable(Object message) {
        client.sendTCP(message);
    }

    public void dispose() {
        running = false;
        clearInbox();
        subscribers.clear();
        client.stop();
        connectionExecutor.shutdownNow();
    }

    public void onNetworkMessage(Object object) {
        if (object instanceof WorldMessage) {
            offerWorldMessage((WorldMessage) object);
        } else {
            eventQueue.offer(object);
        }
    }

    public void onDisconnected() {
        clearInbox();
        connect();
    }

    private void connect() {
        if (!running || !connecting.compareAndSet(false, true)) {
            return;
        }

        connectionExecutor.execute(() -> {
            try {
                while (running && !client.isConnected()) {
                    try {
                        client.connect(5000, Constant.SERVER_HOST, Constant.TCP_PORT, Constant.UDP_PORT);
                    } catch (IOException e) {
                        try {
                            Thread.sleep(RECONNECT_DELAY_MILLIS);
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            } finally {
                connecting.set(false);
                if (running && !client.isConnected()) {
                    connect();
                }
            }
        });
    }

    private void dispatchReliableEvent(Object event) {
        if (event instanceof BallInitMessage) {
            BallInitMessage ballInitMessage = (BallInitMessage) event;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onBallInitMessage(ballInitMessage);
            }
            return;
        }

        if (event instanceof TestMesssage) {
            TestMesssage testMesssage = (TestMesssage) event;
            for (NetworkEventSubscriber subscriber : subscribers) {
                subscriber.onTestMessage(testMesssage);
            }
        }
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

    private void clearInbox() {
        eventQueue.clear();
        latestWorldMessage.set(null);
    }
}
