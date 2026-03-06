package kw.tony.net.server.event;

public class ClientDisconnectedEvent {
    private final int clientId;

    public ClientDisconnectedEvent(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }
}
