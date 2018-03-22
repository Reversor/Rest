package services;

import dao.NodeDao;
import entities.Node;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.logging.Logger;

@Startup
@Singleton
public class NodeManager {

    @Inject
    NodeDao nodeDao;
    Client client;
    private Logger logger = Logger.getLogger(this.getClass());
    Set<Node> nodes;

    @PostConstruct
    private void init(){
        client = ClientBuilder.newClient();
        nodes = nodeDao.getAll();
    }

    @Schedule(second = "*/20")
    public void checkNodes() {
        logger.info("Check nodes");
        logger.info(nodes);
        // TODO Http-client needed
    }
}
