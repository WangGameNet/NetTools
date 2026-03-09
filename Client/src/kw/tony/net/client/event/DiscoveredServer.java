package kw.tony.net.client.event;

public class DiscoveredServer {
    private final String name;
    private final String host;
    private final int tcpPort;
    private final int udpPort;

    public DiscoveredServer(String name, String host, int tcpPort, int udpPort) {
        this.name = name;
        this.host = host;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public String getDisplayAddress() {
        return host + ":" + tcpPort;
    }
}
