package com.HungryBells.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.mobileapptracker.MobileAppTracker;

import org.jsoup.Jsoup;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("SetJavaScriptEnabled")


public class SocialAuthActivity extends UserActivity {

    /*progressbar for loading*/
	ProgressBar progressBar;

    /*web page loading view*/
	WebView mWebView;

    /*Timer instance */
	Timer timer;

    /*url for authentication*/
	String authenticationUrl;

    /*activity starttime*/
	long startTime;

    /* Memember for timer task */
	final Handler handler = new Handler();

    /*Timertask instance */
	TimerTask timerTask;

    /*parsiong of webview page interface*/
	JIFace iface = new JIFace();

    /*After sucessful login social media return url*/
	String returnUrl = "http://hbells.finateltech.com:7070";

   /*initialize timer task
    get authenticaition from previous activity*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		appState = (GlobalAppState) getApplication();
		mUndoBarController = new UndoBarController(
					findViewById(R.id.undobar), this);
		easyTracker.send(MapBuilder.createEvent("Socail Media",
					"Social Media login", "FB", null).build());
		authenticationUrl = getIntent().getExtras().getString("authUrl");
		progressBar = (ProgressBar) findViewById(R.id.progressBarSocialAuth);
		startTime = new Date().getTime();
		timer = new Timer();
		initializeTimerTask();
		timer.schedule(timerTask, 5000, 1000);
	}

	/**
	 * Set up WebView to load the provider URL
	 * 
	 */
	private void setUpWebView() {
		mWebView = (WebView) findViewById(R.id.webViewForLogin);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.setWebViewClient(new SocialAuthWebViewClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.loadUrl(authenticationUrl);
		mWebView.addJavascriptInterface(iface, "droid");
	}

    /*get all data from the webview page in the from of string */
	class JIFace {
		public void print(String data) {
			data = "<html>" + data + "</html>";

            if(data.contains("Whitelabel Error Page")){
                onBackPressed();
                return;
            }
			String response = Jsoup.parseBodyFragment(data).body().text();
			if (response.contains("authenticationId")) {
				if (response.contains("favouriteCusines")) {
					callProfile(response);
				}

			}
		}
	}



    /*Webview client that always waiting the webpage loading finished*/
	private class SocialAuthWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.d("SocialAuth-Override ", "Override url: " + url);
			return false;

		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e("errorCode", errorCode + ":" + description + ":" + failingUrl);
			// response.onError(new SocialAuthError(description, new Exception(
			// failingUrl)));
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d("SocialAuth-WebView ", "Override url Page Finished: " + url);
		}

		@Override
		public void onPageFinished(WebView view, final String url) {

			super.onPageFinished(view, url);
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
			String ht = "javascript:window.droid.print(document.getElementsByTagName('html')[0].innerHTML);";
			mWebView.loadUrl(ht);
			Log.e("Return Url", returnUrl);
			if (url.equals(getString(R.string.serverurl_test))
					|| url.equals(getString(R.string.serverurl_test)+"#_=_")) {
				mWebView.setVisibility(View.INVISIBLE);
                CustomProgressDialog progressDialog= new CustomProgressDialog(SocialAuthActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.show();
			}
			if (progressBar != null)
				progressBar.setVisibility(View.GONE);

		}
	}



	@Override
	public void onBackPressed() {
        startActivity(new Intent(this,LoginActivity.class));
        finish();;
	}

    /*This method is used to convert the HB profile response to profile pojo and store it in shared preference
    * After that navigating to deals page
    * */
	private void callProfile(String data) {

		Customers myData = new Customers();
		String response = "{\"profile\":" + data + "}";
		response.replace("\n", "");
		Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
		try {
			myData = datas.profile;

            // Registering successful event with the MAT server
            MobileAppTracker mobileAppTracker = MobileAppTracker.getInstance();
            if (!myData.getEmail().isEmpty()) mobileAppTracker.setUserEmail(myData.getEmail());
            if (!myData.getAuthenticationId().isEmpty()) mobileAppTracker.setUserId(myData.getAuthenticationId());
            mobileAppTracker.measureAction("933029118");
            //mobileAppTracker.measureAction("registration");

		} catch (Exception e) {
			Log.e("Json Parsing", e.toString(), e);
		}
		String serializedData = myData.serialize();
		GlobalAppState appState = (GlobalAppState) getApplication();
		appState.setProfile(myData);
		SharedPreferences preferencesReader = getSharedPreferences("HB",
				MODE_PRIVATE);
		SharedPreferences.Editor editor = preferencesReader.edit();
		editor.putString("Profile", serializedData);
		editor.apply();
		startActivity(new Intent(this, DealsActivity.class));
		finish();
		overridePendingTransition(0, R.anim.login_signupanimation);

	}

    /*This method is used to used to initialze timer for connection checking*/
	public void initializeTimerTask() {

		timerTask = new TimerTask() {
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						if (new Date().getTime() - startTime >= 90000) {
							showSettingsAlert();
						}
					}
				});
			}
		};
	}

    /*This method is used to show alert dialog after a time interval because of slow connection*/
	private void showSettingsAlert() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		final Dialog dialog = new Dialog(SocialAuthActivity.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog_customnetwork);
		TextView message = (TextView) dialog.findViewById(R.id.textViewnwtext);
		message.setText("Network timeout.Do you want to continue?");
		Button okButton = (Button) dialog.findViewById(R.id.buttonnwok);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		Button cancelButton = (Button) dialog.findViewById(R.id.buttonnwcancel);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (progressBar != null)
					progressBar.setVisibility(View.GONE);
				finish();
				dialog.cancel();
			}
		});
		try {
			dialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		setUpWebView();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub
	}

}
