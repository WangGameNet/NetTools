package kw.tony.shared.constant.register;

import com.esotericsoftware.kryo.Kryo;
import kw.tony.shared.constant.bean.BallInfo;
import kw.tony.shared.constant.bean.CollectibleInfo;
import kw.tony.shared.constant.bean.ScoreInfo;
import kw.tony.shared.constant.message.BallInitMessage;
import kw.tony.shared.constant.message.RemoveMessage;
import kw.tony.shared.constant.message.TestMesssage;
import kw.tony.shared.constant.message.WorldMessage;

import java.util.ArrayList;

public class ClassRegister {
    public static void register(Kryo kryo){
        kryo.register(ArrayList.class);
        kryo.register(BallInfo.class);
        kryo.register(CollectibleInfo.class);
        kryo.register(ScoreInfo.class);
        kryo.register(RemoveMessage.class);

        kryo.register(TestMesssage.class);
        kryo.register(WorldMessage.class);
        kryo.register(BallInitMessage.class);

    }
}
