package kw.tony.net.client;

import com.esotericsoftware.kryonet.Client;
import kw.tony.net.client.listener.ClientGameListener;
import kw.tony.net.client.listener.ClientListener;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;
import kw.tony.shared.constant.register.ClassRegister;

import java.io.IOException;
import java.util.ArrayList;

public class ClientMain {
    private static ClientMain clientMain;
    private ClientGameListener clientGameListener;
    public Client client;
    private ClientListener clientListener;
    public ClientMain(){
        client = new Client();
        ClassRegister.register(client.getKryo());
        this.clientListener = new ClientListener();
        client.addListener(clientListener);
        client.start();
        try {
            client.connect(5000,"localhost", Constant.TCP_PORT,Constant.UDP_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void setClientGameListener(ClientGameListener clientGameListener) {
        this.clientGameListener = clientGameListener;
        this.clientListener.setClientGameListener(this.clientGameListener);
    }
}
