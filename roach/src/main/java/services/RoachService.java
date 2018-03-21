package services;

import dao.NodeDao;
import entities.Node;
import entities.Roach;
import exceptions.CockroachException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoachService {

    @Inject
    NodeManager nodeManager;
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
        // FIXME
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
            Set<Node> nodes = nodeDao.getAll();
            // TODO
            return roach = new Roach("Zhenya", (byte) 0);
        }
    }
}
