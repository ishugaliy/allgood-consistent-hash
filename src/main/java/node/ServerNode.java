package node;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class ServerNode implements Node {
    private final String dc;
    private final String ip;
    private final int port;

    public ServerNode(String ip, int port) {
        this("", ip, port);
    }

    public ServerNode(String dc, String ip, int port) {
        this.dc = dc;
        this.ip = ip;
        this.port = port;
    }

    public String getDc() {
        return dc;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getKey() {
        return String.format("%s:%s:%s", dc, ip, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServerNode)) return false;
        ServerNode that = (ServerNode) o;
        return port == that.port &&
                Objects.equals(dc, that.dc) &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dc, ip, port);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dc", dc)
                .add("ip", ip)
                .add("port", port)
                .toString();
    }
}
