package kw.tony.net.server;

import kw.tony.net.server.event.BallState;
import kw.tony.net.server.event.ClientConnectedEvent;
import kw.tony.net.server.event.ClientDisconnectedEvent;
import kw.tony.net.server.event.ClientTestMessageReceivedEvent;

public interface ServerNetworkSubscriber {
    default void onClientConnected(ClientConnectedEvent clientConnectedEvent) {
    }

    default void onClientDisconnected(ClientDisconnectedEvent clientDisconnectedEvent) {
    }

    default void onTestMessageReceived(ClientTestMessageReceivedEvent clientTestMessageReceivedEvent) {
    }

    default void onUpdateBallPos(BallState event){}
}
