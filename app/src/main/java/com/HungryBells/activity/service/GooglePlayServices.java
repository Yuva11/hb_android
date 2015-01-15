package com.HungryBells.activity.service;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Rajesh on 11/10/2014.
 */
public class GooglePlayServices implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	// Global constants

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 60;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL =10* MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL =10* MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;
	@SuppressWarnings("unused")
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	// Define an object that holds accuracy and frequency parameters
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Context context;
	private Intent mIntentService;
	private PendingIntent mPendingIntent;
	LocationManager locationManager;

	public GooglePlayServices(Context context) {
		this.context = context;
	}

	/*
	 * Called by Location Services when the request to connect the client
	 * finishes successfully. At this point, you can request the current
	 * location or start periodic updates
	 */
	@Override
	public void onConnected(Bundle dataBundle) {
		// Display the connection status
		Log.e("GooglePlayServices", "onConnected :: Location client connected");
		// locationclient.setMockMode(true);
		locationclient.requestLocationUpdates(locationrequest, mPendingIntent);


	}

	/*
	 * Called by Location Services if the connection to the location client
	 * drops because of an error.
	 */
	@Override
	public void onDisconnected() {
		// Display the connection status
		Log.e("LocationService", "onDisconnected :: Serious error occurred");
	}

	/*
	 * Called by Location Services if the attempt to Location Services fails.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
		Log.e("LocationService", "onConnectionFailed :: Serious error occurred");

	}

	public void start() {
		Log.i("GooglePlayServices", "start method");
		mIntentService = new Intent(context, LocationIntentService.class);
		mPendingIntent = PendingIntent
				.getService(context, 1, mIntentService, 0);
		// Create the LocationRequest object
		locationclient = new LocationClient(context, this, this);

		locationrequest = LocationRequest.create();
		// Use moderate accuracy
		locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		// Set the update interval to 5 seconds
		locationrequest.setInterval(UPDATE_INTERVAL);
		// Set the fastest update interval to 1 second
		locationrequest.setFastestInterval(FASTEST_INTERVAL);
		locationclient.connect();

	}

}
