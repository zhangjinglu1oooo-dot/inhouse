package com.inhouse.ai;

import java.util.HashMap;
import java.util.Map;

public class AiInvokeRequest {
    private String prompt;
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
