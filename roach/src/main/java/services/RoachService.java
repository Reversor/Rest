package services;

import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
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
        byte fill;
        checkRoach();
        if ((fill = roach.getFill()) > 0) {
            try {
                roach.setFill(--fill);
                return roach;
            } finally {
                roach = null;
            }
        }
        return null;
    }

    public boolean kick() throws CockroachException {
        byte fill;
        checkRoach();
        if ((fill = roach.getFill()) > 0) {
            try {
                roach.setFill(--fill);
                Node randomNode = nodeManager.getRandomLivingNode();
                return nodeManager.nodeToTarget(randomNode).path("node")
                        .request()
                        .post(Entity.entity(roach, MediaType.APPLICATION_JSON)).getStatus() == 200;
            } finally {
                roach = null;
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
        // checked
        if (nodes.isEmpty()) {
            return roach;
        }
        Map<Long, Node> nodesWithCockroach = new HashMap<>();
        for (Node node : nodes) {
            String createdStr = nodeManager.nodeToTarget(node).path("node/roach").request().get()
                    .getHeaderString("created");
            if (createdStr != null) {
                nodesWithCockroach.put(Long.valueOf(createdStr), node);
            }
        }
        // checked
        if (nodesWithCockroach.isEmpty()) {
            return roach;
        }
        long olderCockroachCreatedTime = Collections.min(nodesWithCockroach.keySet());
        if (olderCockroachCreatedTime >= createdTime) {
            nodesWithCockroach.values().stream().map(nodeManager::nodeToTarget)
                    .forEach(webTarget -> webTarget.path("node/roach").request().delete());
            return roach;
        }
        killRoach();
        Node nodeWithOlderCockroach = nodesWithCockroach.get(olderCockroachCreatedTime);
        if (nodeWithOlderCockroach != null) {
            nodesWithCockroach.remove(olderCockroachCreatedTime);
            nodesWithCockroach.values().stream().map(nodeManager::nodeToTarget)
                    .forEach(webTarget -> webTarget.path("node/roach").request().delete());
            return nodeManager.nodeToTarget(nodeWithOlderCockroach).request()
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
        try {
            if (roach == null) {
                throw new CockroachException();
            }
            Roach roach = checkOlderCockroach();
            return this.roach;
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
