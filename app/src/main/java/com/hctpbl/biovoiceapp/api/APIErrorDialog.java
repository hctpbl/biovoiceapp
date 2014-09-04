package com.hctpbl.biovoiceapp.api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.hctpbl.biovoiceapp.R;
import com.hctpbl.biovoiceapp.UserRegisterFragment;

/**
 * Fragment dialog to show API errors
 */
public class APIErrorDialog extends DialogFragment {
	
	private static final String TAG = "APIErrorDialog";
	
	public static final String EXTRA_ERROR_TYPE = 
			"com.hctpbl.biovoiceapp.error_type";
	
	public static final int ERROR_URL = 1;
	public static final int ERROR_TIMEOUT = 2;
	public static final int ERROR_IO = 3;
	public static final int ERROR_JSON = 4;
	public static final int ERROR_NO_CONNECTION = 5;
    public static final int ERROR_NO_STORAGE = 6;
	
	private int mErrorType;
	
	public static APIErrorDialog getErrorDialog(int errorType) {
		Bundle args = new Bundle();
		args.putSerializable(EXTRA_ERROR_TYPE,
				errorType);
		APIErrorDialog errorDialog = new APIErrorDialog();
		errorDialog.setArguments(args);
		
		return errorDialog;
	}
	
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		mErrorType = getArguments().getInt(EXTRA_ERROR_TYPE);
		Log.e(TAG, "API error type: " + mErrorType);
		return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.general_validation_title)
					.setMessage(getErrorMessageId())
					.setPositiveButton(android.R.string.ok, null)
					.create();
	}
	
	private int getErrorMessageId() {
		switch (mErrorType) {
		case ERROR_URL:
			return R.string.general_validation_bad_api_url;
		case ERROR_TIMEOUT:
			return R.string.general_validation_timeout;
		case ERROR_IO:
			return R.string.general_validation_no_server;
		case ERROR_JSON:
			return R.string.general_validation_bad_json;
		case ERROR_NO_CONNECTION:
			return R.string.general_validation_no_connection;
        case ERROR_NO_STORAGE:
            return R.string.general_validation_no_storage;

		default:
			return R.string.general_validation_generic_message;
		}
	}

}
