package enitities.listeners;

import enitities.Roach;
import javax.persistence.PostRemove;
import javax.persistence.PrePersist;
import org.jboss.logging.Logger;

public class RoachListener {

    private final Logger logger = Logger.getLogger(this.getClass());

    @PostRemove
    public void postRemove(Roach roach) {
        logger.info("Remove");
        logger.info(roach);
    }

    @PrePersist
    public void prePersist(Roach roach) {
        logger.info("Persist");
        logger.info(roach);
    }
}
