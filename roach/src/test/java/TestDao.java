import dao.NodeDao;
import entities.Node;
import java.util.Set;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.junit.Assert;
import org.junit.Test;

public class TestDao extends Assert {

    private NodeDao nodeDao = new NodeDao();

    @Test
    public void getAll() {
        Node localhost = new Node("localhost", "roach", 8080);
        Set<Node> nodes = nodeDao.getAll();
        assertEquals(2, nodes.size());
        System.out.println(nodes);
    }

}
