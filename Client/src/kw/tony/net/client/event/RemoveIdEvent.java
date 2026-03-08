package kw.tony.net.client.event;

public class RemoveIdEvent {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "RemoveIdEvent{" +
                "id=" + id +
                '}';
    }
}
