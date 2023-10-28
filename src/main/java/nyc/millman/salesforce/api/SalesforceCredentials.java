package nyc.millman.salesforce.api;

import io.grpc.CallCredentials;
import io.grpc.Metadata;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executor;

public class SalesforceCredentials extends CallCredentials {

    public static final Metadata.Key<String> INSTANCE_URL = keyOf("instanceUrl");
    // Session token of the customer
    public static final Metadata.Key<String> SESSION_TOKEN = keyOf("accessToken");
    // Tenant Id of the customer org
    public static final Metadata.Key<String> TENANT_ID = keyOf("tenantId");
    private final String instanceUrl;
    private final String accessToken;
    private final String orgId;

    public SalesforceCredentials(SalesforceToken token){
        this.instanceUrl = token.instanceUrl();
        this.accessToken = token.accessToken();
        this.orgId = parseOutOrgId(token.id());
    }

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        Metadata headers = new Metadata();
        headers.put(INSTANCE_URL, instanceUrl);
        headers.put(SESSION_TOKEN, accessToken);
        headers.put(TENANT_ID, orgId);
        metadataApplier.apply(headers);
    }

    @Override
    public void thisUsesUnstableApi() {

    }

    private String parseOutOrgId(String tokenId){
        return StringUtils.substringBetween(tokenId, "id/", "/");
    }

    private static Metadata.Key<String> keyOf(String name) {
        return Metadata.Key.of(name, Metadata.ASCII_STRING_MARSHALLER);
    }
}
