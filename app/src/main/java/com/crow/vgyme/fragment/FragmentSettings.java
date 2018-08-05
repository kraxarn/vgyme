package com.crow.vgyme.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;

import com.crow.vgyme.BuildConfig;
import com.crow.vgyme.R;
import com.crow.vgyme.Tools;

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

		// Check if user key is set
		findPreference("userKey").setSummary("User key for account " + (prefs.getString("userKey", "").length() > 0 ? "(set)" : "(not set)"));
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		switch (preference.getKey())
		{
			case "uploadImage":
				Tools.setFragment(getActivity(), new FragmentUploadImage());
				break;

			case "userKeyButton":
				Tools.openBrowser(getActivity(), "https://vgy.me/account/details");
				break;

			case "openProjectPage":
				Tools.openBrowser(getActivity(), "https://github.com/kraxarn/vgyme");
				break;
		}

		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}
}