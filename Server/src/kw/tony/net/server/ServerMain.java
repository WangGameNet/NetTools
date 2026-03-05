package kw.tony.net.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.server.listener.ServerListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.LoginMesssage;
import kw.tony.shared.constant.message.Message;

import java.io.IOException;

public class ServerMain extends ApplicationAdapter {
    private Server server;

    @Override
    public void create() {
        super.create();
        this.server = new Server();
        addClass();
        server.addListener(new ServerListener(server));
        server.start();
        try {
            server.bind(Constant.TCP_PORT, Constant.UDP_PORT);
        } catch (IOException e) {
            NLog.d(e);
        }

    }

    public void addClass(){
        server.getKryo().register(LoginMesssage.class);
    }
}
