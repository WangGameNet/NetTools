package kw.tony.net.client;

import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

public interface NetworkEventSubscriber {
    void onBallInitMessage(BallInitMessage ballInitMessage);

    void onWorldMessage(WorldMessage worldMessage);

    void onTestMessage(TestMesssage testMesssage);
}
