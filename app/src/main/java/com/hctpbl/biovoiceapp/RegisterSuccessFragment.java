package com.hctpbl.biovoiceapp;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hctpbl.biovoiceapp.api.model.User;

/**
 * Fragment to show the user confirmation of his register, and
 * the data associated to the created user
 */
public class RegisterSuccessFragment extends Fragment {
	
	private static final String TAG = "RegisterSuccessFragment";
	
	TextView mUserUsernameTextView;
	TextView mUserFirstNameTextView;
	TextView mUserSurnameTextView;
	TextView mUserEmailTextView;
	TextView mUserRegistryTimeTextView;
	
	Button mGoMainButton;
	Button mGoEnrollButton;

    /**
     * Static method to create an instance of this fragment with the
     * required parameters
     * @param user User just registered
     * @return An instance of RegisterSuccessFragment
     */
	public static RegisterSuccessFragment newInstance(User user) {
		Bundle args = new Bundle();
		args.putSerializable(UserRegisterFragment.EXTRA_JSON_REGISTRY_SUCCESS,
				user);
		RegisterSuccessFragment fragment = new RegisterSuccessFragment();
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.fragment_register_success,
				container, false);
		
		mUserUsernameTextView = (TextView)v.findViewById(R.id.regsuc_usr_username);
		mUserFirstNameTextView = (TextView)v.findViewById(R.id.regsuc_usr_first_name);
		mUserSurnameTextView = (TextView)v.findViewById(R.id.regsuc_usr_surname);
		mUserEmailTextView = (TextView)v.findViewById(R.id.regsuc_usr_email);
		mUserRegistryTimeTextView = (TextView)v.findViewById(R.id.regsuc_usr_registry_time);
		
		User user = (User)getArguments().getSerializable(UserRegisterFragment.EXTRA_JSON_REGISTRY_SUCCESS);

        mUserUsernameTextView.setText(String.format(
                getResources().getString(R.string.regsuc_usr_username),
                user.getUsername())
        );
        mUserFirstNameTextView.setText(String.format(
                getResources().getString(R.string.regsuc_usr_first_name),
                user.getFirst_name()
        ));
        mUserSurnameTextView.setText(String.format(
                getResources().getString(R.string.regsuc_usr_surname),
                user.getSurname()
        ));
        mUserEmailTextView.setText(String.format(
                getResources().getString(R.string.regsuc_usr_email),
                user.getEmail()
        ));
        java.text.DateFormat df = DateFormat.getDateFormat(getActivity());
        String timeReg = df.format(user.getTime_reg());
        mUserRegistryTimeTextView.setText(String.format(
                getResources().getString(R.string.regsuc_usr_registry_time),
                timeReg
        ));
		
		mGoMainButton = (Button)v.findViewById(R.id.regsuc_gomain);
		mGoMainButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goBackToMain();
			}
		});
		
		mGoEnrollButton = (Button)v.findViewById(R.id.regsuc_goenroll);
		mGoEnrollButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToEnroll();
			}
		});
		
		return v;
	}
	
	private void goBackToMain() {
		Intent i = new Intent(getActivity(), BioVoiceAppActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}
	
	private void goToEnroll() {
		Intent i = new Intent(getActivity(), VoiceRecognitionActivity.class);
		startActivity(i);
        getActivity().finish();
	}

}
