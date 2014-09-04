package com.hctpbl.biovoiceapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hctpbl.biovoiceapp.api.APIConnException;
import com.hctpbl.biovoiceapp.api.APIErrorDialog;
import com.hctpbl.biovoiceapp.api.BioVoiceAPI;
import com.hctpbl.biovoiceapp.api.RetroFitErrorHandler;
import com.hctpbl.biovoiceapp.api.model.UserStatus;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Fragment to access the voice recording. Asks for a registered user.
 */
public class VoiceAccessFragment extends Fragment {

    private static final String TAG = "VoiceAccessFragment";

    private static final String URL_STATUS_USER = "v1/voiceaccess/status";

    private static final String DIALOG_ERROR = "error_dialog_voice_access";

    public static final String KEY_REGISTERED = "registered";
    public static final String KEY_ENROLLED = "enrolled";
    public static final String KEY_MESSAGE = "message";

    public static final String EXTRA_VOICEACC_USERNAME = "com.hctpbl.biovoiceapp.voiceacc_username";
    public static final String EXTRA_VOICEACC_ENROLLED = "com.hctpbl.biovoiceapp.voiceacc_enrolled";

    private int mErrorMessageId;
    Validator mValidator;

    @Required(order = 1, messageResId = R.string.user_username_validate_required)
    @TextRule(order = 2, minLength = 2, maxLength = 6, messageResId = R.string.user_username_validate_length)
    private EditText mVoiceUsernameEditText;

    private Button mVoiceRecognitionButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_voice_access,
				container, false);

        mVoiceUsernameEditText = (EditText)v.findViewById(R.id.voice_username);

        mValidator = new Validator(this);
        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                goVoiceRecognition();
            }

            @Override
            public void onValidationFailed(View failedView, Rule<?> failedRule) {
                String message = failedRule.getFailureMessage();
                failedView.requestFocus();
                ((EditText) failedView).setError(message);
            }
        });

        mVoiceRecognitionButton = (Button)v.findViewById(R.id.voice_go);
        mVoiceRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValidator.validate();
            }
        });
		
		return v;
	}

    /**
     * Go to voice recognition fragment
     */
    private void goVoiceRecognition() {
        UserStatus usrStatus;
        try {
            usrStatus = new getUserStatus().execute().get();
            if (usrStatus != null) {
                if (usrStatus.isRegistered()) {
                    VoiceRecordingFragment fragment = VoiceRecordingFragment.newInstance(
                            mVoiceUsernameEditText.getText().toString(), usrStatus.isEnrolled());
                    getFragmentManager().beginTransaction().replace(
                            R.id.container_voice_recognition,
                            fragment).addToBackStack(null).commit();
                } else {
                    mVoiceUsernameEditText.setError(usrStatus.getMessage());
                    mVoiceUsernameEditText.requestFocus();
                }
            }
        } catch (Exception e) {
            APIErrorDialog.getErrorDialog(mErrorMessageId).show(getFragmentManager(), DIALOG_ERROR);
        }
    }

    /**
     * Creates a thread to get the status of a user from the API
     */
    private class getUserStatus extends AsyncTask<Void, Void, UserStatus> {

        @Override
        protected UserStatus doInBackground(Void... params) {

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String apiURLstr = sp.getString(SettingsActivity.KEY_PREF_API_URL, "")
                    + "/" + URL_STATUS_USER + "/" + mVoiceUsernameEditText.getText();
            try {
                RestAdapter adapter = new RestAdapter.Builder()
                        .setEndpoint(apiURLstr)
                        .build();
                BioVoiceAPI api = adapter.create(BioVoiceAPI.class);
                UserStatus response = api.userStatus(mVoiceUsernameEditText.getText().toString());
                return response;
            } catch (RetrofitError re) {
                if (re.isNetworkError()) {
                    mErrorMessageId = APIErrorDialog.ERROR_NO_CONNECTION;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(UserStatus result) {
            super.onPostExecute(result);
            if (mErrorMessageId != 0) {
                APIErrorDialog.getErrorDialog(mErrorMessageId).show(getFragmentManager(), DIALOG_ERROR);
            }
        }
    }
}
