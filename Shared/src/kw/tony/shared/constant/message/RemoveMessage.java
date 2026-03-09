package kw.tony.shared.constant.message;

public class RemoveMessage {
    private int clientId;

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "RemoveMessage{" +
                "clientId=" + clientId +
                '}';
    }
}
