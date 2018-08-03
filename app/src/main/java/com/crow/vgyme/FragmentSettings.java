package com.crow.vgyme;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

public class FragmentSettings extends PreferenceFragment
{
	private int clicks;

	private SharedPreferences prefs;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Set preference page from xml
		addPreferencesFromResource(R.xml.preferences);

		// Create shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// Set app version
		findPreference("appVersion").setTitle(String.format("Version %s", BuildConfig.VERSION_NAME));
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		if (preference.getKey().equals("uploadImage"))
			Tools.showDialog(getActivity(), "Sorry", "NotImplemented");
		else if (preference.getKey().equals("userKeyButton"))
			Tools.openBrowser(getActivity(), "https://vgy.me/account/details");

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}