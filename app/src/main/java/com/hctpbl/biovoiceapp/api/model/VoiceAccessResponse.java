package com.hctpbl.biovoiceapp.api.model;

public class VoiceAccessResponse {
    private boolean error;
    private float result;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }
}
