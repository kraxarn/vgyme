package com.crow.vgyme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public abstract class Tools
{
	private static Fragment currentFragment;

	public static void setFragment(Activity source, Fragment fragment)
	{
		FragmentManager manager = source.getFragmentManager();

		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.container, fragment);

		if (Build.VERSION.SDK_INT >= 24)
			transaction.commitNow();
		else
		{
			transaction.addToBackStack(null);
			transaction.commit();
		}

		currentFragment = fragment;
	}

	public static Fragment getFragment()
	{
		return currentFragment;
	}

	public static void showDialog(Context context, String title, String message)
	{
		AlertDialog.Builder builder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
			builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
		else
			builder = new AlertDialog.Builder(context);

		builder
				.setTitle(title)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
					}
				})
				.show();
	}

	public static void openBrowser(Context context, String url)
	{
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	public static void setClipboard(Context context, String label, String text)
	{
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = ClipData.newPlainText(label, text);
		manager.setPrimaryClip(data);
	}
}
