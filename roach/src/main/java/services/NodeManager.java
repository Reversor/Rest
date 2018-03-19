package services;

import dao.NodeDao;
import entities.Node;
import java.util.Set;
import javax.ejb.Schedule;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.logging.Logger;

@Singleton
public class NodeManager {

    @Inject
    NodeDao nodeDao;
    Client client;
    private Logger logger = Logger.getLogger(this.getClass());

    public NodeManager() {
        init();
    }

    private void init() {
        client = ClientBuilder.newClient();
    }

    @Schedule(second = "*/20")
    public void checkNodes() {
        Set<Node> nodes = nodeDao.getAll();
        logger.info("Check nodes");
        logger.info(nodes);
        // TODO Http-client neededl
    }
}
