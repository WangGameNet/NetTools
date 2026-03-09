package kw.tony.net.server;

import kw.tony.shared.constant.Constant;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class ServerDiscoveryService {
    private static final int BUFFER_SIZE = 256;
    private static final int SOCKET_TIMEOUT_MILLIS = 1000;

    private final String serverName;

    private DatagramSocket socket;
    private Thread workerThread;
    private volatile boolean running;

    public ServerDiscoveryService() {
        this.serverName = resolveServerName();
    }

    public void start() {
        if (running) {
            return;
        }

        try {
            socket = new DatagramSocket(Constant.DISCOVERY_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
        } catch (SocketException e) {
            throw new IllegalStateException("Failed to start discovery socket", e);
        }

        running = true;
        workerThread = new Thread(this::listenLoop, "server-discovery");
        workerThread.setDaemon(true);
        workerThread.start();
    }

    public void stop() {
        running = false;
        if (socket != null) {
            socket.close();
            socket = null;
        }
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread = null;
        }
    }

    private void listenLoop() {
        while (running) {
            try {
                DatagramPacket requestPacket = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                socket.receive(requestPacket);
                handleRequest(requestPacket);
            } catch (SocketTimeoutException ignored) {
                // Keep the loop responsive to shutdown.
            } catch (IOException e) {
                if (!running) {
                    return;
                }
            }
        }
    }

    private void handleRequest(DatagramPacket requestPacket) throws IOException {
        String request = new String(
                requestPacket.getData(),
                requestPacket.getOffset(),
                requestPacket.getLength(),
                StandardCharsets.UTF_8
        );
        if (!Constant.DISCOVERY_REQUEST_TOKEN.equals(request)) {
            return;
        }

        byte[] responseBytes = (
                Constant.DISCOVERY_RESPONSE_TOKEN + "|" +
                serverName + "|" +
                Constant.TCP_PORT + "|" +
                Constant.UDP_PORT
        ).getBytes(StandardCharsets.UTF_8);
        DatagramPacket responsePacket = new DatagramPacket(
                responseBytes,
                responseBytes.length,
                requestPacket.getAddress(),
                requestPacket.getPort()
        );
        socket.send(responsePacket);
    }

    private String resolveServerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "NetTools Server";
        }
    }
}
