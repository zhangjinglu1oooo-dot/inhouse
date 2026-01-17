package com.inhouse.ai;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 调用请求体。
 */
public class AiInvokeRequest {
    // 用户提示词
    private String prompt;
    // 调用参数（温度、最大长度等）
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
