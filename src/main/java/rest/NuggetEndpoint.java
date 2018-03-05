package rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import services.GetOutService;

@Path("/")
public class NuggetEndpoint {

    @Inject
    private GetOutService getOutService;

    @GET
    @Path("/nugget")
    @Produces(MediaType.TEXT_PLAIN)
    public String getNugget() {
        return getOutService.sayGetOut("bro");
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public String getOut() {
        return "{ message: \"get out\" }";
    }
}
