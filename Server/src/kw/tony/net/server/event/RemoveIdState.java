package kw.tony.net.server.event;

public class RemoveIdState {
    public int removeId;

    public int getRemoveId() {
        return removeId;
    }

    public void setRemoveId(int removeId) {
        this.removeId = removeId;
    }

    @Override
    public String toString() {
        return "RemoveMessage{" +
                "removeId=" + removeId +
                '}';
    }
}
