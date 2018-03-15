package services;

import dao.NodeDao;
import javax.ejb.Schedule;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class NodeTimer {

    @Inject
    NodeDao nodeDao;

    @Schedule(second = "*/20")
    public void checkNodes() {
        // TODO
    }
}
