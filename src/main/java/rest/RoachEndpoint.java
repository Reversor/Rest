package rest;

import enitities.Roach;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/")
public class RoachEndpoint {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private RoachService roachService;

    @GET
    @Path("/feed")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean feed() {
        return roachService.feed();
    }

    @PUT
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Roach putRoach(Roach roach) {
        logger.info(roach);
        return roachService.setRoach(roach);
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Roach getRoach() {
        return roachService.getRoach();
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Roach getRoachForName(@PathParam("name")String name) {
        return new Roach(name, (byte) 10);
    }
}
