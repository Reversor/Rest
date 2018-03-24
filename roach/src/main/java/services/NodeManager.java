package services;

import dao.NodeDao;
import entities.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;

@Singleton
public class NodeManager {

    @Inject
    private NodeDao nodeDao;
    private Client client;
    private Set<Node> nodes;
    private Set<Node> livingNodes;
    private Logger logger = Logger.getLogger(this.getClass());

    @PostConstruct
    private void init() {
        nodes = nodeDao.getAll();
        livingNodes = new HashSet<>();
        client = ClientBuilder.newClient();
    }

    public Set<Node> getLivingNodes() {
        return Collections.unmodifiableSet(livingNodes);
    }

    @PreDestroy
    private void destroy() {
        client.close();
    }

    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void checkNodes() {
        logger.info("Check nodes");
        nodes.forEach(node -> {
            try {
                int statusCode = client.target("http://" + "httpbin.org")//node.getUrl())
                        .path("get").request(MediaType.TEXT_PLAIN).get().getStatus();
                // FIXME
                logger.info("Status: " + Status.fromStatusCode(statusCode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
