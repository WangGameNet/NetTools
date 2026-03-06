package kw.tony.net.client.listener;

import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

public interface ClientGameListener {
    void ballInitMessage(BallInitMessage ballInitMessage);
    void testMesssage(TestMesssage testMesssage);
    void WorldMessage(WorldMessage testMesssage);

}
