package com.hctpbl.biovoiceapp.api.model;

/**
 * Response to a voice access request. It can be of type
 * enrollment or verification
 */
public class VoiceAccessResponse {

    public static final String ACTION_ENROLL = "enroll";
    public static final String ACTION_TEST = "test";

    private boolean error;
    private String action;
    private float threshold;
    private float result;
    private String message;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }
}
