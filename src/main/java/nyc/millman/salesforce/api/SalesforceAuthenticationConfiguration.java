package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SalesforceAuthenticationConfiguration {

    @JsonProperty("tokenUrl")
    private final String tokenUrl;

    @JsonProperty("username")
    private final String username;

    @JsonProperty("password")
    private final String password;

    @JsonProperty("clientId")
    private final String clientId;

    @JsonProperty("clientSecret")
    private final String clientSecret;

    public SalesforceAuthenticationConfiguration(
            @JsonProperty("tokenUrl") String tokenUrl,
            @JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("clientId") String clientId,
            @JsonProperty("clientSecret") String clientSecret
    ){
        this.tokenUrl = tokenUrl;
        this.username = username;
        this.password = password;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @JsonProperty("tokenUrl")
    public String getTokenUrl(){
        return tokenUrl;
    }

    @JsonProperty("username")
    public String getUsername(){
        return username;
    }

    @JsonProperty("password")
    public String getPassword(){
        return password;
    }

    @JsonProperty("clientId")
    public String getClientId(){
        return clientId;
    }

    @JsonProperty("clientSecret")
    public String getClientSecret(){
        return clientSecret;
    }

    @Override
    public String toString(){
        return "{" +
                "tokenUrl:\"" + tokenUrl + "\"" +
                "username:\"" + username + "\"" +
                "clientId:\"" + clientId + "\"" +
                "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenUrl, username, password, clientId, clientSecret);

    }

}