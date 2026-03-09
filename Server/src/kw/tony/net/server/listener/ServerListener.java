package kw.tony.net.server.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.server.ServerNetworkService;

public class ServerListener implements Listener {
    private final ServerNetworkService serverNetworkService;

    public ServerListener(ServerNetworkService serverNetworkService) {
        this.serverNetworkService = serverNetworkService;
    }

    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
        serverNetworkService.onClientConnected(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
        serverNetworkService.onClientDisconnected(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);
        serverNetworkService.onMessageReceived(connection, object);
    }
}
