package kw.tony.net.client.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.client.ClientMain;

public class ClientListener implements Listener {
    private final ClientMain clientMain;

    public ClientListener(ClientMain clientMain) {
        this.clientMain = clientMain;
    }

    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
        NLog.i("client====>  connected ");
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
        NLog.i("client====>  disconnected ");
        clientMain.onDisconnected();
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);
        NLog.i("client====>  received ");
        clientMain.onNetworkMessage(object);
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
        NLog.i("client====>  idle ");
    }
}
