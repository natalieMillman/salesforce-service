package nyc.millman.salesforce.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nyc.millman.salesforce.service.SalesforceContext;

@Path("/contact")
public class ContactResource implements SalesforceResource {

    private final SalesforceContext context;

    public ContactResource(SalesforceContext context){
        this.context = context;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello(){
        return "Hello World!";
    }

}
