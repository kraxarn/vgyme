package com.crow.vgyme;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;

public abstract class Tools
{
	public static String version = "1.0.0";

	public static void setFragment(MainActivity source, Fragment fragment)
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
				.setIcon(android.R.drawable.ic_dialog_info)
				.show();
	}

	public static void openBrowser(Context context, String url)
	{
		context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
}
