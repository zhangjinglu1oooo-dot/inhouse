package com.inhouse.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * API 网关控制器，负责转发权限校验。
 */
@RestController
@RequestMapping("/gateway")
@CrossOrigin(origins = "*")
public class GatewayController {
    // HTTP 客户端
    private final RestTemplate restTemplate;
    // 权限服务地址
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
        // 将路径参数补充到请求体
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

    /**
     * 网关内部拒绝异常。
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    private static class GatewayAccessDenied extends RuntimeException {
        GatewayAccessDenied(String message) {
            super(message);
        }
    }
}
