package nyc.millman.salesforce.external.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nyc.millman.salesforce.api.configuration.SalesforceAuthenticationConfiguration;
import nyc.millman.salesforce.api.configuration.SalesforceClientConfiguration;
import nyc.millman.salesforce.api.SalesforceToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class SalesforceClient {
    
    private final HttpClient client;
    private final Logger logger = LoggerFactory.getLogger(SalesforceClient.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final SalesforceClientConfiguration configuration;
    private final SalesforceAuthenticationConfiguration authenticationConfiguration;
    private final String baseUrl;
    private SalesforceToken token;

    public SalesforceClient(SalesforceClientConfiguration configuration, SalesforceAuthenticationConfiguration AuthenticationConfiguration) {
        this.configuration = configuration;
        this.authenticationConfiguration = AuthenticationConfiguration;
        this.mapper.registerModule(new JavaTimeModule());
        this.baseUrl = configuration.getBaseUrl();
        this.token = getToken();
        this.client = buildClient(configuration);
    }

    public HttpResponse<String> get(String url) throws IOException, InterruptedException {
        fetchNewTokenIfNeeded();
        var uri = URI.create(String.format("%s%S", baseUrl, url));
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
        var uri = URI.create(String.format("%s%s", baseUrl, url));
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

    private void fetchNewTokenIfNeeded(){
        if(Instant.now().minusSeconds(900).isBefore(token.issuedAt())){
            this.token = getToken();
        }
    }

    public SalesforceToken getToken(){
        try{
            if(this.token != null){
                return this.token;
            } else {
                HttpResponse<String> response = fetchToken();
                return mapper.readValue(response.body(), SalesforceToken.class);
            }
        } catch (Exception e){
            logger.error("Exception caught! {}", e.getMessage());
        }
        return null;
    }

    private HttpResponse<String> fetchToken() throws IOException, InterruptedException {
        var requestBody = getRequestBody();
        var uri = URI.create(String.format("%s%s", baseUrl, "/services/oauth2/token"));
        var bodyPublisher = HttpRequest.BodyPublishers.ofString(requestBody);
        var request = HttpRequest.newBuilder()
                .POST(bodyPublisher)
                .uri(uri)
                .header("Accept", "application/json")
                .header("content-type", "application/x-www-form-urlencoded")
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String getRequestBody(){
        return String.format("grant_type=client_credentials&client_id=%s&client_secret=%s",
                authenticationConfiguration.getClientId(),
                authenticationConfiguration.getClientSecret());
    }

    private HttpClient buildClient(SalesforceClientConfiguration configuration){
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(configuration.getTimeout()))
                .build();
    }

    public void close(){
        client.shutdown();
    }

}