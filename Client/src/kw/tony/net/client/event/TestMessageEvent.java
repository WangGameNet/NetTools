package kw.tony.net.client.event;

public class TestMessageEvent {
    private final int value;
    private final String name;

    public TestMessageEvent(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
