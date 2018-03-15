import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import rest.RoachEndpoint;


@ApplicationPath("/")
public class JAXActivator extends Application {

    public JAXActivator() {
        init();
    }

    private void init() {
        SwaggerConfiguration configuration = new SwaggerConfiguration();
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(RoachEndpoint.class);
        resources.add(OpenApiResource.class);
        return resources;
    }

}
