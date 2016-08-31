package com.catena.mock;

/**
 * Created by hx-pc on 16-6-3.
 */
public class MockRuntimeException extends RuntimeException {

    private String code = "999";

    public MockRuntimeException(String code) {
        super();
        this.code = code;
    }

    public MockRuntimeException(String message, String code) {
        super(message);
        this.code = code;
    }

    public MockRuntimeException(String message, Throwable cause, String code) {
        super(message, cause);
        this.code = code;
    }

    public MockRuntimeException(Throwable cause, String code) {
        super(cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
