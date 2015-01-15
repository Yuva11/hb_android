package com.HungryBells.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ProgressBar;

import com.HungryBells.activity.R;

public class CustomProgressDialog extends ProgressDialog {
	public CustomProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_custom_progress_dialog);
		ProgressBar prog = (ProgressBar) findViewById(R.id.progressBar1);
		prog.getIndeterminateDrawable().setColorFilter(
				Color.parseColor("#d21617"),
				android.graphics.PorterDuff.Mode.SRC_IN);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void dismiss() {
		super.dismiss();
	}
}
