package com.zbar.lib.http;

public class CallResponse {
    public String error;
    public String message;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CallResponse{" +
                "error='" + error + '\'' +
                ", message='" + message + '\'' +
                '}';
    }



}
