package nyc.millman.salesforce.application;

import io.dropwizard.core.setup.Environment;
import nyc.millman.salesforce.resource.MainResource;
import nyc.millman.salesforce.service.SalesforceContext;

public class SalesforceServiceResourceFactory {

    private final SalesforceContext service;
    private final Environment environment;

    public SalesforceServiceResourceFactory(Environment environment, SalesforceContext service){
        this.environment = environment;
        this.service = service;
    }

    public SalesforceServiceResourceFactory registerResources(){
        environment.jersey().register(getMainResource());
        return this;
    }

    public MainResource getMainResource(){
        return new MainResource(service);
    }
}
