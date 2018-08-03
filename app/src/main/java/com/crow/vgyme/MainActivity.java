package com.crow.vgyme;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity
{
	private FragmentManager fragmentManager;

	private FragmentSettings fragmentSettings;

	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Create preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Create fragment manager
		fragmentManager = getFragmentManager();

		// Create fragment
		fragmentSettings = new FragmentSettings();

		// Switch to default fragment
		Tools.setFragment(this, fragmentSettings);

		setContentView(R.layout.activity_main);

		// Set title
		getSupportActionBar().setTitle("vgyme settings");
	}
}