package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.client.listener.ClientListener;

import java.io.IOException;

public class ClientMain {
    public Client client;
    public ClientMain(){
        client = new Client();
        try {
            client.connect(5000,"localhost",12345);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        client.addListener(new ClientListener());
        client.start();
    }

}
