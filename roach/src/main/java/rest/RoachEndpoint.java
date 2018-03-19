package rest;

import entities.Roach;
import exceptions.RoachException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/")
@OpenAPIDefinition(info = @Info(title = "Roach Heaven"))
public class RoachEndpoint implements CockroachEndpoint {

    private final static Response NOT_FOUND = Response.status(404, "Cockroach not found").build();
    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private RoachService roachService;

    @GET
    @Path("/lure")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Lure cockroach",
            description = "Try lure cockroach",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach lured"),
                    @ApiResponse(responseCode = "404", description = "Cockroach not found")
            }
    )
    @ApiResponse(responseCode = "200", description = "roach lured")
    @Override
    public Roach lure() {
        // FIXME Node works
        return null;
    }

    @PATCH
    @Path("/feed")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Feed cockroach",
            description = "Try feed cockroach, if fill = 10 ",
            method = HttpMethod.PATCH,
            responses = {
                    @ApiResponse(responseCode = "200", description = "at up")
            }
    )
    @Override
    public Response feed() {
        try {
            roachService.checkRoach();
            if (roachService.feed()) {
                return Response.ok().entity("eat up").build();
            }
            return Response.status(412, "no hunger").build();
        } catch (RoachException e) {
            return NOT_FOUND;
        }
    }

    @DELETE
    @Path("/kick")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Kick cockroach",
            description = "Kick cockroach from current node in a random destination",
            method = HttpMethod.DELETE,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach was kicked"),
                    @ApiResponse(responseCode = "404", description = "Cockroach not found")
            }
    )
    @Override
    public Response kick() {
        try {
            roachService.kick();
            return Response.ok("cockroach was kicked").build();
        } catch (RoachException e) {
            return NOT_FOUND;
        }
    }

    @GET
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Cockroach state",
            description = "Get cockroach state in nodes",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach was kicked")
            }
    )
    @Override
    public Roach get() {
        return roachService.get();
    }

}


