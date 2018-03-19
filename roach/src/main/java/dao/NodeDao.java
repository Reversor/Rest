package dao;

import entities.Message;
import entities.Node;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Singleton;
import org.h2.jdbcx.JdbcDataSource;

@Singleton
public class NodeDao {

    private JdbcDataSource ds = new JdbcDataSource();

    public NodeDao() {
        init();
    }

    public Message insert(Node node) {
        // TODO
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            String query = new StringBuilder("INSERT INTO NODE (url, path, port) VALUES (")
                    .append(node.getUrl()).append(", ")
                    .append(node.getPath()).append(", ")
                    .append(node.getPort()).append(");")
                    .toString();
            st.execute(query);
        } catch (SQLException e) {
            // FIXME
            e.printStackTrace();
        }
        return null;
    }

    public Set<Node> getAll() {
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM NODE");
            Set<Node> nodes = new HashSet<>();
            while (rs.next()) {
                String url = rs.getString("url");
                String path = rs.getString("path");
                int port = rs.getInt("port");
                nodes.add(new Node(url, path, port));
            }
            return nodes;
        } catch (SQLException e) {
            // FIXME
            e.printStackTrace();
        }
        return null;
    }

    private void init() {
        ds.setUrl("jdbc:h2:~/node");
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            st.addBatch("DROP TABLE IF EXISTS NODE;");
            st.addBatch("CREATE TABLE NODE("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "url VARCHAR  NOT NULL,"
                    + "path VARCHAR NOT NULL,"
                    + "port INT DEFAULT 8080"
                    + ");");
            st.addBatch(
                    "INSERT INTO NODE (url, path, port) VALUES ('localhost', 'roach', 8080);"
            );
            conn.commit();
        } catch (SQLException e) {

        }
    }
}
