package services;

import static java.util.Collections.synchronizedSet;
import static java.util.Collections.unmodifiableSet;

import dao.NodeDao;
import entities.Node;
import entities.Roach;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;

@Singleton
public class NodeManager {

    private Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private NodeDao nodeDao;
    private Client client;
    private Set<Node> nodes;
    private Set<Node> livingNodes;
    @PostConstruct
    private void init() {
        nodes = nodeDao.getAll();
        livingNodes = synchronizedSet(new HashSet<>());
        client = ClientBuilder.newClient();
        // Cockroach synchronization
        checkNodes();
    }

    @PreDestroy
    private void destroy() {
        client.close();
    }

    public Set<Node> getLivingNodes() {
        return unmodifiableSet(livingNodes);
    }

    public Node getRandomLivingNode() {
        Random rnd = new Random();
        List<Node> nodes = new ArrayList<>(livingNodes);
        return nodes.get(rnd.nextInt(nodes.size()));
    }

    public boolean sendRoachToNode(Node node, Roach roach) {
        Response response = client
                .target("http://" + node.getHost() + ':' + node.getPort()).path(node.getPath())
                .path("node")
                .request()
                .post(Entity.entity(roach, MediaType.APPLICATION_JSON));
        return response.getStatus() == 200;
    }

    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void checkNodes() {
        logger.info("Check nodes");
        for (Node node : nodes) {
            try {
                int statusCode = client.target("http://" + node.getHost() + ':' + node.getPort())
                        .path(node.getPath()).path("node").request().get().getStatus();
                if (statusCode == Status.OK.getStatusCode()) {
                    logger.info(node.toString() + " alive");
                    livingNodes.add(node);
                } else {
                    logger.info(node.toString() + " dead");
                    livingNodes.remove(node);
                }
            } catch (ClientErrorException e) {
                logger.warn(node.toString() + " dead with message:" + e.getMessage());
                livingNodes.remove(node);
            }
        }
    }
}
