package services;

import static java.util.Collections.synchronizedSet;
import static java.util.Collections.unmodifiableSet;

import dao.NodeDao;
import entities.Node;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

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
        client = new ResteasyClientBuilder()
                .connectionCheckoutTimeout(300, TimeUnit.MILLISECONDS)
                .connectTimeout(300, TimeUnit.MILLISECONDS)
                .build();
        checkNodes();
    }

    @PreDestroy
    private void destroy() {
        client.close();
    }

    public WebTarget nodeToTarget(Node node, String path) {
        WebTarget target = client.target("http://" + node.toString());
        return path == null ? target : target.path(path);

    }

    public Set<Node> getLivingNodes() {
        return unmodifiableSet(livingNodes);
    }

    public Node getRandomLivingNode() {
        Random rnd = new Random();
        List<Node> nodes = new ArrayList<>(livingNodes);
        return nodes.get(rnd.nextInt(nodes.size()));
    }

    @Schedule(second = "*/20", minute = "*", hour = "*", persistent = false)
    public void checkNodes() {
        logger.info("Check nodes");
        for (Node node : nodes) {
            try (Response response = nodeToTarget(node, "node").request().get()
            ) {
                int statusCode = response.getStatus();
                response.close();
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
