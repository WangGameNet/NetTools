package kw.tony.net.server.event;

public class ClientConnectedEvent {
    private final int clientId;

    public ClientConnectedEvent(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}
