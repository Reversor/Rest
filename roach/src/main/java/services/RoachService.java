package services;

import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;

@Singleton
public class RoachService {

    private Roach roach;
    private Client client;
    @Inject
    private NodeManager nodeManager;
    private Logger logger = Logger.getLogger(this.getClass());

    {
        client = ClientBuilder.newClient();
    }

    public boolean feed() {
        byte fill = roach.getFill();
        if (fill < 10) {
            roach.setFill(++fill);
            return true;
        }
        return false;
    }

    public Roach lure() throws CockroachException {
        byte fill = 0;
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
        byte fill = 0;
        checkRoach();
        if ((fill = roach.getFill()) > 0) {
            try {
                roach.setFill(--fill);
                Node randomNode = nodeManager.getRandomLivingNode();
                nodeManager.sendRoachToNode(randomNode, roach);
                logger.info("Cockroach has been kicked");
                return true;
            } finally {
                roach = null;
            }
        }
        return false;
    }

    public Roach checkRoach() throws CockroachException {
        if (roach != null && roach.getName() != null) {
            return roach;
        }
        throw new CockroachException("Roach not found");
    }

    public boolean setRoach(Roach roach) {
        if (this.roach != null) {
            return false;
        }
        this.roach = roach;
        return true;
    }

    public Roach get() {
        try {
            return checkRoach();
        } catch (CockroachException cockroachException) {
            Set<Node> nodes = nodeManager.getLivingNodes();
            Roach roach = null;
            for (Node node : nodes) {
                try {
                    roach = client.target("http://" + node.getUrl() + ':' + node.getPort())
                            .path(node.getPath()).path("node")
                            .request(MediaType.APPLICATION_JSON)
                            .get(Roach.class);
                    if (roach != null) {
                        break;
                    }
                } catch (Exception exception) {
                    logger.warn(exception.getMessage());
                }
            }
            if (roach != null) {
                return roach;
            } else {
                return this.roach = new Roach("Zhenya", (byte) 0);
            }
        }
    }
}
