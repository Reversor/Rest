package rest;

import entities.Cockroach;
import exceptions.CockroachException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/node")
@Hidden
@OpenAPIDefinition(
        info = @Info(
                title = "Node endpoint",
                description = "Is used for work with other nodes"
        )
)
public class NodeEndpoint {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Inject
    private RoachService roachService;

    @GET
    @Path("/")
    @Operation(
            summary = "Check working",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(responseCode = "200", description = "IT'S ALIVE")
            }
    )
    public Response alive() {
        return Response.ok().build();
    }

    @DELETE
    @Path("/roach")
    @Operation(
            summary = "Delete cockroach",
            method = HttpMethod.DELETE,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deleted"),
                    @ApiResponse(responseCode = "503", description = "Service unavailable")
            }
    )
    public Response delete() {
        if (roachService.killRoach()) {
            return Response.ok("Roach deleted").build();
        }
        return Response.status(Status.SERVICE_UNAVAILABLE).build();
    }

    @GET
    @Path("/roach")
    @Operation(
            summary = "Check node for cockroach",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Roach is here, look"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Roach not found"
                    )
            }
    )
    public Response checkNodeForCockroach() {
        try {
            Cockroach roach = roachService.checkRoach();
            return Response.ok(roach, MediaType.APPLICATION_JSON).build();
        } catch (CockroachException e) {
            return Response.status(Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/roach/creation")
    @Operation(
            summary = "Get cockroach time of creation",
            method = HttpMethod.GET,
            responses = {
                    @ApiResponse(
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Roach not found"
                    )
            }
    )
    public Response checkCockroachCreatedTime() {
        long created = roachService.getCreatedTime();
        if (created == Long.MAX_VALUE) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(roachService.getCreatedTime()).build();
    }

    @POST
    @Path("/roach")
    @Operation(
            summary = "Take cockroach",
            method = HttpMethod.POST,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "All done"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Already have the cockroach",
                            headers = {
                                    @Header(name = "created",
                                            description = "time creating a cockroach")
                            }
                    )
            }
    )
    public Response takeRoach(@QueryParam("created") long created, Cockroach roach) {
        if (roachService.setRoach(roach, created)) {
            logger.info("Cockroach was received");
            return Response.ok().build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }
}
