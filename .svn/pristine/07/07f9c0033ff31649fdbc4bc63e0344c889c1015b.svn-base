package com.HungryBells.activity;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.PaymentResponse;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.Util;
import com.google.gson.Gson;

public class PaymentReturnActivity extends UserActivity {
	PaymentResponse paymentResponse;
	String data;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_paymentsucess);
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null) {
            try {
                data = getIntent().getExtras().getString("reply");
                actionBarCreation(this);
            }catch (Exception e){
                startActivity(new Intent(this, DealsActivity.class));
                finish();
            }
		} else {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		// transactionStatus
	}

	@Override
	protected void onStart() {
		super.onStart();
		responseListion();
	}

	private void responseListion() {
		Typeface face = Typeface.createFromAsset(getAssets(), "forte.ttf");
		TextView textEnjoy = (TextView) findViewById(R.id.textViewenjoy);
		textEnjoy.setTypeface(face);
		ContentsCache.getInstance().setContents(
				new HashMap<String, ContentDTO>());
		if (getAllResponse(data)) {

			((TextView) findViewById(R.id.textViewOrderId))
					.setText(" # " + paymentResponse.getOrderID());
			Drawable myDrawable = getResources().getDrawable(
					R.drawable.finishedpage);
			((ImageView) findViewById(R.id.imageView1))
					.setImageDrawable(myDrawable);
			textEnjoy.setText("Enjoy your meals");

		} else {
			((RelativeLayout) findViewById(R.id.backgroundresponse))
					.setBackgroundResource(R.drawable.payment_roundedcorner);
			((TextView) findViewById(R.id.textViewOrderId))
					.setVisibility(View.GONE);

			Drawable myDrawable = getResources().getDrawable(
					R.drawable.dealnotavailable1);
			((ImageView) findViewById(R.id.imageView1))
					.setImageDrawable(myDrawable);
			((TextView) findViewById(R.id.textView2)).setText("Payment Failed");
			textEnjoy.setText("Oops! Please Retry");
		}
		((Button) findViewById(R.id.buttoncontinue))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
                        appState.setSelectedItem(1);

						startActivity(new Intent(PaymentReturnActivity.this,
								DealsActivity.class));

					}
				});
	}

	private boolean getAllResponse(String data) {
		String response = "{\"paymentResponse\":" + data + "}";
		response.replace("\n", "");
		Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
		try {
			paymentResponse = datas.paymentResponse;
			if (paymentResponse.getTransactionStatus().equals("SUCCESS")) {
				return true;
			}
		} catch (Exception e) {
			Log.e("Json Parsing", e.toString(), e);
			return false;
		}
		return false;
	}

	@Override
	public void onUndo(Parcelable token) {

	}

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, DealsActivity.class));
		finish();
	}

}
