package com.dev.chakray.testchakray.model;

public class Response {

    private boolean success;
    private Object data;

    public Response() {
        this.success = false;
        this.data = null;
    }

    public Response(
            boolean success,
            Object data
    ) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

}