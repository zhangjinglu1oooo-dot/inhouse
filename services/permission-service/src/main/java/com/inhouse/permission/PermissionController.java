package com.inhouse.permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限策略管理与鉴权控制器。
 */
@RestController
@RequestMapping("/permissions")
@CrossOrigin(origins = "*")
public class PermissionController {
    // 策略仓库
    private final PolicyRepository repository;

    public PermissionController(PolicyRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public Policy createPolicy(@RequestBody Policy policy) {
        // 创建策略并记录创建时间
        String id = UUID.randomUUID().toString();
        policy.setId(id);
        policy.setCreatedAt(new Date());
        repository.savePolicy(policy);
        return policy;
    }

    @GetMapping("/policies")
    public List<Policy> listPolicies() {
        return new ArrayList<Policy>(repository.listPolicies());
    }

    @PostMapping("/check")
    public PermissionCheckResponse check(@RequestBody PermissionCheckRequest request) {
        // 遍历策略进行匹配
        for (Policy policy : repository.listPolicies()) {
            if (!policy.getApp().equals(request.getApp())) {
                continue;
            }
            if (!policy.getFeature().equals(request.getFeature())) {
                continue;
            }
            if (policy.getResource() != null && request.getResource() != null
                    && !policy.getResource().equals(request.getResource())) {
                continue;
            }
            if (!conditionsMatch(policy.getConditions(), request.getAttributes())) {
                continue;
            }
            if ("deny".equalsIgnoreCase(policy.getEffect())) {
                return new PermissionCheckResponse(false, "Denied by policy");
            }
            return new PermissionCheckResponse(true, "Allowed by policy");
        }
        return new PermissionCheckResponse(false, "No matching policy");
    }

    private boolean conditionsMatch(Map<String, Object> conditions, Map<String, Object> attributes) {
        // 校验策略条件与请求属性是否一致
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            Object value = attributes.get(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
