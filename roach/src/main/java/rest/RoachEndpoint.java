package rest;

import entities.Message;
import entities.Roach;
import exceptions.RoachNotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RoachEndpoint implements CockroachEndpoint {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private RoachService roachService;

    @GET
    @Path("/lure")
    @ApiResponse(responseCode = "200", description = "roach lured")
    public Roach lure() {
        // FIXME Node works
        return null;
    }

    @GET
    @Path("/feed")
    public Message feed() {
        if (roachService.feed()) {
            return new Message("eat up");
        }
        return new Message("no hunger");
    }

    @GET
    @Path("/get")
    public Roach get() {
        return roachService.get();
    }

    @DELETE
    @Path("/kick")
    public Message kick() {
        try {
            roachService.kick();
            return new Message("roach was kicked");
        } catch (RoachNotFoundException e) {
            return new Message("roach not found");
        }
    }
}
