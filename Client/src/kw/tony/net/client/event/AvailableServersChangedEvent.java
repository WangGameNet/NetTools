package kw.tony.net.client.event;

import java.util.List;

public class AvailableServersChangedEvent {
    private final List<DiscoveredServer> servers;

    public AvailableServersChangedEvent(List<DiscoveredServer> servers) {
        this.servers = servers;
    }

    public List<DiscoveredServer> getServers() {
        return servers;
    }
}
