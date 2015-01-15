package com.HungryBells.activity.intefaceImpl;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ErrorDescription;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.intefaces.LoginParsing;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoginUpdate implements LoginParsing {
	Activity context;
	UndoBarController mUndoBarController;
	GlobalAppState appState;

	@Override
	public boolean successfullLogin(Bundle data, Activity context,
			UndoBarController bar, GlobalAppState appState) {
		mUndoBarController = bar;
		this.context = context;
		this.appState = appState;
		return jsonParsing(data);
	}

	private boolean jsonParsing(Bundle data) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		String response = data.getString(ServiceListener.RESPONSEDATA);
		Gson gson = gsonBuilder.create();
		try {
			if (response.contains("errorDescription")) {
				ErrorDescription errorDescription = new ErrorDescription();
				errorDescription = gson.fromJson(response,
						ErrorDescription.class);
                Util.customToast(context,errorDescription.getMessage());
				/*mUndoBarController.showUndoBar(true,
						errorDescription.getMessage(), null);*/
				return false;
			} else {
				Customers profile = new Customers();
				profile = gson.fromJson(response, Customers.class);
				if (profile != null) {
					String serializedData = profile.serialize();
					SharedPreferences preferencesReader = context
							.getSharedPreferences("HB", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = preferencesReader.edit();
					editor.putString("Profile", serializedData);
					editor.apply();
					appState.setProfile(profile);
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
			return false;
		}
	}
}
