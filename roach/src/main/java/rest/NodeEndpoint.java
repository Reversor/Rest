package rest;

import entities.Roach;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jboss.logging.Logger;
import services.RoachService;

@Path("/node")
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

    @POST
    @Path("/roach")
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Take cockroach",
            method = HttpMethod.POST,
            responses = {
                    @ApiResponse(responseCode = "200", description = "All done"),
                    @ApiResponse(responseCode = "400", description = "Already have the cockroach")
            }
    )
    public Response takeRoach(Roach roach) {
        if (roachService.setRoach(roach)) {
            logger.info("Cockroach was received");
            return Response.ok().build();
        }
        return Response.status(Status.BAD_REQUEST).build();
    }
}
