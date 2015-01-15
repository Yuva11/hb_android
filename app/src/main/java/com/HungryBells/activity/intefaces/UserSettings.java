package com.HungryBells.activity.intefaces;

import android.app.Activity;

import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.CustomProgressDialog;

public interface UserSettings {
	public abstract void userSettingsPage(CustomProgressDialog progressBar,
			ServiceListener httpConnection, Activity context,
			GlobalAppState appState);
}
