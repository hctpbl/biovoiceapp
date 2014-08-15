package com.hctpbl.biovoiceapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BioVoiceAppFragment extends Fragment {
	
	FragmentManager fm;
	
	private Button mRegisterButton;
	private Button mVoiceAccessButton;

	public BioVoiceAppFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		fm = getFragmentManager();
		
		View v = inflater.inflate(R.layout.fragment_bio_voice_app,
				container, false);
		mRegisterButton = (Button)v.findViewById(R.id.user_register);
		mRegisterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), UserActivity.class);
				startActivity(i);
			}
		});
		
		mVoiceAccessButton = (Button)v.findViewById(R.id.user_voice_access);
		mVoiceAccessButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), VoiceRecognitionActivity.class);
				startActivity(i);
			}
		});
		return v;
	}
}
