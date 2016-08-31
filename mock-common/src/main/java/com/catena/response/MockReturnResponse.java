package com.catena.response;

/**
 * Created by hx-pc on 16-5-30.
 */
public class MockReturnResponse {

    private String api;
    private String data;

    public MockReturnResponse(String key, String value) {
        this.api = key;
        this.data = value;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static MockReturnResponse build(String key, String value) {
        return new MockReturnResponse(key, value);
    }
}
