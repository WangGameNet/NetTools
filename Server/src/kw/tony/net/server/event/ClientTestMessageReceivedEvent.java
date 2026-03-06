package kw.tony.net.server.event;

public class ClientTestMessageReceivedEvent {
    private final int clientId;
    private final int value;
    private final String name;

    public ClientTestMessageReceivedEvent(int clientId, int value, String name) {
        this.clientId = clientId;
        this.value = value;
        this.name = name;
    }

    public int getClientId() {
        return clientId;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
