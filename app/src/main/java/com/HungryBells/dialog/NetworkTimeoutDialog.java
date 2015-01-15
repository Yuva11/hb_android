package com.HungryBells.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.HungryBells.activity.R;

public class NetworkTimeoutDialog extends Dialog implements
		android.view.View.OnClickListener {
	Activity context;

	public NetworkTimeoutDialog(Activity _context) {
		super(_context);
		context = _context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_networktimeout);
		setCancelable(false);
		TextView message = (TextView) findViewById(R.id.textViewnwtext);
		message.setText("Please Enter the Url");
		Button okButton = (Button) findViewById(R.id.buttonnwok);
		okButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		dismiss();
		context.finish();
	}

}
