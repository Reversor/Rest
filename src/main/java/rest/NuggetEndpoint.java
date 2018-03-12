package rest;

import enitities.Roach;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import services.RoachService;

@Path("/")
public class NuggetEndpoint {

    @Inject
    private RoachService roachService;

    @GET
    @Path("/nugget")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean getNugget() {
        return roachService.feed();
    }

    @GET
    @Path("/json/{param}")
    @Consumes
    @Produces(MediaType.APPLICATION_JSON)
    public Roach getRoach(@PathParam("param")String param) {
        return new Roach(param, (byte) 0);
    }
}
