package dao;

import entities.Node;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.h2.jdbcx.JdbcDataSource;
import org.jboss.logging.Logger;

@Singleton
public class NodeDao {

    private final Logger logger = Logger.getLogger(this.getClass());

    private JdbcDataSource ds = new JdbcDataSource();

    {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("db.properties"));
        } catch (IOException e) {

        }

        try {

            Context initContext = new InitialContext();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        ds.setUrl("jdbc:h2:~/node");
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            st.addBatch("DROP TABLE IF EXISTS NODE;");
            st.addBatch("CREATE TABLE NODE("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "url VARCHAR  NOT NULL,"
                    + "path VARCHAR NOT NULL,"
                    + "port INT DEFAULT 8080"
                    + ");"
            );
            st.addBatch(
                    "INSERT INTO NODE (url, path, port) VALUES "
                            + "('localhost', 'roach', 9417),"
                            + "('localhost', 'roach', 8080);"
            );
            st.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            logger.warn(e.getMessage());
        }
    }

    public boolean insert(Node node) {
        // TODO
        try (Connection conn = ds.getConnection();
                PreparedStatement st = conn.
                        prepareStatement("INSERT INTO NODE (url, path, port) VALUES (?,?,?)")) {
            st.setString(1, node.getUrl());
            st.setString(2, node.getPath());
            st.setInt(3, node.getPort());
            st.execute();
            return true;
        } catch (SQLException e) {
            // FIXME
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
                String url = rs.getString("url");
                String path = rs.getString("path");
                int port = rs.getInt("port");
                nodes.add(new Node(url, path, port));
            }
            return nodes;
        } catch (SQLException e) {
            // FIXME
            logger.warn(e.getMessage());
        }
        return null;
    }


}
