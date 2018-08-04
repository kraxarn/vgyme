package com.crow.vgyme.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.crow.vgyme.MainActivity;
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

import cz.msebera.android.httpclient.Header;

import static android.app.Activity.RESULT_OK;

public class FragmentUploadImage extends Fragment
{
	private View view;

	private SharedPreferences prefs;

	private static int PICK_IMAGE;

	private Bitmap image;

	private String imageType;

	private boolean uploadToAccount;

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

		// Upload to account switch
		// This seriously feels like a hack
		uploadToAccount = false;

		((Switch) view.findViewById(R.id.uploadToAccount)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked)
			{
				uploadToAccount = checked;
			}
		});

		// Upload button
		view.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				setUploading(true);

				if (image == null)
				{
					Tools.showDialog(getActivity(), "No image", "Select an image to upload first");
					setUploading(false);
				}
				else
				{
					ByteArrayOutputStream stream = new ByteArrayOutputStream();

					switch (imageType)
					{
						case "image/jpeg":
							image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
							break;

						case "image/png":
						case "image/webp":
							image.compress(Bitmap.CompressFormat.PNG, 100, stream);
							break;

						case "image/gif":
							Tools.showDialog(getActivity(), "Format not supported", "Sorry, but gif files aren't supported at this time");
							setUploading(false);
							return;

						default:
							Tools.showDialog(getActivity(), "Format not supported", String.format("Format %s is not supported by vgy.me", imageType));
							setUploading(false);
							return;
					}

					byte[] bytes = stream.toByteArray();

					// Create web client
					AsyncHttpClient client = new AsyncHttpClient();

					// Create parameters
					RequestParams params = new RequestParams();
					params.put("file", new ByteArrayInputStream(bytes), "image.png");

					if (uploadToAccount && prefs.getString("userKey", null) != null)
					{
						params.put("userkey", prefs.getString("userKey", null));
					}

					client.post("https://vgy.me/upload", params, new AsyncHttpResponseHandler()
					{
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody)
						{
							String response = new String(responseBody);

							Log.i("IMAGE_UPLOAD", "Success");
							Log.i("HTTP_RESPONSE", response);

							JSONObject json;
							String link0;

							try
							{
								json = new JSONObject(response);
								link0 = json.getString("image");
							}
							catch (JSONException e)
							{
								e.printStackTrace();
								Tools.showDialog(getActivity(), "Invalid response", "Invalid response, check logcat for details");

								setUploading(false);
								return;
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
							Log.i("IMAGE_UPLOAD", "Failed");
							Log.i("HTTP_RESPONSE", error.getMessage());
							Tools.showDialog(getActivity(), "Failed to upload image", error.getMessage());
							setUploading(false);
						}

						@Override
						public void onFinish()
						{
							super.onFinish();
							setUploading(false);
						}
					});
				}
			}
		});

		((MainActivity) getActivity()).setTitle("Upload Image");

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
				imageType = getActivity().getContentResolver().getType(uri);

				// Check so it's an actual image
				if (!imageType.startsWith("image/"))
				{
					Tools.showDialog(getActivity(), "Invalid image", "That doesn't look like an image to me");
					return;
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Tools.showDialog(getActivity(), "Image not found", e.getMessage());
			}

			((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(image);
		}
	}

	private void setUploading(boolean uploading)
	{
		view.findViewById(R.id.upload).setEnabled(!uploading);
		((Button) view.findViewById(R.id.upload)).setText(uploading ? "Uploading..." : "Upload");
	}
}
