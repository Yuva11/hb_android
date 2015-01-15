package com.HungryBells.util;

import android.app.Activity;

public class WebAppInterface {
	public Activity mContext;
	public JsHandler mJSHandler;

	public WebAppInterface(Activity context, JsHandler responseHandler) {
		this.mContext = context;
		this.mJSHandler = responseHandler;
	}

	public void loadWalletResponse(String response) {
		mJSHandler.showResponse(response);
	}
}
