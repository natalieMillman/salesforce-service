package nyc.millman.salesforce.external.client;

import nyc.millman.salesforce.api.SalesforceAuthenticationConfiguration;
import nyc.millman.salesforce.api.SalesforceClientConfiguration;
import nyc.millman.salesforce.api.SalesforceToken;
import nyc.millman.salesforce.service.SalesforceAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SalesforceClient {
    
    private final HttpClient client;
    private final Logger logger = LoggerFactory.getLogger(SalesforceClient.class);
    private final SalesforceClientConfiguration configuration;
    private final SalesforceAuthenticationConfiguration authenticationConfiguration;
    private final SalesforceAuthenticationService service;
    private SalesforceToken token;

    public SalesforceClient(SalesforceClientConfiguration configuration, SalesforceAuthenticationConfiguration AuthenticationConfiguration) {
        this.client = HttpClient.newHttpClient();
        this.configuration = configuration;
        this.authenticationConfiguration = AuthenticationConfiguration;
        this.service = new SalesforceAuthenticationService(this, AuthenticationConfiguration);
        this.token = getToken();
    }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        fetchNewTokenIfNeeded();
        var uri = URI.create(String.format("%s%S", configuration.getBaseUrl(), url));
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.accessToken())
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> post(String url, String body, String contentType) throws IOException, InterruptedException {
        fetchNewTokenIfNeeded();
        var uri = URI.create(String.format("%s%S", configuration.getBaseUrl(), url));
        var bodyPublisher = HttpRequest.BodyPublishers.ofString(body);
        var request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token.accessToken())
                .header("content-type", contentType)
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private SalesforceToken getToken(){
        try{
            return service.getToken();
        } catch (Exception e){
            logger.error("Exception caught! {}", e.getMessage());
        }
        return null;
    }

    private void fetchNewTokenIfNeeded(){
        if(Instant.now().minus(900, ChronoUnit.SECONDS).isBefore(token.issuedAt())){
            this.token = getToken();
        }
    }

}