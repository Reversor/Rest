package services;

import dao.NodeDao;
import entities.Node;
import entities.Roach;
import exceptions.RoachException;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RoachService {

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

    public void lure() {
        // FIXME
    }

    public boolean kick() throws RoachException {
        if (roach == null || roach.getName() == null) {
            throw new RoachException();
        }
        byte fill = roach.getFill();
        if (fill > 0) {
            roach.setFill(--fill);
            return true;
        }
        return false;
    }

    public Roach find() throws RoachException {
        if (roach != null && roach.getName() != null) {
            Set<Node> nodes = nodeDao.getAll();
            return roach;
        }
        throw new RoachException("Roach not found");
    }

    public Roach get() {
        try {
            return find();
        } catch (RoachException e) {
            return roach = new Roach("Zhenya", (byte) 0);
        }
    }
}
