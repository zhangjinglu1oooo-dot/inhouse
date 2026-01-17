package com.inhouse.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/gateway")
public class GatewayController {
    private final RestTemplate restTemplate;
    private final String permissionServiceUrl;

    public GatewayController(RestTemplate restTemplate,
                             @Value("${permission.service.url}") String permissionServiceUrl) {
        this.restTemplate = restTemplate;
        this.permissionServiceUrl = permissionServiceUrl;
    }

    @PostMapping("/{app}/{feature}")
    public PermissionCheckResponse route(
            @PathVariable("app") String app,
            @PathVariable("feature") String feature,
            @RequestBody PermissionCheckRequest request) {
        request.setApp(app);
        request.setFeature(feature);
        ResponseEntity<PermissionCheckResponse> response = restTemplate.postForEntity(
                permissionServiceUrl + "/permissions/check",
                new HttpEntity<PermissionCheckRequest>(request),
                PermissionCheckResponse.class);
        if (response.getBody() == null) {
            throw new GatewayAccessDenied("Permission service returned empty response");
        }
        if (!response.getBody().isAllowed()) {
            throw new GatewayAccessDenied(response.getBody().getDecisionReason());
        }
        return response.getBody();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    private static class GatewayAccessDenied extends RuntimeException {
        GatewayAccessDenied(String message) {
            super(message);
        }
    }
}
