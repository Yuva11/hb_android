package com.HungryBells.util;

import android.app.Activity;
import android.util.Log;
import android.webkit.WebView;

/**
 * Class to handle all calls from JS & from Java too
 **/
public class JsHandler {
	public static String sJsonResponse;
	Activity activity;
	String TAG = "JsHandler";
	WebView webView;

	public JsHandler(Activity _contxt, WebView _webView) {
		activity = _contxt;
		webView = _webView;
	}

	/**
	 * This function handles call from JS
	 */
	public void sendResponse(String jsString) {
		// showDialog(jsString);
		showResponse(jsString);
		Log.e("Send Response", jsString);
	}

	/**
	 * @author gauravgupta This function will send intent to response page .
	 *         Json value as bundle
	 * */
	public void showResponse(String jsString) {
		sJsonResponse = jsString;
		activity.finish();
		Log.e("show Response", jsString);
		// webView.setVisibility(View.GONE);

	}
}
