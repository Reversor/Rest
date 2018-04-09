package dao;

import entities.Node;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.jboss.logging.Logger;

@Singleton
public class NodeDao {

    private final Logger logger = Logger.getLogger(this.getClass());

    private DataSource ds;
    private String id;

    {
        try {
            Properties prop = new Properties();
            prop.load(this.getClass().getResourceAsStream("/nodes.properties"));
            id = prop.getProperty("id");

            InitialContext cxt = new InitialContext();
            ds = (DataSource) cxt.lookup(prop.getProperty("database.jndi-name"));
        } catch (NamingException e) {
            logger.warn(e.getMessage());
            logger.info("Set default ds");
            ds = new JdbcDataSource();
            ((JdbcDataSource) ds).setUrl("jdbc:h2:~/node");
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            st.addBatch("DROP TABLE IF EXISTS NODE;");
            st.addBatch("CREATE TABLE NODE("
                    + "id VARCHAR PRIMARY KEY,"
                    + "url VARCHAR  NOT NULL,"
                    + "path VARCHAR NOT NULL,"
                    + "port INT DEFAULT 80"
                    + ");"
            );
            st.addBatch(
                    "INSERT INTO NODE (id, url, path, port) VALUES "
                            + "('6ad226c9e088a69ab56e88e7d5b93344', 'localhost', 'roach', 9417),"
                            + "('473f9f49e4927c53ada40075c1605aef', 'localhost', 'roach', 8080);"
            );
            st.executeBatch();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean insert(Node node) {
        try (Connection conn = ds.getConnection();
                PreparedStatement st = conn.
                        prepareStatement("INSERT INTO NODE (url, path, port) VALUES (?,?,?)")) {
            st.setString(1, node.getHost());
            st.setString(2, node.getPath());
            st.setInt(3, node.getPort());
            st.execute();
            return true;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
            return false;
        }
    }

    public Set<Node> getAll() {
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM NODE");
            Set<Node> nodes = new HashSet<>();
            while (rs.next()) {
                if (id.equals(rs.getString("id"))) {
                    continue;
                }
                String url = rs.getString("url");
                String path = rs.getString("path");
                int port = rs.getInt("port");
                nodes.add(new Node(url, path, port));
            }
            return nodes;
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }


}
