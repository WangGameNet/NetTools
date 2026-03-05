package com.tony.game;

import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kw.gdx.BaseGame;
import com.kw.gdx.constant.Constant;
import com.kw.gdx.resource.annotation.GameInfo;
import com.kw.gdx.utils.log.NLog;
import com.tony.game.constant.GameConstant;
import com.tony.game.screen.LoadScreen;
import kw.tony.net.client.ClientMain;

@GameInfo(width = 1080, height = 1920, batch = Constant.COUPOLYGONBATCH)
public class GameMain extends BaseGame {

    public GameMain() {

    }

    @Override
    public void create() {
        super.create();
        NLog.isLog = false;
        Constant.viewColor.set(0.f, 0.f, 0.0f, 1.0f);
        NLog.i("create -->");
    }

    @Override
    protected void loadingView() {
        super.loadingView();
        setScreen(LoadScreen.class);
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
    }
}
