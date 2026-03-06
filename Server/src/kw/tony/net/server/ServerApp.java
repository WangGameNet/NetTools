package kw.tony.net.server;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

public class ServerApp {
    public static void main(String[] args) {
        HeadlessApplicationConfiguration configuration = new HeadlessApplicationConfiguration();
        configuration.updatesPerSecond = 60;
        new HeadlessApplication(new ServerMain(), configuration);
    }
}
