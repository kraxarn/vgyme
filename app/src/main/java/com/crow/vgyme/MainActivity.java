package com.crow.vgyme;

import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crow.vgyme.fragment.FragmentSettings;

public class MainActivity extends AppCompatActivity
{
	private FragmentManager fragmentManager;

	private FragmentSettings fragmentSettings;

	private SharedPreferences prefs;

	public void setTitle(String title)
	{
		if (getSupportActionBar() == null)
			throw new IllegalStateException("No status bar to set title to");

		getSupportActionBar().setTitle(title);
	}

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
		setTitle("settings");
	}
}