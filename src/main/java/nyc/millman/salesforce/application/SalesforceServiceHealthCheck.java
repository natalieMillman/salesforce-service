package nyc.millman.salesforce.application;

import com.codahale.metrics.health.HealthCheck;
public class SalesforceServiceHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        boolean status = true;

        //Check application health and set the status accordingly

        if(status == false)
            return Result.unhealthy("message");
        return Result.healthy();
    }
}