package entities;

public class Node {

    private String host;
    private String path;
    private int port;

    public Node(String host, String path, int port) {
        this.host = host;
        this.path = path;
        this.port = port;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    public void setPort(short port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return host + ':' + port + '/' + path;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            return node == this || node.port == port
                    && host.equalsIgnoreCase(node.host)
                    && path.equalsIgnoreCase(node.path);
        }
        return false;
    }
}
