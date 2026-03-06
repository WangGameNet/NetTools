package kw.tony.net.client;

import kw.tony.net.client.event.InitialWorldStateEvent;
import kw.tony.net.client.event.TestMessageEvent;
import kw.tony.net.client.event.WorldSnapshotEvent;

public interface NetworkEventSubscriber {
    default void onConnected() {
    }

    default void onDisconnected() {
    }

    default void onReconnecting() {
    }

    default void onInitialWorldState(InitialWorldStateEvent initialWorldStateEvent) {
    }

    default void onWorldSnapshot(WorldSnapshotEvent worldSnapshotEvent) {
    }

    default void onTestMessage(TestMessageEvent testMessageEvent) {
    }
}
