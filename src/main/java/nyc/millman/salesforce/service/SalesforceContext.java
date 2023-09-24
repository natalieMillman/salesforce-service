package nyc.millman.salesforce.service;

import nyc.millman.salesforce.external.client.SalesforceClient;

public class SalesforceContext {

    private final SalesforceClient client;

    public SalesforceContext(SalesforceClient client){
        this.client = client;
    }

    public SalesforceClient getClient(){
        return client;
    }

}
