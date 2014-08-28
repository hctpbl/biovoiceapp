package com.hctpbl.biovoiceapp;

import com.hctpbl.biovoiceapp.api.model.VoiceAccessResponse;

public interface VoiceAccessResponseReceiver {
    public void getVoiceAccessResponse(VoiceAccessResponse response);
}
