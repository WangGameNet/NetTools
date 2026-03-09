package kw.tony.net.client.event;

public class ConnectionFailedEvent {
    private final DiscoveredServer server;
    private final String message;

    public ConnectionFailedEvent(DiscoveredServer server, String message) {
        this.server = server;
        this.message = message;
    }

    public DiscoveredServer getServer() {
        return server;
    }

    public String getMessage() {
        return message;
    }
}
