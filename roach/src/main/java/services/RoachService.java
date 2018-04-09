package services;

import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.SyncInvoker;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Singleton
public class RoachService {

    private Logger logger = Logger.getLogger(this.getClass());

    private Roach roach;
    private Client client;
    @Inject
    private NodeManager nodeManager;

    private long createdTime = Long.MAX_VALUE;

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
                return true;
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

    public Roach checkOlderCockroach() {
        Set<Node> nodes = nodeManager.getLivingNodes();
//        TODO WebTarget, всякий треш
        List<Builder> requestBuilders = nodes.stream().map(node ->
                client.target("http://" + node.getHost() + ':' + node.getPort())
                        .path(node.getPath()).path("node").path("roach").request())
                .collect(Collectors.toList());
        Map<Long, Builder> builderMap = requestBuilders.stream().collect(Collectors.toMap(
                builder -> Long.valueOf(builder.get().getHeaderString("created")),
                builder -> builder));
        long older = Collections.min(builderMap.keySet());
        Builder requestBuilderToNodeWithOlderCockroach = builderMap.remove(older);
        builderMap.values().forEach(SyncInvoker::delete);
        return requestBuilderToNodeWithOlderCockroach.accept(MediaType.APPLICATION_JSON)
                .get(Roach.class);
    }

    public boolean setRoach(Roach roach) {
        if (get() != null) {
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
                    Response response = client
                            .target("http://" + node.getHost() + ':' + node.getPort())
                            .path(node.getPath()).path("node").path("roach").request().get();
                    long time = Long.valueOf(response.getHeaderString("created"));
                    roach = response.readEntity(Roach.class);
                    if (roach != null) {
                        break;
                    }
                } catch (ClientErrorException exception) {
                    logger.warn("Client exception: " + exception.getMessage());
                }
            }
            if (roach != null) {
                return roach;
            } else {
                createdTime = System.currentTimeMillis();
                return this.roach = new Roach("Zhenya", (byte) 0);
            }
        }
    }

    public long getCreatedTime() {
        return createdTime;
    }
}
