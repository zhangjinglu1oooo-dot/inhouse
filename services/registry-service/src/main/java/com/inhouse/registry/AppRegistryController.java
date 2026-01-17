package com.inhouse.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 应用注册控制器。
 */
@RestController
@RequestMapping("/registry/apps")
@CrossOrigin(origins = "*")
public class AppRegistryController {
    // 内存应用仓库
    private final AppStore store;

    public AppRegistryController(AppStore store) {
        this.store = store;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppDefinition create(@RequestBody AppDefinition app) {
        // 创建应用记录
        String id = UUID.randomUUID().toString();
        app.setId(id);
        app.setCreatedAt(new Date());
        store.getApps().put(id, app);
        return app;
    }

    @GetMapping
    public List<AppDefinition> list() {
        return new ArrayList<AppDefinition>(store.getApps().values());
    }
}
