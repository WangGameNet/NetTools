package kw.tony.net.server.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class ServerListener implements Listener {

    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
    }
}
