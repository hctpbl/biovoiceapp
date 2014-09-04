package com.hctpbl.biovoiceapp;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Activity for the settings of our app. It only has the API
 * URL in this first version.
 */
public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    /**
     * Stores the public URL for the API
     */
	public static final String KEY_PREF_API_URL = "pref_api_url";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, false);
		EditTextPreference prefApiUrl = (EditTextPreference)findPreference(KEY_PREF_API_URL);
		prefApiUrl.setSummary(prefApiUrl.getText());
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Preference pref = findPreference(key);
		
		if (pref instanceof EditTextPreference) {
			((EditTextPreference) pref).setSummary(sharedPreferences.getString(key, ""));
		}
	}
	
	
}
