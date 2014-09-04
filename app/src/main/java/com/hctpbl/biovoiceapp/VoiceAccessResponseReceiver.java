package com.hctpbl.biovoiceapp;

import com.hctpbl.biovoiceapp.api.model.VoiceAccessResponse;

/**
 * Interface that must be implemented by every fragment or activity
 * trying to perform asynchronous enrollment or validation against the API
 */
public interface VoiceAccessResponseReceiver {

    /**
     * Receives and processes the API response
     *
     * @param response The response to a enrollment or verification action
     */
    public void getVoiceAccessResponse(VoiceAccessResponse response);
}
