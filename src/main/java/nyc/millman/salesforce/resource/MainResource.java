package nyc.millman.salesforce.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nyc.millman.salesforce.service.SalesforceContext;

@Path("/")
public class MainResource {

    private final SalesforceContext service;

    public MainResource(SalesforceContext service){
        this.service = service;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello(){
        return "Hello World!";
    }


}
