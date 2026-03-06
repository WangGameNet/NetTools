package kw.tony.net.server.event;

public class TestMessagePayload {
    private final int value;
    private final String name;

    public TestMessagePayload(int value, String name) {
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
