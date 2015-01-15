package com.HungryBells.activity.subactivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.HungryBells.DTO.Customers;
import com.HungryBells.activity.MainActivity;
import com.HungryBells.activity.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMRegistrationActivity {
	Activity context;
	final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	GoogleCloudMessaging gcm;
	SharedPreferences prefs;
	String regid;

	public GCMRegistrationActivity(Activity context) {
		this.context = context;
	}

	public void GCMRegister() {
         Log.e("Inside","Inside GCM");
		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(context);
			regid = getRegistrationId(context);
            Log.e("RegId","RegId"+regid);
			if (regid.isEmpty()||regid.length()==0) {
				registerInBackground();
			}
		} else {
			Log.i("GCMRegistrationActivity",
					"No valid Google Play Services APK found.");
		}
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i("GCMRegistrationActivity", "Registration not found.");
			return "";
		}
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, 1);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            this.prefs = context.getSharedPreferences("HB", Context.MODE_PRIVATE);
            String profileSerialized = this.prefs.getString("Profile", "");
            Customers profile = Customers.create(profileSerialized);
            try {
                if(profile.getAuthenticationId()!=null){
                    profile.setAuthenticationId("");
                }
                Customers myData = profile;

                String serializedData = myData.serialize();
                SharedPreferences.Editor editor = this.prefs.edit();
                editor.putString("Profile", serializedData);
                editor.apply();
            }catch(Exception e){

            }

            return "";
        }
		return registrationId;
	}
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {

            throw new RuntimeException("Could not get package name: " + e);
        }
    }
	private SharedPreferences getGCMPreferences(Context context) {
		return context.getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil
						.getErrorDialog(resultCode, context, 9000).show();
			} else {
				Log.i("GCMRegistrationActivity",
						"This device is not supported.");
			}
			return false;
		}
		return true;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void registerInBackground() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm
							.register(context.getString(R.string.reg_gcm_id));
					msg = "Device registered, registration ID=" + regid;
                    Log.i("GCMRegistrationActivity", msg);
					storeRegistrationId(context, regid);
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;

			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		SharedPreferences.Editor editor = prefs.edit();
        int appVersion = getAppVersion(context);
		editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.apply();
	}
}
