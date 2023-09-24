package nyc.millman.salesforce.application;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Environment;
import nyc.millman.salesforce.external.client.SalesforceClient;
import nyc.millman.salesforce.service.SalesforceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalesforceServiceApplication extends Application<SalesforceServiceConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceServiceApplication.class);

    public static void main(String[] args) throws Exception {
        new SalesforceServiceApplication().run(args);
    }

    @Override
    public void run(SalesforceServiceConfiguration configuration, Environment environment) {
        environment.healthChecks().register("Application", new SalesforceServiceHealthCheck());
        SalesforceClient salesforceClient = new SalesforceClient(configuration.getSalesforceClientConfiguration(), configuration.getSalesforceAuthenticationConfiguration());
        SalesforceContext salesforceContext = new SalesforceContext(salesforceClient);
        SalesforceServiceResourceFactory resourceFactory = new SalesforceServiceResourceFactory(environment, salesforceContext).registerResources();
    }


}
