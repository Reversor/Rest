package services;

import dao.NodeDao;
import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

@Singleton
public class RoachService {

    private Roach roach;
    private Client client;
    @Inject
    private NodeManager nodeManager;
    @Inject
    private NodeDao nodeDao;

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
                // FIXME
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
                // FIXME
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

    public Roach get() {
        try {
            return checkRoach();
        } catch (CockroachException e) {
            Set<Node> nodes = nodeManager.getLivingNodes();
            Roach roach = null;
            for (Node node : nodes) {
                try {
                    roach = client.target("http://" + node.getUrl())
                            .path(node.getPath())
                            .request(MediaType.APPLICATION_JSON)
                            .get(Roach.class);
                    if (roach != null) {
                        break;
                    }
                } catch (ClientErrorException clientException) {
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
