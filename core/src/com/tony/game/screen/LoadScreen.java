package com.tony.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.ArrayMap;
import com.kw.gdx.BaseGame;
import com.kw.gdx.asset.Asset;
import com.kw.gdx.screen.BaseScreen;
import com.tony.game.constant.GameConstant;
import kw.tony.net.client.ClientMain;
import kw.tony.net.client.listener.ClientGameListener;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

public class LoadScreen extends BaseScreen {
    private ArrayMap<Integer,Image> images = new ArrayMap<>();
    public LoadScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void initView() {
        super.initView();
        GameConstant.clientMain = ClientMain.getInstant();
        GameConstant.clientMain.setClientGameListener(new ClientGameListener() {
            @Override
            public void ballInitMessage(BallInitMessage ballInitMessage) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        for (BallInfo position : ballInitMessage.getPositions()) {
                            Image image = new Image(Asset.getAsset().getTexture("white.png"));
                            addActor(image);
                            image.setPosition(position.getPosx(), position.getPosy());
                            images.put(position.getBallId(), image);
                        }
                    }
                });
            }

            @Override
            public void testMesssage(TestMesssage testMesssage) {

            }

            @Override
            public void WorldMessage(WorldMessage worldMessage) {
            
                for (BallInfo position : worldMessage.getPositions()) {
                    Image image = images.get(position.getBallId());
                    if (image != null) {
                        image.setPosition(position.getPosx(), position.getPosy());
                    }
                }
            }
        });

    }
}
