package services;

import dao.NodeDao;
import entities.Node;
import entities.Roach;
import exceptions.RoachException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;

@Singleton
public class RoachService {

    private Client nodeClient;

    @Inject
    private Roach roach;

    @Inject
    private NodeDao nodeDao;

    public boolean feed() {
        byte fill = roach.getFill();
        if (fill < 10) {
            roach.setFill(++fill);
            return true;
        }
        return false;
    }

    public Roach lure() throws RoachException {
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
        // FIXME
    }

    public boolean kick() throws RoachException {
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

    public Roach checkRoach() throws RoachException {
        if (roach != null && roach.getName() != null) {
            return roach;
        }
        throw new RoachException("Roach not found");
    }

    public Roach get() {
        try {
            return checkRoach();
        } catch (RoachException e) {
            Set<Node> nodes = nodeDao.getAll();
            // TODO
            return roach = new Roach("Zhenya", (byte) 0);
        }
    }
}
