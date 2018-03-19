package rest;

import entities.Roach;
import exceptions.RoachException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(info = @Info(title = "Roach Heaven"))
public class RoachEndpoint implements CockroachEndpoint {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private RoachService roachService;

    @GET
    @Path("/lure")
    @Operation(summary = "Lure cockroach")
    @ApiResponse(responseCode = "200", description = "roach lured")
    @Override
    public Roach lure() {
        // FIXME Node works
        return null;
    }

    @PATCH
    @Path("/feed")
    @Override
    public Response feed() {
        if (roachService.feed()) {
            return Response.ok().entity("eat up").build();
        }
        return Response.ok().entity("no hunger").build();
    }

    @DELETE
    @Path("/kick")
    @Operation(
            description = "Kick cockroach from current node in a random destination",
            method = HttpMethod.DELETE
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "roach was kicked"),
            @ApiResponse(responseCode = "404", description = "roach not found")
    })
    @Override
    public Response kick() {
        try {
            roachService.kick();
            return Response.ok("roach was kicked").build();
        } catch (RoachException e) {
            return Response.status(Status.NOT_FOUND).entity("roach not found").build();
        }
    }

    @GET
    @Path("/get")
    @Override
    public Roach get() {
        return roachService.get();
    }

}


