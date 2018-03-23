package services;

import dao.NodeDao;
import entities.Node;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import org.jboss.logging.Logger;

@Singleton
public class NodeManager {

    @Inject
    private NodeDao nodeDao;
    private Client client;
    private Set<Node> nodes;
    private Logger logger = Logger.getLogger(this.getClass());

    @PostConstruct
    private void init() {
        client = ClientBuilder.newClient();
        nodes = nodeDao.getAll();
        logger.debug("init");
        checkNodes();
    }

    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void checkNodes() {
        logger.info("Check nodes");
//        Client client = ClientBuilder.newClient();
//        WebTarget target = client.target("http://localhost:11327/roach/get");
//        logger.info(target.request().buildGet().invoke().getEntity());
//        client.close();
        logger.info(nodes);
        // TODO Http-client needed
    }
}
