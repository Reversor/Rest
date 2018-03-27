package services;

import static java.util.Collections.synchronizedSet;
import static java.util.Collections.unmodifiableSet;

import dao.NodeDao;
import entities.Node;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
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
        livingNodes = synchronizedSet(new HashSet<>());
        client = ClientBuilder.newClient();
    }

    @PreDestroy
    private void destroy() {
        client.close();
    }

    public Set<Node> getLivingNodes() {
        return synchronizedSet(unmodifiableSet(livingNodes));
    }

    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void checkNodes() {
        logger.info("Check nodes");
        for (Node node : nodes) {
            try {
                int statusCode = client.target("http://" + node.getUrl() + ':' + node.getPort())
                        .path(node.getPath()).path("node").request().get().getStatus();
                if (statusCode == Status.OK.getStatusCode()) {
                    logger.info(node.toString() + " alive");
                    livingNodes.add(node);
                } else {
                    logger.info(node.toString() + " dead");
                    livingNodes.remove(node);
                }
            } catch (Exception e) {
                logger.warn(node.toString() + " dead with message:" + e.getMessage());
                livingNodes.remove(node);
            }
        }
    }
}
