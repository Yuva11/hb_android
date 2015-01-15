package com.HungryBells.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.HungryBells.activity.R;
import com.HungryBells.activity.subactivity.UpdateStatus;

public class TwitterDialog extends Dialog implements
		android.view.View.OnClickListener {

	Activity dialogContext;
	UpdateStatus updateStatus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_twitter);
		setCanceledOnTouchOutside(false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		((Button) findViewById(R.id.buttonfeedbacksubmit))
				.setOnClickListener(this);
	}

	public TwitterDialog(Activity context, UpdateStatus updateStatus) {
		super(context);
		this.updateStatus = updateStatus;
		dialogContext = context;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonfeedbacksubmit:
			String editTextComments = ((EditText) findViewById(R.id.editTextComments))
					.getText().toString();
			if (editTextComments != null && editTextComments.length() > 0) {
				// ShareUpdatesStatus.submitTwitts(editTextComments);
				updateStatus.submitTwitts(editTextComments);
				dismiss();
			}
			break;
		default:
			break;
		}
	}

}