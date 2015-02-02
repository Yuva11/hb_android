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
import android.widget.TextView;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.PaymentResponse;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.Util;
import com.google.gson.Gson;


/*This class will call after failed payment*/
public class PaymentFailedActivity extends UserActivity {

    /*payment response from HB server*/
	PaymentResponse paymentResponse;
	String data;

    /*current deal try to buy  by user*/
    Deals viewDeals;

    /*This method initialize app state and get data from payment page*/
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_paymentfailure);
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
			data = getIntent().getExtras().getString("reply");
            Object dealObj = getIntent().getExtras().getSerializable(
                    "Deals");
            viewDeals = (Deals) dealObj;
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

    /*This method check the response from HB server
   Then the result will be shown as datas*/
	private void responseListion() {
		if (getAllResponse(data)) {
			((TextView) findViewById(R.id.textView2))
					.setText("Payment Cancelled");
            ((TextView) findViewById(R.id.textViewsetts))
                    .setText("Transaction Cancelled");

			Drawable image = getResources().getDrawable(
					R.drawable.cancelpayment);
			((ImageView) findViewById(R.id.imageView1)).setImageDrawable(image);
		} else {
            ((TextView) findViewById(R.id.textViewsetts))
                    .setText("Transaction Failed");
			((TextView) findViewById(R.id.textView2)).setText("Payment Failed");
			Drawable image = getResources().getDrawable(
					R.drawable.cancelpayment);
			((ImageView) findViewById(R.id.imageView1)).setImageDrawable(image);
		}
		ContentsCache.getInstance().setContents(
				new HashMap<String, ContentDTO>());

		((Button) findViewById(R.id.buttoncontinue))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
                        superonBackPressed();
                       /* Intent intent = new Intent(PaymentFailedActivity.this, OrderSummaryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("Deals", viewDeals);
                        intent.putExtras(bundle);
                        startActivity(intent);

						finish();*/

					}
				});
	}

    /*parsing json response from HB server and change it to payment pojo */
	private boolean getAllResponse(String data) {
       try {
        String response = "{\"paymentResponse\":" + data + "}";
		response.replace("\n", "");
		Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
		try {
			paymentResponse = datas.paymentResponse;
			if (paymentResponse.getTransactionStatus().equals("CANCELED")) {
				return true;
			}
		} catch (Exception e) {
			Log.e("Json Parsing", e.toString(), e);
			return false;
		}
		return false;
        }catch (Exception e){
            return false;
        }
	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub

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
    private void superonBackPressed() {
       super.onBackPressed();
    }
}
