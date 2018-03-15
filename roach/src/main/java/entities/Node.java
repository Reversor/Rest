package entities;

public class Node {

    private final int id;
    private String url;
    private String path;
    private int port;

    public Node(int id, String url, String path, int port) {
        this.id = id;
        this.url = url;
        this.path = path;
        this.port = port;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
        return url + ':' + port + path;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Node) {
            Node node = (Node) obj;
            return node == this || node.port == port
                    && url.equalsIgnoreCase(node.url)
                    && path.equalsIgnoreCase(node.path);
        }
        return false;
    }
}
