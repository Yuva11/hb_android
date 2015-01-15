package com.HungryBells.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.MainActivity;
import com.HungryBells.activity.PaymentActivity;
import com.HungryBells.activity.R;

public class SettingsDialog {
	Activity context;

	public SettingsDialog(Activity _context) {
		context = _context;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Network Settings");
		alertDialog
				.setMessage("Network is not enabled! Please enable network provider in settings menu");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        context.startActivityForResult(new Intent(
								Settings.ACTION_SETTINGS), 2);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						((MainActivity) context).onBackPressed();
					}
				});

		alertDialog.show();
	}

	public void showExitAlert() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_exitvalue);
        dialog.setCanceledOnTouchOutside(false);
		TextView message = (TextView) dialog.findViewById(R.id.textViewnwtext);
		message.setText("Do you want to exit from HungryBells ?");
		Button okButton = (Button) dialog.findViewById(R.id.buttonnwok);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				((DealsActivity) context).onBackPressed(true);

			}
		});
		Button cancelButton = (Button) dialog.findViewById(R.id.buttonnwcancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
                ((DealsActivity) context).onBackPressed(false);
				dialog.cancel();
			}
		});

		 dialog.show();

	}

	public void showExitTransaction() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_exitvalue);
		TextView titles = (TextView) dialog.findViewById(R.id.textViewnwtitle);
		titles.setText("Payment");
		TextView message = (TextView) dialog.findViewById(R.id.textViewnwtext);
		message.setText("Do you want to cancel ?");
		Button okButton = (Button) dialog.findViewById(R.id.buttonnwok);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
				((PaymentActivity) context).onBackPressed(true);

			}
		});
		Button cancelButton = (Button) dialog.findViewById(R.id.buttonnwcancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});

		dialog.show();

	}

}
