package com.catena.response;

/**
 * Created by hx-pc on 16-5-30.
 */
public class MockReturnResponse {

    private String api;
    private String key;

    public MockReturnResponse(String key, String api) {
        this.api = api;
        this.key = key;
    }

    public static MockReturnResponse build(String key, String api) {

        return new MockReturnResponse(key, api);
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
