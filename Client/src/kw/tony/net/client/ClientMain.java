package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.client.listener.ClientListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.LoginMesssage;
import kw.tony.shared.constant.message.Message;

import java.io.IOException;

public class ClientMain {
    private static ClientMain clientMain;
    public Client client;
    public ClientMain(){
        client = new Client();
        addClass();
        client.addListener(new ClientListener());
        client.start();
        try {
            client.connect(5000,"localhost", Constant.TCP_PORT,Constant.UDP_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void addClass(){
        client.getKryo().register(LoginMesssage.class);
    }

    public static ClientMain getInstant() {
        if(clientMain == null){
            clientMain = new ClientMain();
        }
        return clientMain;
    }

    public void sendTCP(Object loginMesssage) {
        client.sendTCP(loginMesssage);
    }
}
