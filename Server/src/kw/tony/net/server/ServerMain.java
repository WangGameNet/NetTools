package kw.tony.net.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Server;
import com.kw.gdx.utils.log.NLog;
import kw.tony.net.server.listener.ServerListener;
import kw.tony.net.server.manager.ServerManager;
import kw.tony.shared.constant.Constant;
import kw.tony.shared.constant.message.TestMesssage;

import java.io.IOException;

public class ServerMain extends ApplicationAdapter {
    private ServerManager serverManager;

    @Override
    public void create() {
        super.create();
        serverManager = ServerManager.getInstance();
    }

    @Override
    public void render() {
        super.render();
        serverManager.update(Gdx.graphics.getDeltaTime());
    }
}
