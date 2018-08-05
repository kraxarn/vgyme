package com.crow.vgyme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.crow.vgyme.fragment.FragmentSettings;
import com.crow.vgyme.fragment.FragmentUploadImage;

import java.io.IOException;

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

		// Check if launched from share menu
		Intent intent = getIntent();
		if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND))
		{
			Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);

			try
			{
				// Get image data
				Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
				String imageType = getContentResolver().getType(uri);

				// Launch upload image
				FragmentUploadImage uploadImage = new FragmentUploadImage();
				uploadImage.setImage(image, imageType);
				Tools.setFragment(this, uploadImage);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Tools.showDialog(this, "Image not found", e.getMessage());
			}
		}
	}

	@Override
	public void onBackPressed()
	{
		Tools.setFragment(this, fragmentSettings);
		setTitle("Settings");
	}
}