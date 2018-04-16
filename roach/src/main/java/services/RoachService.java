package services;

import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import exceptions.NodeException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Singleton
public class RoachService {

    private Logger logger = Logger.getLogger(this.getClass());
    private Roach roach;
    @Inject
    private NodeManager nodeManager;
    private long createdTime = Long.MAX_VALUE;

    public boolean feed() {
        byte fill = roach.getFill();
        if (fill < 10) {
            roach.setFill(++fill);
            return true;
        }
        return false;
    }

    public Roach lure() throws CockroachException {
        if (roach != null) return roach;
        Set<Node> nodes = nodeManager.getLivingNodes();
        if (nodes.isEmpty()) {
            return roach;
        }
        Iterator<Node> it = nodes.iterator();
        while (roach == null && it.hasNext()) {
            Node node = it.next();
            Response response = nodeManager.nodeToTarget(node, "node/roach").request().get();
            if (response.getStatus() == 200) {
                Roach receivedRoach = response.readEntity(Roach.class);
                byte fill = receivedRoach.getFill();
                if (fill > 0) {
                    receivedRoach.setFill(--fill);
                    this.createdTime = nodeManager.nodeToTarget(node, "node/roach/creation")
                            .request().get(Long.TYPE);
                    nodeManager.nodeToTarget(node, "node/roach").request().delete().close();
                    roach = receivedRoach;
                }
                break;
            }
        }
        if (roach == null) {
            throw new CockroachException();
        }
        return roach;
    }

    public boolean kick() throws CockroachException, NodeException {
        byte fill;
        checkRoach();
        if ((fill = roach.getFill()) > 0) {
            try {
                roach.setFill(--fill);
                Node randomNode = nodeManager.getRandomLivingNode();
                Response response = nodeManager.nodeToTarget(randomNode, "node/roach")
                        .queryParam("created", createdTime).request()
                        .post(Entity.entity(roach, MediaType.APPLICATION_JSON));
                if (response.getStatus() == 200) {
                    return true;
                }
                throw new NodeException();
            } finally {
                roach = null;
                createdTime = Long.MAX_VALUE;
            }
        }
        return false;
    }

    public Roach checkRoach() throws CockroachException {
        if (roach != null) {
            return roach;
        }
        throw new CockroachException("Roach not found");
    }

    private Roach checkOlderCockroach() {
        Set<Node> nodes = nodeManager.getLivingNodes();
        if (nodes.isEmpty()) {
            return roach;
        }
        Map<Long, WebTarget> nodesWithCockroach = new HashMap<>();
        for (Node node : nodes) {
            Long created;
            try (Response response = nodeManager.nodeToTarget(node, "node/roach/creation")
                    .request().get()) {
                if (response.getStatus() == 200) {
                    created = response.readEntity(Long.TYPE);
                    nodesWithCockroach.put(created, nodeManager.nodeToTarget(node, "node/roach"));
                }
            }
        }
        if (nodesWithCockroach.isEmpty()) {
            return roach;
        }
        long olderCockroachCreatedTime = Collections.min(nodesWithCockroach.keySet());
        if (olderCockroachCreatedTime >= createdTime) {
            nodesWithCockroach.values()
                    .forEach(webTarget -> webTarget.request().delete());
            return roach;
        }
        killRoach();
        WebTarget nodeWithOlderCockroach = nodesWithCockroach.get(olderCockroachCreatedTime);
        if (nodeWithOlderCockroach != null) {
            nodesWithCockroach.remove(olderCockroachCreatedTime);
            nodesWithCockroach.values()
                    .forEach(webTarget -> webTarget.request().delete());
            return nodeWithOlderCockroach.request()
                    .accept(MediaType.APPLICATION_JSON).get(Roach.class);
        }
        return roach;
    }

    public boolean setRoach(Roach roach, long createdTime) {
        if (roach == null) {
            return false;
        }
        this.createdTime = createdTime;
        this.roach = roach;
        return true;
    }

    public boolean killRoach() {
        if (roach == null) {
            return false;
        }
        createdTime = Long.MAX_VALUE;
        this.roach = null;
        return true;
    }

    public Roach get() {
        //FIXME
        try {
            if (roach == null) {
                throw new CockroachException();
            }
            return checkOlderCockroach();
        } catch (CockroachException cockroachException) {
            Roach roach = checkOlderCockroach();
            if (roach != null) {
                return roach;
            } else {
                createdTime = System.currentTimeMillis();
                Properties prop = new Properties();
                String name;
                try {
                    prop.load(this.getClass().getResourceAsStream("/roach.properties"));
                    name = prop.getProperty("name");
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                    name = "Alenya";
                }
                return this.roach = new Roach(name, (byte) 0);
            }
        }
    }

    public long getCreatedTime() {
        return createdTime;
    }
}
