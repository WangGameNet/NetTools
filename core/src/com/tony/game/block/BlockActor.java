package com.tony.game.block;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.kw.gdx.asset.Asset;

public class BlockActor extends Group {
    private Image block;
    public BlockActor(){
        block = new Image(Asset.getAsset().getTexture("white.png"));
        addActor(block);
        setSize(100,100);
        block.setSize(getWidth(),getHeight());
        block.setPosition(50,50, Align.center);
    }

    public void hit(){

    }

    public void updatePosition(float x, float y) {

    }
}
