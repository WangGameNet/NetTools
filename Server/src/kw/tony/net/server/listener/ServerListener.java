package kw.tony.net.server.listener;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kw.gdx.utils.log.NLog;
import kw.tony.shared.constant.message.LoginMesssage;
import kw.tony.shared.constant.message.Message;

public class ServerListener implements Listener {
    private Array<Connection> connections;
    private Server server;
    public ServerListener(Server server){
        this.server = server;
        this.connections = new Array<>();
    }

    @Override
    public void connected(Connection connection) {
        Listener.super.connected(connection);
        NLog.i("=========server ===== connected");
        connections.add(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        Listener.super.disconnected(connection);
        NLog.i("=========server ===== disconnected");
        connections.removeValue(connection,false);
    }

    @Override
    public void received(Connection connection, Object object) {
        Listener.super.received(connection, object);

        if (object instanceof Message){
            NLog.i("=========server ===== received");
            Gdx.app.postRunnable(()->{
                System.out.println(object);
                LoginMesssage loginMesssage = new LoginMesssage();
                loginMesssage.setName("xxxxxxxxx");
                server.sendToAllTCP(loginMesssage);
            });
        }
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
//        NLog.i("=========server ===== idle");
    }
}
