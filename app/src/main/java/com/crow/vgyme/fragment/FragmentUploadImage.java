package com.crow.vgyme.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import com.crow.vgyme.R;
import com.crow.vgyme.Tools;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

public class FragmentUploadImage extends Fragment
{
	private View view;

	private SharedPreferences prefs;

	private String path;

	private static int PICK_IMAGE;

	private Bitmap image;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
	{
		// Create view
		view = inflater.inflate(R.layout.fragment_upload_image, container, false);

		// Get preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		// Enable 'upload to account'
		if (prefs.contains("userKey"))
		{
			view.findViewById(R.id.uploadToAccount).setEnabled(true);
		}

		// Select image button
		view.findViewById(R.id.selectImage).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
			}
		});

		// Upload button
		view.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				view.findViewById(R.id.upload).setEnabled(false);
				((Button) view.findViewById(R.id.upload)).setText("Uploading...");

				if (image == null)
				{
					Tools.showDialog(getActivity(), "No image", "Select an image to upload first");
				}
				else
				{
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					image.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] bytes = stream.toByteArray();

					// Create web client
					AsyncHttpClient client = new AsyncHttpClient();

					// Create parameters
					RequestParams params = new RequestParams();
					params.put("file", new ByteArrayInputStream(bytes), "image.png");

					if (((Switch) view.findViewById(R.id.uploadToAccount)).isChecked() && prefs.getString("userKey", null) != null)
					{
						params.put("userkey", prefs.getString("userKey", null));
					}

					final View rootView = view;

					client.post("https://vgy.me/upload", params, new AsyncHttpResponseHandler()
					{
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
						{
							String response = new String(responseBody);

							Log.i("IMAGE_UPLOAD", "Success");
							Log.i("HTTP_RESPONSE", response);

							JSONObject json;
							String link0 = null;

							try
							{
								json = new JSONObject(response);
								link0 = json.getString("image");
							}
							catch (JSONException e)
							{
								e.printStackTrace();
								Tools.showDialog(getActivity(), "Invalid response", "Invalid response, check logcat for details");
							}

							final String link = link0;

							// I really need to start learning Java
							String[] items = { "Open link", "Copy to clipboard"};
							final ArrayList<Integer> selectedItems = new ArrayList<>();

							AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
							builder
									.setTitle("Upload complete!")
									.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener()
									{
										@Override
										public void onClick(DialogInterface dialogInterface, int index, boolean checked)
										{
											if (checked)
												selectedItems.add(index);
											else
												selectedItems.remove(Integer.valueOf(index));
										}
									})
									.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
									{
										public void onClick(DialogInterface dialog, int which)
										{
											if (selectedItems.contains(0))
												Tools.openBrowser(getActivity(), link);

											if (selectedItems.contains(1))
												Tools.setClipboard(getActivity(), "image", link);
										}
									})
									.show();
						}

						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
						{
							String response = new String(responseBody);

							Log.i("IMAGE_UPLOAD", "Failed");
							Log.i("HTTP_RESPONSE", response);
							Tools.showDialog(getActivity(), "Error", "Failed to upload image, check logcat for details");
						}

						@Override
						public void onFinish()
						{
							super.onFinish();

							rootView.findViewById(R.id.upload).setEnabled(true);
							((Button) rootView.findViewById(R.id.upload)).setText("Upload");
						}
					});
				}
			}
		});

		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null)
		{
			Uri uri = data.getData();

			image = null;
			try
			{
				image = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Tools.showDialog(getActivity(), "Image not found", e.getMessage());
			}

			((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(image);
		}
	}
}
