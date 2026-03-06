package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import kw.tony.net.client.listener.ClientListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClientMain {
    private static final long RECONNECT_DELAY_MILLIS = 2000L;

    private static ClientMain clientMain;

    public final Client client;
    private final ClientListener clientListener;
    private final ExecutorService connectionExecutor;
    private final AtomicBoolean connecting;
    private volatile boolean running;

    // Reliable TCP messages are consumed on the render thread.
    private final ConcurrentLinkedQueue<Object> eventQueue = new ConcurrentLinkedQueue<Object>();
    // World snapshots are lossy; only the newest one matters.
    private final AtomicReference<WorldMessage> latestWorldMessage = new AtomicReference<WorldMessage>();

    public ClientMain() {
        this.connectionExecutor = Executors.newSingleThreadExecutor();
        this.connecting = new AtomicBoolean(false);
        this.running = true;
        this.client = new Client();
        ClassRegister.register(client.getKryo());
        this.clientListener = new ClientListener(this);
        client.addListener(clientListener);
        client.start();
        connetServer();
    }

    private void connetServer() {
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
                    connetServer();
                }
            }
        });
    }

    public static ClientMain getInstant() {
        if (clientMain == null) {
            clientMain = new ClientMain();
        }
        return clientMain;
    }

    public void onNetworkMessage(Object object) {
        if (object instanceof WorldMessage) {
            offerWorldMessage((WorldMessage) object);
        } else {
            eventQueue.offer(object);
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

    public Object pollEvent() {
        return eventQueue.poll();
    }

    public WorldMessage consumeLatestWorldMessage() {
        return latestWorldMessage.getAndSet(null);
    }

    public void clearInbox() {
        eventQueue.clear();
        latestWorldMessage.set(null);
    }

    public void onDisconnected() {
        clearInbox();
        connetServer();
    }

    public void sendTCP(Object message) {
        client.sendTCP(message);
    }

    public void dispose() {
        running = false;
        clearInbox();
        client.stop();
        connectionExecutor.shutdownNow();
    }
}
