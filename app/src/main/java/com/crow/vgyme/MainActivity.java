package com.crow.vgyme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crow.vgyme.fragment.FragmentSettings;

public class MainActivity extends AppCompatActivity
{
	private FragmentSettings fragmentSettings;

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

		// Create fragment
		fragmentSettings = new FragmentSettings();

		// Switch to default fragment
		Tools.setFragment(this, fragmentSettings);

		setContentView(R.layout.activity_main);

		// Set title
		setTitle("Settings");
	}

	@Override
	public void onBackPressed()
	{
		Tools.setFragment(this, fragmentSettings);
		setTitle("Settings");
	}
}