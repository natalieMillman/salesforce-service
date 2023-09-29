package nyc.millman.salesforce.application;

import io.dropwizard.core.setup.Environment;
import nyc.millman.salesforce.resource.AccountResource;
import nyc.millman.salesforce.resource.ContactResource;
import nyc.millman.salesforce.resource.OpportunityResource;
import nyc.millman.salesforce.service.SalesforceContext;

public class SalesforceServiceResourceFactory {

    private final SalesforceContext context;
    private final Environment environment;

    public SalesforceServiceResourceFactory(Environment environment, SalesforceContext context){
        this.environment = environment;
        this.context = context;
    }

    public SalesforceServiceResourceFactory registerResources(){
        environment.jersey().register(getAccountResource());
        environment.jersey().register(getContactResource());
        environment.jersey().register(getOpportunityResource());
        return this;
    }

    public AccountResource getAccountResource(){
        return new AccountResource(context);
    }

    public ContactResource getContactResource(){
        return new ContactResource(context);
    }

    public OpportunityResource getOpportunityResource(){
        return new OpportunityResource(context);
    }

}
