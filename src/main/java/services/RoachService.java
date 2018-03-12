package services;

import enitities.Roach;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;
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

    public boolean transmit() {
        // FIXME
        byte fill = roach.getFill();
        if (fill > 0) {
            roach.setFill(--fill);
            return true;
        }
        return false;
    }

    public Roach getRoach() {
        return roach;
    }

    public Roach setRoach(Roach roach) {
        return this.roach = roach;
    }
}
