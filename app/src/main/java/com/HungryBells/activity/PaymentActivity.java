package com.HungryBells.activity;

import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.PaymentDTO;
import com.HungryBells.DTO.PaymentResponse;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.dialog.SettingsDialog;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.Util;
import com.google.gson.Gson;


/*This class is payment activity where user need to pay for deals*/
public class PaymentActivity extends UserActivity {

    /*web page view user need to navigate for payment*/
	WebView _mwebView;

    /*Payment dto for HB server need to load*/
	PaymentDTO urlLoad;

    /*parsiong of webview page interface*/
	JIFace iface = new JIFace();

    /*After sucessful/failed payment  return url*/
	private String responseUrl;


    /*Payment response from HB server*/
	PaymentResponse paymentResponse;

    /*Current deal user try to buy*/
    Deals viewDeals;

    /*Initializing app state ,server connection and get current deal*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_payment);
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
			httpConnection = new ServiceListener(appState);
			_mwebView = (WebView) findViewById(R.id.webViewWebpagePayment);
			urlLoad = (PaymentDTO) getIntent().getExtras().getSerializable(
					"payment");
            Object dealObj = getIntent().getExtras().getSerializable(
                    "Deals");
            viewDeals = (Deals) dealObj;

			Log.e("Payment Returns",
					"Payment Return Url:" + urlLoad.getReturnURL());
			callPayment();
		} else {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
	}

    /*get all data from the webview page*/
	class JIFace {
		public void print(String data) {
			data = "<html>" + data + "</html>";
            Log.e("Payment Url",data);
			if (responseUrl.equals(urlLoad.getReturnURL())
					|| responseUrl.contains("ssl_session_expired_response")) {
				Log.e("Response", data);
				data = html2text(data);
				callNewPage(data);
			}
		}
	}
    /*convert html text to string*/
	private String html2text(String html) {
		return Jsoup.parse(html).text();
	}

    /*This method convert the json response from HB server and convert to payment response pojo*/
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

    /*This method navigate when response received from server after payment attempted by user*/
	private void callNewPage(String data) {
		if (responseUrl.contains("ssl_session_expired_response")) {
			Intent intent = new Intent(this, PaymentFailedActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("reply", "");
            bundle.putSerializable("Deals", viewDeals);
			intent.putExtras(bundle);
			startActivity(intent);
			finish();
		} else {
			if (getAllResponse(data)) {
				Intent intent = new Intent(this, PaymentReturnActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("reply", data);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(this, PaymentFailedActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("reply", data);
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
			}
		}
	}

    /*This method is used to call payment page in webview where url given by HB server*/
	@SuppressLint("SetJavaScriptEnabled")
	private void callPayment() {
		progressBar.show();
		_mwebView.setBackgroundColor(Color.parseColor("#808080"));
		_mwebView.setWebViewClient(new WebViewClient());
		_mwebView.getSettings().setJavaScriptEnabled(true);
		// _mwebView.addJavascriptInterface(new WebAppInterface(this,
		// _jsHandler),
		// "Android");
		_mwebView.addJavascriptInterface(iface, "droid");
		_mwebView.setWebViewClient(new PaymentWebViewClient());
		_mwebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);

				if (newProgress < 100) {

				}

				if (newProgress == 100) {
					if (progressBar != null && progressBar.isShowing()) {
						progressBar.dismiss();
					}
				}
			}

		});
		_mwebView.postUrl(urlLoad.getPaymentURL(),
				EncodingUtils.getBytes(urlLoad.getPaymentPOSTURL(), "UTF-8"));
	}

    /*Webview client that always waiting the webpage loading finished*/
	private class PaymentWebViewClient extends WebViewClient {

		@Override
		public void onPageFinished(WebView view, final String url) {

			super.onPageFinished(view, url);
			Log.d("Url", url);
			String ht = "javascript:window.droid.print(document.getElementsByTagName('html')[0].innerHTML);";
			_mwebView.loadUrl(ht);
			responseUrl = url;
			if (responseUrl.equals(urlLoad.getReturnURL())
					|| responseUrl.contains("ssl_session_expired_response")) {
				_mwebView.setVisibility(View.INVISIBLE);
			}
			// /paymentresponse/cancelledbyUser/{merchantTransactionId}
		}
	}

    /* Concrete method from useractivity used to receive datas from HB Server*/
	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
		progressBar.dismiss();
		super.onBackPressed();

	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub

	}

    /*on back pressed confirmation dialog*/
	@Override
	public void onBackPressed() {
		SettingsDialog exit = new SettingsDialog(this);
		exit.showExitTransaction();
	}

    /*this method will send payment cancellation request to HB server*/
	public void onBackPressed(boolean b) {
		progressBar.setCancelable(false);
		progressBar.show();
		httpConnection = new ServiceListener(appState);
		String url = "paymentresponse/cancelledbyUser/"
				+ urlLoad.getTransactionId();
		httpConnection.sendRequest(url, null, ServiceListenerType.SUBMITDATA,
				SyncHandler, "GET", null);
	}
}
