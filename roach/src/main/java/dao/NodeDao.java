package dao;

import entities.Node;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
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

    {
        List<String> stringNodes;
        Set<Node> nodes;
        try {
            InitialContext cxt = new InitialContext();
            ds = (DataSource) cxt.lookup("java:jboss/nodes");

            // FIXME First variant properties
            FileInputStream fis = new FileInputStream("nodes.properties");
            Properties prop = new Properties();
            prop.load(fis);
            stringNodes = Arrays.asList(prop.getProperty("nodes").split(","));

            // FIXME Second variant csv
            stringNodes = Files.readAllLines(Paths.get("nodes.csv"));
            // and then parse

            // FIXME Third variant csv
            nodes = Files.lines(Paths.get("nodes.csv")).map( string -> {
                String[] buf = string.split(",");
                return new Node(buf[0], buf[2], Integer.parseInt(buf[1]));
            }).collect(Collectors.toSet());

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
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "url VARCHAR  NOT NULL,"
                    + "path VARCHAR NOT NULL,"
                    + "port INT DEFAULT 80"
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
