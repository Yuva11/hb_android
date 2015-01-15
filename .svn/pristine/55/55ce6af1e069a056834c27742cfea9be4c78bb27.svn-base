package com.HungryBells.activity.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.LocationClient;

/**
 * An {@link android.app.IntentService} subclass for handling asynchronous task
 * requests in a service on a separate handler thread.
 * <p>
 * 
 * helper methods.
 */
public class LocationIntentService extends IntentService {

	public static final String BROADCAST_ACTION = "LOCATIONUPDATE";

	public LocationIntentService() {
		super("LocationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("Location service", "onHandleIntent called");
		try {
			if (intent != null) {
				Location location = intent
						.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
				if (location != null) {
					Log.i("LocationService",
							"onHandleIntent " + location.getLatitude() + ","
									+ location.getLongitude() + "Provider"
									+ location.getProvider());
					Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(
							LocationClient.KEY_LOCATION_CHANGED, location);
					LocalBroadcastManager.getInstance(this).sendBroadcast(
							localIntent);
					Log.i("LocationIntentService",
							"Location update broadcasted");

				} else {
					Log.e("LocationIntentService",
							"Received null location in service");
				}
			} else {
				Log.e("LocationIntentService", "Received null location intent");

			}
		} catch (Exception e) {
			Log.e("LocationIntentService", e.getMessage());
		}
	}

}
