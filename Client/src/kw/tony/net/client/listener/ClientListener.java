package kw.tony.net.client.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.client.NetworkService;

public class ClientListener implements Listener {
    private final NetworkService networkService;

    public ClientListener(NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
        NLog.i("client====>  connected ");
        networkService.onConnected(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
        NLog.i("client====>  disconnected ");
        networkService.onDisconnected();
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);
        NLog.i("client====>  received ");
        networkService.onNetworkMessage(object);
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
        NLog.i("client====>  idle ");
    }
}
