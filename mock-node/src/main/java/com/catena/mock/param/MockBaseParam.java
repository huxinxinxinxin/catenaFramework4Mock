package com.catena.mock.param;

import com.catena.core.NodeParameter;
import com.catena.mock.core.HttpRequestMethod;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by hx-pc on 16-5-20.
 */
public class MockBaseParam extends NodeParameter {

    @NotBlank
    private String key;
    @NotBlank
    private String apiValue;
    @NotBlank
    private String dataValue;
    @NotBlank
    private HttpRequestMethod httpRequestMethod;

    public String getApiValue() {
        return apiValue;
    }

    public void setApiValue(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return httpRequestMethod;
    }

    public void setHttpRequestMethod(HttpRequestMethod httpRequestMethod) {
        this.httpRequestMethod = httpRequestMethod;
    }
}
