package kw.tony.net.client.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;

public class ClientListener implements Listener {
    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
        NLog.i("client====>  connected ");
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
        NLog.i("client====>  disconnected ");
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);
        NLog.i("client====>  disconnected ");
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
        NLog.i("client====>  idle ");
    }
}
