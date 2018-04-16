import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import rest.NodeEndpoint;
import rest.RoachEndpoint;


@ApplicationPath("/")
public class JAXActivator extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(OpenApiResource.class);
        resources.add(RoachEndpoint.class);
        resources.add(NodeEndpoint.class);
        return resources;
    }

}
