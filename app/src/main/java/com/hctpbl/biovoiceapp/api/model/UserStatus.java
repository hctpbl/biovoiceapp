package com.hctpbl.biovoiceapp.api.model;

/**
 * Status of a user. If he is registered and if he is
 * enrolled.
 */
public class UserStatus {
    boolean error;
    boolean registered;
    boolean enrolled;
    String message;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
