package com.hctpbl.biovoiceapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;


public class EnrollmentSuccessDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.gendiag_enrolled_title)
                .setMessage(R.string.gendiag_enrolled_text)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
