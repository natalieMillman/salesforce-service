package nyc.millman.salesforce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import nyc.millman.salesforce.api.SalesforceAuthenticationConfiguration;
import nyc.millman.salesforce.api.SalesforceToken;
import nyc.millman.salesforce.external.client.SalesforceClient;

import java.io.IOException;
import java.net.http.HttpResponse;

public class SalesforceAuthenticationService {

    private final SalesforceClient client;
    private final SalesforceAuthenticationConfiguration configuration;
    private final ObjectMapper mapper = new ObjectMapper();

    public SalesforceAuthenticationService(SalesforceClient client, SalesforceAuthenticationConfiguration configuration){
        this.client = client;
        this.configuration = configuration;
    }

    public SalesforceToken getToken() throws IOException, InterruptedException{
        String requestBody = getRequestBody();
        HttpResponse<String>  response = client.post("s", requestBody, "application/x-www-form-urlencoded");
        return mapper.readValue(response.body(), SalesforceToken.class);
    }

    private String getRequestBody(){
        return String.format("username=%s&password=%s&client_id=%s&client_secret=%s",
                configuration.getUsername(),
                configuration.getPassword(),
                configuration.getClientId(),
                configuration.getClientSecret());
    }

}
