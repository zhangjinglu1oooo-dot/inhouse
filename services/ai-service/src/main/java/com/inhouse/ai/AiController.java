package com.inhouse.ai;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 服务控制器，提供模型调用模拟接口。
 */
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "*")
public class AiController {
    @PostMapping("/{provider}/{model}/invoke")
    public AiInvokeResponse invoke(
            @PathVariable("provider") String provider,
            @PathVariable("model") String model,
            @RequestBody AiInvokeRequest request) {
        // 简单模拟 AI 调用，返回反转后的提示词
        String output = "[" + provider + ":" + model + "] " + new StringBuilder(request.getPrompt()).reverse();
        Map<String, Object> usage = new HashMap<String, Object>();
        usage.put("promptTokens", countTokens(request.getPrompt()));
        usage.put("completionTokens", countTokens(output));

        AiInvokeResponse response = new AiInvokeResponse();
        response.setProvider(provider);
        response.setModel(model);
        response.setOutput(output);
        response.setUsage(usage);
        response.setCreatedAt(new Date());
        return response;
    }

    /**
     * 简易 token 统计，用空格分割。
     */
    private int countTokens(String text) {
        if (text == null || text.trim().isEmpty()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
