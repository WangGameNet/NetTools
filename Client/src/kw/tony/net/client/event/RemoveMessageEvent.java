package kw.tony.net.client.event;

public class RemoveMessageEvent {
    private int clientId;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "RemoveMessageEvent{" +
                "clientId=" + clientId +
                '}';
    }
}
