package kw.tony.shared.constant.message;

public class TestMesssage extends Message{
    private String name;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LoginMesssage{" +
                "name='" + name + '\'' +
                '}';
    }
}
