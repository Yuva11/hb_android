package com.HungryBells.activity;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;



/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 * Code from android developer page
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver  {


	@Override
	public void onReceive(Context context, Intent intent) {
       ComponentName comp = new ComponentName(context.getPackageName(),GCMReceiver.class.getName());
       startWakefulService(context, (intent.setComponent(comp)));
       Log.e("Message","Inside Receiver");
       setResultCode(Activity.RESULT_OK);

	}



}