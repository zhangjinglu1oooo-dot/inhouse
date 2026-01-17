package com.inhouse.permission;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
    private final PolicyStore store;

    public PermissionController(PolicyStore store) {
        this.store = store;
    }

    @PostMapping("/policies")
    @ResponseStatus(HttpStatus.CREATED)
    public Policy createPolicy(@RequestBody Policy policy) {
        String id = UUID.randomUUID().toString();
        policy.setId(id);
        policy.setCreatedAt(new Date());
        store.getPolicies().put(id, policy);
        return policy;
    }

    @GetMapping("/policies")
    public List<Policy> listPolicies() {
        return new ArrayList<Policy>(store.getPolicies().values());
    }

    @PostMapping("/check")
    public PermissionCheckResponse check(@RequestBody PermissionCheckRequest request) {
        for (Policy policy : store.getPolicies().values()) {
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
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            Object value = attributes.get(entry.getKey());
            if (value == null || !value.equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }
}
