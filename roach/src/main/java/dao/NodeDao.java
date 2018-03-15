package dao;

import entities.Message;
import entities.Node;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;
import org.h2.jdbcx.JdbcDataSource;

@Singleton
public class NodeDao {

    private JdbcDataSource ds = new JdbcDataSource();

    public NodeDao() {
        init();
    }

    public Message insert() {
        // TODO
        /*try () {

        } catch (SQLException e) {

        }*/
        return null;
    }

    public List<Node> getAll() {
        try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM NODE");
            List<Node> nodeList = new ArrayList<>();
            while (rs.next()) {
                int id = rs.getInt(1);
                String url = rs.getString(2);
                String path = rs.getString(3);
                int port = rs.getInt(4);
                nodeList.add(new Node(id, url, path, port));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void init() {
        ds.setUrl("jdbc:h2:~/node");
        /*try (Connection conn = ds.getConnection();
                Statement st = conn.createStatement()) {
            // TODO init DB
            ResultSet rs = st.executeQuery("CREATE TABLE IF NOT EXISTS node("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "url VARCHAR  NOT NULL,"
                    + "path VARCHAR NOT NULL,"
                    + "port INT DEFAULT 8080"
                    + ")");
        } catch (SQLException e) {

        }*/
    }
}
