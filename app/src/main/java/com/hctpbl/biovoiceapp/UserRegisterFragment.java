package com.hctpbl.biovoiceapp;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hctpbl.biovoiceapp.api.APIErrorDialog;
import com.hctpbl.biovoiceapp.api.BioVoiceAPI;
import com.hctpbl.biovoiceapp.api.model.ErrorMessages;
import com.hctpbl.biovoiceapp.api.model.NewUserResponse;
import com.hctpbl.biovoiceapp.api.model.User;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.mobsandgeeks.saripaar.annotation.TextRule;

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
import android.widget.Toast;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.converter.GsonConverter;

/**
 * Fragment to perform the user registration
 */
public class UserRegisterFragment extends Fragment {
	
	private static final String TAG = "UserRegisterFragment";
	
	public static final String EXTRA_JSON_REGISTRY_SUCCESS =
			"com.hctpbl.biovoiceapp.json_registry_succes";
	
	private static final String DIALOG_ERROR = "error_dialog_user_register";
	
	private static final String URL_REGISTER_USER = "/v1/users";

	public static final String KEY_USER = "users";
	public static final String KEY_USER_USERNAME = "username";
	public static final String KEY_USER_FIRSTNAME = "first_name";
	public static final String KEY_USER_SURNAME = "surname";
	public static final String KEY_USER_EMAIL = "email";
	public static final String KEY_USER_REGISTRY_TIME = "time_reg";
	public static final String KEY_USER_ERROR = "error";
	public static final String KEY_USER_ERROR_MESSAGES = "messages";
	
	private Validator mValidator;

    // Validation made using saripaar dependency

	@Required(order = 1, messageResId = R.string.user_username_validate_required)
	@TextRule(order = 2, minLength = 2, maxLength = 6, messageResId = R.string.user_username_validate_length)
	private EditText mUserUsernameEditText;
	
	@Required(order = 3, messageResId = R.string.user_first_name_validate_required)
	private EditText mUserFirstNameEditText;

	@Required(order = 4, messageResId = R.string.user_surname_validate_required)
	private EditText mUserSurnameEditText;

	@Required(order = 5, messageResId = R.string.user_email_validate_required)
	@Email(order = 6, messageResId = R.string.user_email_validate_valid)
	private EditText mUserEmailEditText;
	
	private Button mUserRegisterButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_user_register,
				container, false);

		mValidator = new Validator(this);
		mValidator.setValidationListener(new Validator.ValidationListener() {
			@Override
			public void onValidationSucceeded() {
				NewUserResponse respuesta;
				try {
                    respuesta = new RegisterUser().execute().get();
                    if (respuesta != null) {
                        if (!respuesta.getError()) {
                            RegisterSuccessFragment fragment = RegisterSuccessFragment.newInstance(respuesta.getUser());
                            getFragmentManager().beginTransaction().replace(R.id.user_container, fragment)
                                    .commit();
                            //Log.i(TAG, "Response: " + respuesta.toString());
                            //Log.i(TAG, respuesta.getUser().getUsername());
                        } else {
                            mapErrorMessages(respuesta.getMessages());
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onValidationFailed(View failedView, Rule<?> failedRule) {
				String message = failedRule.getFailureMessage();
				if (failedView instanceof EditText) {
					failedView.requestFocus();
					((EditText) failedView).setError(message);
				} else {
					Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();;
				}
			}
		});
		
		mUserUsernameEditText = (EditText)v.findViewById(R.id.editTextUsername);
		mUserFirstNameEditText = (EditText)v.findViewById(R.id.editTextFirstName);
		mUserSurnameEditText = (EditText)v.findViewById(R.id.editTextSurname);
		mUserEmailEditText = (EditText)v.findViewById(R.id.editTextEmail);
		
		mUserRegisterButton = (Button)v.findViewById(R.id.buttonRegisterUser);
		mUserRegisterButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mValidator.validate();
			}
		});
		
		return v;
	}

    /**
     * Class to create a thread in which the POST method of the aPI is called to register the user
     */
	private class RegisterUser extends AsyncTask<Void, Void, NewUserResponse> {
		
		int mErrorMessageId = 0;

		@Override
		protected NewUserResponse doInBackground(Void... params) {
			
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String apiURLstr = sp.getString(SettingsActivity.KEY_PREF_API_URL, "");

            //---
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(apiURLstr)
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setConverter(new GsonConverter(gson))
                    .build();
            BioVoiceAPI api = adapter.create(BioVoiceAPI.class);
            User user = new User();
            user.setUsername(mUserUsernameEditText.getText().toString());
            user.setFirst_name(mUserFirstNameEditText.getText().toString());
            user.setSurname(mUserSurnameEditText.getText().toString());
            user.setEmail(mUserEmailEditText.getText().toString());
            NewUserResponse response = null;
            try {
                response = api.createUser(user);
                Log.i(TAG, response.getUser().getUsername());
                return response;
            } catch (RetrofitError re) {
                Log.e(TAG, "err: " + re.getMessage());
                if (re.isNetworkError()) {
                    mErrorMessageId = APIErrorDialog.ERROR_IO;
                } else if (re.getResponse().getStatus() == 400) {
                    response = (NewUserResponse)re.getBodyAs(NewUserResponse.class);
                    return response;
                }
            }
			return null;
		}
		
		@Override
		protected void onPostExecute(NewUserResponse result) {
			super.onPostExecute(result);
			if (mErrorMessageId != 0) {
				APIErrorDialog.getErrorDialog(mErrorMessageId).show(getFragmentManager(), DIALOG_ERROR);
			}
		}
		
	}

    /**
     * Maps JSON error messages received to the different EditTexts
     * in the form
     * @param errors
     */
	private void mapErrorMessages(ErrorMessages errors) {
        // Solo se coge el primer mensaje de cada array
        List<String> usernameErrors = errors.getUsername();
        if (usernameErrors != null) {
            mUserUsernameEditText.setError(usernameErrors.get(0));
            mUserUsernameEditText.requestFocus();
        }
        List<String> firstNameErrors = errors.getFirst_name();
        if (firstNameErrors != null) {
            mUserFirstNameEditText.setError(firstNameErrors.get(0));
            mUserFirstNameEditText.requestFocus();
        }
        List<String> surnameErrors = errors.getSurname();
        if (surnameErrors != null) {
            mUserSurnameEditText.setError(surnameErrors.get(0));
            mUserSurnameEditText.requestFocus();
        }
        List<String> emailErrors = errors.getEmail();
        if (emailErrors != null) {
            mUserEmailEditText.setError(emailErrors.get(0));
            mUserEmailEditText.requestFocus();
        }
	}

}
