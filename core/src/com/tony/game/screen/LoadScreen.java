package com.tony.game.screen;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.kw.gdx.BaseGame;
import com.kw.gdx.clip.Constant;
import com.kw.gdx.screen.BaseScreen;
import com.tony.game.constant.GameConstant;
import kw.tony.net.client.ClientMain;
import kw.tony.shared.constant.message.LoginMesssage;

public class LoadScreen extends BaseScreen {
    public LoadScreen(BaseGame game) {
        super(game);
    }

    @Override
    public void initView() {
        super.initView();
        GameConstant.clientMain = ClientMain.getInstant();

        stage.addAction(Actions.forever(
                Actions.sequence(
                        Actions.delay(2),
                        Actions.run(()->{
                            LoginMesssage loginMesssage = new LoginMesssage();
                            loginMesssage.setName("zzzzzzzzzzzzzzz");
                            GameConstant.clientMain.sendTCP(loginMesssage);
                        })
                )
        ));
    }
}
