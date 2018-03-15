package services;

import entities.Roach;
import exceptions.RoachNotFoundException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jboss.logging.Logger;

@Singleton
public class RoachService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private Roach roach;

    public boolean feed() {
        logger.info("Try eat");
        byte fill = roach.getFill();
        if (fill < 10) {
            roach.setFill(++fill);
            logger.info(this);
            logger.info("eat up");
            return true;
        }
        logger.info("no hunger");
        return false;
    }

    public void lure() {
        // FIXME
    }

    public Roach kick() throws RoachNotFoundException {
        if (roach == null || roach.getName() == null) {
            throw new RoachNotFoundException();
        }
        byte fill = roach.getFill();
        if (fill > 0) {
            roach.setFill(--fill);
            return roach;
        }
        return roach;
    }

    public Roach find() throws RoachNotFoundException {
        if (roach != null && roach.getName() != null) {
            return roach;
        }
        throw new RoachNotFoundException("Roach not found");
    }

    public Roach get() {
        try {
            return find();
        } catch (RoachNotFoundException e) {
            return roach = new Roach("Zhenya", (byte) 0);
        }
    }
}
