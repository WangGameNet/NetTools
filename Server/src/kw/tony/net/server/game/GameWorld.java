package kw.tony.net.server.game;

import com.badlogic.gdx.utils.Array;
import kw.tony.net.server.ball.GameBallInfo;
import kw.tony.shared.constant.Constant;

public class GameWorld {
    private Array<GameBallInfo> ballInfos;
    private float time;

    public GameWorld(){
        ballInfos = new Array<>();
    }

    /**
     * init  ball
     */
    public void startGame(){
        for (int i = 0; i < 10; i++) {
            GameBallInfo gameBallInfo = new GameBallInfo();
            gameBallInfo.setId(i);
            float startX = randomPosition();
            float startY = randomPosition();
            gameBallInfo.setPosition(startX, startY);
            gameBallInfo.setTarget(startX, startY);
            ballInfos.add(gameBallInfo);
        }
    }

    public Array<GameBallInfo> getBallInfos() {
        return ballInfos;
    }

    public void update(float delta){
        time += delta;
        while (time >= Constant.BALL_MOVE_INTERVAL_SECONDS) {
            time -= Constant.BALL_MOVE_INTERVAL_SECONDS;
            for (GameBallInfo gameBallInfo : ballInfos) {
                gameBallInfo.setPosition(gameBallInfo.getCurrentX(), gameBallInfo.getCurrentY());
                gameBallInfo.setTarget(randomPosition(), randomPosition());
            }
        }

        float progress = time / Constant.BALL_MOVE_INTERVAL_SECONDS;
        for (GameBallInfo gameBallInfo : ballInfos) {
            gameBallInfo.calBallCurrentPos(progress);
        }
    }

    private float randomPosition() {
        return (float) (Math.random() * 720);
    }
}
