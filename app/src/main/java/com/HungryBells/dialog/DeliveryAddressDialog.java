package com.HungryBells.dialog;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.HungryBells.activity.OrderSummaryActivity;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.activity.ViewCouponActivity;
import com.HungryBells.activity.ViewDealsActivity;
import com.HungryBells.util.Util;

public class DeliveryAddressDialog extends Dialog implements
		View.OnClickListener {

	UserActivity dialogContext;
    String deliveryAddress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.dialog_deliveryaddress);
		setCanceledOnTouchOutside(false);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        ((EditText) findViewById(R.id.editTextdeliveryaddress)).setText(deliveryAddress);
		((Button) findViewById(R.id.buttondok))
				.setOnClickListener(this);
        ((Button) findViewById(R.id.buttondcancel))
                .setOnClickListener(this);
	}

	public DeliveryAddressDialog(UserActivity context,String address) {
		super(context);
		dialogContext = context;
        deliveryAddress = address;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.buttondok:
			String editTextComments = ((EditText) findViewById(R.id.editTextdeliveryaddress))
					.getText().toString();
			if (editTextComments != null && editTextComments.length() == 0) {
                Util.customToast(dialogContext, dialogContext.getString(R.string.emptyaddress));
			} else if (editTextComments.length() < 10) {
                Util.customToast(dialogContext, dialogContext.getString(R.string.emptyaddresschars));
			} else {
				Log.e("Context", dialogContext.getLocalClassName());
					((OrderSummaryActivity) dialogContext)
							.submitChanges(editTextComments);

				dismiss();
			}
			break;
		default:
            dismiss();
			break;
		}
	}
}