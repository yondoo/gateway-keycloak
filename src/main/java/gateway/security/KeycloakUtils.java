package gateway.security;

import gateway.security.oauth2.AuthorizationHeaderUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.resource.ProtectedResource;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeycloakUtils {
    private AuthzClient authzClient;

    private final AuthorizationHeaderUtil headerUtil;

    private JwtDecoder jwtDecoder;

    private final static String CLAIM_AUTHORIZATION = "authorization";
    private final static String CLAIM_PERMISSIONS = "permissions";
    private final static String CLAIM_SCOPES = "scopes";

    public KeycloakUtils(Configuration configuration, AuthorizationHeaderUtil headerUtil, JwtDecoder jwtDecoder) {
        this.authzClient = AuthzClient.create(configuration);
        this.headerUtil = headerUtil;
        this.jwtDecoder = jwtDecoder;
    }

    public boolean isAuthorized(String requestUri, String httpMethod) throws JSONException {
        if (!headerUtil.getAuthorizationHeader().isPresent()) {
            return false;
        }
        // create an authorization request
        AuthorizationRequest request = new AuthorizationRequest();
//        request.getClaims().put("", "");
        Optional<ResourceRepresentation> resourceRepresentation = findResourceByPath(requestUri);
        if (!resourceRepresentation.isPresent()) {
            return false;
        }
        String resourceType = resourceRepresentation.get().getType();
        String scope = resourceType + ":" + getScope(httpMethod);
        request.addPermission(resourceRepresentation.get().getId(), scope);
        String accessToken = headerUtil.getAuthorizationHeader().get().split(" ")[1];
        try {
            // authorize request
            AuthorizationResponse response = authzClient.authorization(accessToken).authorize(request);
            // get rpt token
            String rpt = response.getToken();
            Jwt jwt = jwtDecoder.decode(rpt);
            Map<String, Object> authorization = jwt.getClaimAsMap(CLAIM_AUTHORIZATION);
            JSONArray permissions = (JSONArray) authorization.get(CLAIM_PERMISSIONS);
            AtomicBoolean hasScope = new AtomicBoolean(false);
            // check permission scopes
            permissions.forEach(node -> {
                JSONObject permission = (JSONObject) node;
                if (permission.containsKey(CLAIM_SCOPES)) {
                    JSONArray scopes = (JSONArray) permission.get(CLAIM_SCOPES);
                    if (scopes.contains(scope)) {
                        hasScope.set(true);
                    }
                }

            });
            return hasScope.get();
        } catch (AuthorizationDeniedException e) {
            return false;
        }
    }

    private Optional<ResourceRepresentation> findResourceByPath(String resourcePath) {
        ProtectedResource resourceClient = authzClient.protection().resource();
        List<ResourceRepresentation> resourceList = resourceClient.findByMatchingUri(resourcePath);
        if (!resourceList.isEmpty()) {
            return Optional.of(resourceList.get(0));
        }
        return Optional.empty();
    }

    private String getScope(String httpMethod) {
        Map<String, String> mapScopes = new HashMap<>();
        mapScopes.put(HttpMethod.GET.toString(), "view");
        mapScopes.put(HttpMethod.POST.toString(), "create");
        mapScopes.put(HttpMethod.PUT.toString(), "update");
        mapScopes.put(HttpMethod.DELETE.toString(), "delete");
        return mapScopes.get(httpMethod);
    }

}
