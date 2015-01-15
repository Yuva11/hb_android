package com.HungryBells.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;

import com.HungryBells.activity.OrderSummaryActivity;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;

public class ChangeQuantity extends Dialog implements
		android.view.View.OnClickListener {

	Context dialogContext;
	NumberPicker np;
	int value;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_counter);
		setCanceledOnTouchOutside(false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		np = (NumberPicker) findViewById(R.id.np);
		np.setMaxValue(20);
		np.setMinValue(1);
		np.setValue(value);
		np.setWrapSelectorWheel(false);
		((Button) findViewById(R.id.buttonnwok)).setOnClickListener(this);
		((Button) findViewById(R.id.buttonnwcancel)).setOnClickListener(this);
	}

	public ChangeQuantity(UserActivity context, int value) {
		super(context);
		dialogContext = context;
		this.value = value;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttonnwok:
			((OrderSummaryActivity) dialogContext)
					.changeQuantity(np.getValue());
			dismiss();
			break;
		case R.id.buttonnwcancel:
			dismiss();
			break;
		default:
			break;
		}
	}

}