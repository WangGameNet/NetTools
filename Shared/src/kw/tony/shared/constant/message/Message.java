package kw.tony.shared.constant.message;

public class Message {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Message{" +
                "value=" + value +
                '}';
    }
}
