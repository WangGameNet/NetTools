package kw.tony.net.client;

import kw.tony.net.client.event.*;

public interface NetworkEventSubscriber {
    default void onAvailableServersChanged(AvailableServersChangedEvent availableServersChangedEvent) {
    }

    default void onConnecting(DiscoveredServer discoveredServer) {
    }

    default void onConnected() {
    }

    default void onDisconnected() {
    }

    default void onReconnecting() {
    }

    default void onConnectionFailed(ConnectionFailedEvent connectionFailedEvent) {
    }

    default void onInitialWorldState(InitialWorldStateEvent initialWorldStateEvent) {
    }

    default void onWorldSnapshot(WorldSnapshotEvent worldSnapshotEvent) {
    }

    default void onTestMessage(TestMessageEvent testMessageEvent) {
    }

    default void onRemoveMessage(RemoveMessageEvent removeMessageEvent){}
}
