package com.HungryBells.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.util.Util;

public class FeedbackAdDialog extends Dialog implements
		android.view.View.OnClickListener {

	UserActivity dialogContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_feedback);
		setCanceledOnTouchOutside(false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		((Button) findViewById(R.id.buttonfeedbacksubmit))
				.setOnClickListener(this);
	}

	public FeedbackAdDialog(UserActivity context) {
		super(context);
		dialogContext = context;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonfeedbacksubmit:
			String editTextComments = ((EditText) findViewById(R.id.editTextComments))
					.getText().toString();
			if (editTextComments != null && editTextComments.length() == 0) {
                Util.customToast(dialogContext, dialogContext.getString(R.string.emptyreview));

			} else if (editTextComments.length() < 3) {
                Util.customToast(dialogContext, dialogContext.getString(R.string.emptyreviewchars));

			} else {
				((ViewAdsActivity) dialogContext)
						.submitFeedback(editTextComments);
				dismiss();
			}
			break;
		default:
			break;
		}
	}
}