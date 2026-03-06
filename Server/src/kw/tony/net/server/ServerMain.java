package kw.tony.net.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import kw.tony.net.server.manager.ServerManager;
import kw.tony.net.server.game.GameWorld;

public class ServerMain extends ApplicationAdapter {
    private ServerNetworkService serverNetworkService;
    private ServerManager serverManager;

    @Override
    public void create() {
        super.create();
        serverNetworkService = new ServerNetworkService();
        serverManager = new ServerManager(serverNetworkService, new GameWorld());
        serverNetworkService.subscribe(serverManager);
        serverNetworkService.start();
    }

    @Override
    public void render() {
        super.render();
        serverNetworkService.update();
        serverManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
        if (serverNetworkService != null) {
            serverNetworkService.unsubscribe(serverManager);
            serverNetworkService.stop();
        }
        super.dispose();
    }
}
