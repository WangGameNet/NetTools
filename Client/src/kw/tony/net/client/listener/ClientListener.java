package kw.tony.net.client.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

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
        NLog.i("client====>  received ");
        if (clientGameListener!=null) {
            if (object instanceof TestMesssage) {
                clientGameListener.testMesssage((TestMesssage) object);
            } else if (object instanceof WorldMessage) {
                clientGameListener.WorldMessage((WorldMessage) object);
            }else if (object instanceof BallInitMessage) {
                System.out.println(object);
                clientGameListener.ballInitMessage((BallInitMessage) object);
            }
        }
    }

    @Override
    public void idle(Connection connection) {
        Listener.super.idle(connection);
        NLog.i("client====>  idle ");
    }

    private ClientGameListener clientGameListener;
    public void setClientGameListener(ClientGameListener clientGameListener) {
        this.clientGameListener = clientGameListener;
    }
}
