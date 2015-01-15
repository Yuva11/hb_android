package com.HungryBells.activity.intefaces;

import android.app.Activity;
import android.os.Handler;

import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.CustomProgressDialog;

public interface ProfileUpdate {
	public abstract void profileUpdate(Activity context, 
			ServiceListener connection, CustomProgressDialog progressBar,
			Handler SyncHandler);
}
