package kw.tony.net.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import kw.tony.net.server.listener.ServerListener;

public class ServerMain {
    private Server server;
    public ServerMain(){
        this.server = new Server();
        server.addListener(new ServerListener());
    }
}
