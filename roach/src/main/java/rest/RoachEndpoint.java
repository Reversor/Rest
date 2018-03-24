package rest;

import entities.Roach;
import exceptions.CockroachException;
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
    @Operation(
            summary = "Lure cockroach",
            description = "Try lure cockroach",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach lured"),
                    @ApiResponse(responseCode = "404", description = "Cockroach not found")
            }
    )
    @Override
    public Response lure() {
        try {
            return Response.ok(roachService.lure()).build();
        } catch (CockroachException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Path("/feed")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(
            summary = "Feed cockroach",
            description = "Try feed cockroach, if fill = 10 ",
            method = HttpMethod.PATCH,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach ate"),
                    @ApiResponse(responseCode = "200", description = "Cockroach not hungry"),
                    @ApiResponse(responseCode = "404", description = "Cockroach not found")
            }
    )
    @Override
    public Response feed() {
        try {
            roachService.checkRoach();
            if (roachService.feed()) {
                return Response.ok("Eat up").build();
            }
            return Response.ok("Cockroach not hungry").build();
        } catch (CockroachException e) {
            return Response.status(Status.NOT_FOUND).build();
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
                    @ApiResponse(responseCode = "200", description = "Cockroach hungry"),
                    @ApiResponse(responseCode = "404", description = "Cockroach not found")
            }
    )
    @Override
    public Response kick() {
        try {
            if (roachService.kick()) {
                return Response.ok("roach was kicked").build();
            }
            return Response.ok("Cockroach hungry").build();
        } catch (CockroachException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/get")
    @Operation(
            summary = "Cockroach state",
            description = "Get cockroach state in nodes",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cockroach state")
            }
    )
    @Override
    public Roach get() {
        return roachService.get();
    }

    @GET
    @Path("/")
    public Response alive() {
        return Response.ok().build();
    }

}


