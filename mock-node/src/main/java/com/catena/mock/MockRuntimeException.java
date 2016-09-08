package com.catena.mock;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hx-pc on 16-6-3.
 */
public class MockRuntimeException extends RuntimeException {

    protected int errorCode;
    protected String message;

    public MockRuntimeException(int errorCode) {
        this.errorCode = errorCode;
        this.message = "Unknown error";
    }

    public MockRuntimeException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public MockRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public MockRuntimeException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map toMap() {
        HashMap map = new HashMap();
        map.put("errorCode", Integer.valueOf(this.errorCode));
        map.put("message", this.message);
        return map;
    }
}
