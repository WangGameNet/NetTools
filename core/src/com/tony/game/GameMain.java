package com.tony.game;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kw.gdx.BaseGame;
import com.kw.gdx.constant.Constant;
import com.kw.gdx.resource.annotation.GameInfo;
import com.kw.gdx.utils.log.NLog;
import com.tony.game.screen.ConnectScreen;
import com.tony.game.screen.LoadScreen;
import kw.tony.net.client.NetworkService;
import kw.tony.net.client.NetworkServiceProvider;

@GameInfo(width = 1080, height = 1920, batch = Constant.COUPOLYGONBATCH)
public class GameMain extends BaseGame implements NetworkServiceProvider {
    private final NetworkService networkService = new NetworkService();

    public GameMain() {

    }

    @Override
    public void create() {
        networkService.start();
        super.create();
        NLog.isLog = false;
        Constant.viewColor.set(0.f, 0.f, 0.0f, 1.0f);
        NLog.i("create -->");
    }

    @Override
    public void render() {
        networkService.update();
        super.render();
    }

    @Override
    protected void loadingView() {
        super.loadingView();
        setScreen(ConnectScreen.class);
    }

    @Override
    protected void initViewport() {
        stageViewport = new ExtendViewport(Constant.WIDTH, Constant.HIGHT);
        Constant.camera = stageViewport.getCamera();
        Constant.camera.far = 7000;
        NLog.i("stageViewport :" + Constant.WIDTH + "," + Constant.HIGHT);
        NLog.i("camera far :" + 5000);
    }

    @Override
    public void resume() {
        super.resume();
    }

    @Override
    protected void otherDispose() {
        networkService.dispose();
    }

    @Override
    public NetworkService getNetworkService() {
        return networkService;
    }
}
