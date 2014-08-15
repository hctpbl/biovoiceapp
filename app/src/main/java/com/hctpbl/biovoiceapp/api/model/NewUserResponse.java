package com.hctpbl.biovoiceapp.api.model;

public class NewUserResponse {
    private int http_code;
    private boolean error;
    private String message;
    private ErrorMessages messages;
    private User user;

    public int getHttp_code() {
        return http_code;
    }

    public void setHttp_code(int http_code) {
        this.http_code = http_code;
    }

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorMessages getMessages() {
        return messages;
    }

    public void setMessages(ErrorMessages messages) {
        this.messages = messages;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String toString() {
        return "http_code="+getHttp_code()+"&error="+String.valueOf(getError())+"&Message="+getMessage();
    }
}
