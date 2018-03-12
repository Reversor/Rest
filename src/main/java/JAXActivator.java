import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import rest.NuggetEndpoint;
import rest.RoachEndpoint;

@ApplicationPath("/")
public class JAXActivator extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(NuggetEndpoint.class);
        resources.add(RoachEndpoint.class);
        return resources;
    }
}