package kw.tony.shared.constant.message;

public class TestMesssage extends Message{
    private String name;

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
