package kw.tony.net.server.game;

import com.badlogic.gdx.utils.Array;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.shared.constant.bean.BallInfo;

public class GameWorld {
    private static GameWorld instance;
    private Array<GameBallInfo> ballInfos;
    private GameWorld(){
        ballInfos = new Array<>();
    }

    public void startGame(){
        for (int i = 0; i < 10; i++) {
            GameBallInfo gameBallInfo = new GameBallInfo();
            gameBallInfo.setId(i);
            gameBallInfo.setX((float) (Math.random() * 720));
            gameBallInfo.setY((float) (Math.random() * 720));
            ballInfos.add(gameBallInfo);
        }
    }

    public static GameWorld getInstance() {
        if (instance == null) {
            instance = new GameWorld();
        }
        return instance;
    }

    public Array<GameBallInfo> getBallInfos() {
        return ballInfos;
    }

    private float time;

    public void update(float delta){
        time += delta;
        if (time > 1.5f) {
            time = 0;
            for (GameBallInfo gameBallInfo : ballInfos) {
                gameBallInfo.setX(gameBallInfo.getCurrentX());
                gameBallInfo.setY(gameBallInfo.getCurrentY());
                gameBallInfo.setTargetX((float) (Math.random() * 720));
                gameBallInfo.setTargetY((float) (Math.random() * 720));
            }
        }else {
            for (GameBallInfo gameBallInfo : ballInfos) {
                gameBallInfo.calBallCurrentPos(time);
            }
        }
    }
}
