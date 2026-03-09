package kw.tony.net.client;

import kw.tony.net.client.event.AvailableServersChangedEvent;
import kw.tony.net.client.event.ConnectionFailedEvent;
import kw.tony.net.client.event.DiscoveredServer;
import kw.tony.net.client.event.InitialWorldStateEvent;
import kw.tony.net.client.event.TestMessageEvent;
import kw.tony.net.client.event.WorldSnapshotEvent;

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
}
