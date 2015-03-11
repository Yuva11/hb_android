package com.HungryBells.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.HungryBells.DTO.ADSettingsDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.User;
import com.HungryBells.DTO.VersonDetailsDTO;
import com.HungryBells.StaggeredGridView.ProgressWheel;
import com.HungryBells.activity.service.GooglePlayServices;
import com.HungryBells.activity.subactivity.AnimationRunActivty;
import com.HungryBells.activity.subactivity.GCMRegistrationActivity;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.FontOverride;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import com.mobileapptracker.MobileAppTracker;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import android.provider.Settings.Secure;



 /*This class is mainly used to show bell animation*/
public class MainActivity extends UserActivity {

 /*handler method*/
    Handler handler;

   /*Activity start time*/
    long startTime;

   /*sub activity to show bell animation*/
    AnimationRunActivty animations;

 /*Not used*/
    public static int width;

     /* Global application state Object where all contextual information is stored */
    private GlobalAppState appState;


   /*Progressbar used to shoe on update time;*/
    ProgressWheel progressBarWheel;

     public MobileAppTracker mobileAppTracker = null;

     /*Constructor method*/
    public MainActivity() {
        handler = new Handler();
        animations = new AnimationRunActivty(this);
        startTime = new Date().getTime();
    }

     /*
   * Initiaizing handler,ProgressWheel
   *
   * OnCreate Method for Main Activity page
   * */
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            FontOverride.setDefaultFont(this, "DEFAULT", "tungab.ttf");
            progressBarWheel = (ProgressWheel) findViewById(R.id.progressBarTwo);
            android.util.Log.d("Main Activity", "On Create Page Called");
            mUndoBarController = new UndoBarController(
                    findViewById(R.id.undobar), this);
            appState = (GlobalAppState) getApplication();
            prefs = getSharedPreferences("HB", MODE_PRIVATE);
            easyTracker.send(MapBuilder.createEvent("Main",
                    "Animation Process", "Main", null).build());

            animations.runAnimation(R.id.imageViewlogo);
            progressBarWheel.setVisibility(View.GONE);
            Display display = getWindowManager().getDefaultDisplay();
            width = display.getWidth();
            appState.setUrl(getString(R.string.serverurl_test));
            GooglePlayServices gs = new GooglePlayServices(this);
            gs.start();
            IntentFilter mIntentFilter = new IntentFilter("LOCATIONUPDATE");
            LocationReceiver mLocationReceiver = new LocationReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mLocationReceiver, mIntentFilter);
            registrations();


            // Initialize MAT
            MobileAppTracker.init(getApplicationContext(), "174262", "0275ecc3449ce2aed43bcfd5bd8cc6de");
            mobileAppTracker = MobileAppTracker.getInstance();

            // If your app already has a pre-existing user base before you implement the MAT SDK, then
            // identify the pre-existing users with this code snippet.
            // Otherwise, MAT counts your pre-existing users as new installs the first time they run your app.
            // Omit this section if you're upgrading to a newer version of the MAT SDK.
            // This section only applies to NEW implementations of the MAT SDK.
            //boolean isExistingUser = ...
            //if (isExistingUser) {
            //    mobileAppTracker.setExistingUser(true);
            //}

            // Collect Google Play Advertising ID; REQUIRED for attribution of Android apps distributed via Google Play
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // See sample code at http://developer.android.com/google/play-services/id.html
                    try {
                        Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                        mobileAppTracker.setGoogleAdvertisingId(adInfo.getId(), adInfo.isLimitAdTrackingEnabled());
                    } catch (Exception e) {
                        // Encountered an error getting Google Advertising Id, use ANDROID_ID
                        mobileAppTracker.setAndroidId(Secure.getString(getContentResolver(), Secure.ANDROID_ID));
                    }

                    // Check if deferred deeplink can be opened
                    // Uncomment this line if your MAT account has enabled deferred deeplinks
                    //mobileAppTracker.checkForDeferredDeeplink(750);
                }
            }).start();



        } catch (Exception e) {
            Util.customToast(this, getString(R.string.internalerror));
            android.util.Log.e("Internal error", e.toString(), e);
        }

        // New google analytics v4
        //***************************//
        try
        {
            // Get tracker.
            Tracker t = ((GlobalAppState) getApplication()).getTracker(
                    GlobalAppState.TrackerName.APP_TRACKER);

            // Set screen name.
            t.setScreenName("Main Activity");

            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());

            // Enable Advertising Features.
            t.enableAdvertisingIdCollection(true);
        }
        catch(Exception  e)
        {
            Toast.makeText(getApplicationContext(), "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
   /*This method invoke after onback pressed on the time of settings page*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Thread looperThread = new Thread(new Runnable() {
                public void run() {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            registrations();
                        }
                    }, 6000);
                }
            });
            looperThread.start();

        }catch (Exception e){
            Log.e("Activity","Activity result Error",e);
        }


    }

     /*This method is used to regiter device for google cloud messaging*/
    private void registrations(){
        if (Util.checkNetwork(this)) {
            GCMRegistrationActivity gcm = new GCMRegistrationActivity(this);
            gcm.GCMRegister();
            String url = appState.getUrl()+"android/getlatestversion";
            updateSettings();
            android.util.Log.e("String result",url);
            Ion.with(this).load(url).asString()
                    .setCallback(new FutureCallback<String>() {
                        @Override
                        public void onCompleted(Exception e, String result) {
                            if(result!=null){
                                sendToNavigation(result);
                            }else{
                                android.util.Log.e("Excep",e.toString(),e);
                                Util.customToast(MainActivity.this,"Low Network connection");
                            }

                        }
                    });


        }
    }

     /*This method is used to check current version in the device with HB server*/
    private void sendToNavigation(String result){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        List<VersonDetailsDTO> versonDetails = new ArrayList<VersonDetailsDTO>();
        try {
            android.util.Log.e("Result",result);
            versonDetails = Arrays.asList(gson.fromJson(result,VersonDetailsDTO[].class));
            if(versonDetails.get(0).getVersionCode() > BuildConfig.VERSION_CODE){
                progressBarWheel.setVisibility(View.VISIBLE);
                updateData(versonDetails.get(0).getApkURL());
                Util.customToast(this,"You have an enhanced version waiting for upgrade");
            }else{
                progressBarWheel.setVisibility(View.GONE);
                checkData();

            }
        } catch (Exception e) {
            android.util.Log.e("Json Parsing", e.toString(), e);

        }

    }
    /*This method is used to send market place where device project version is lower than the HB server version*/
    private void updateData(String apkUrl){
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }
    /*This method is used to check wheather animation played minimum of 3 seconds*/
    private void checkData() {
        if (new Date().getTime() - startTime > 3000) {
            userNavigation();
        } else {
            Thread looperThread = new Thread(new Runnable() {
                public void run() {
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            checkData();
                        }
                    }, 3000);
                }
            });
            looperThread.start();

        }
    }

   /*This method is used to get the androdi device settings from HB server*/
    private void updateSettings() {
        try {
            httpConnection = new ServiceListener(appState);
            String settingUrl = "adsettings/get";
            httpConnection.sendRequest(settingUrl, null,
                    ServiceListenerType.AD_SETTINGS, SyncHandler, "GET", null);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

        // Get source of open for app re-engagement
        mobileAppTracker.setReferralSources(this);
        // MAT will not function unless the measureSession call is included
        mobileAppTracker.measureSession();
    }


     /*
     *This method is used to check login done or not
     * By that pages will be navigated*/
    private void userNavigation() {
        android.util.Log.e("Main Activity",
                "userNavigation On Create Page Called");
        appState.setSelectedItem(0);
        if (!prefs.getBoolean("isProfile", false)) {
            android.util.Log.i("Navigation Data",
                    "Redirecting to LoginActivity");
            startActivitiesUser(new Intent(this, LoginActivity.class), this);
            finish();

        } else {
            try {

                Log.e("Profile","Profile:");
                String profileSerialized = prefs.getString("Profile", "");
                Customers profile = Customers.create(profileSerialized);
                if(profile.getAuthenticationId().length()==0){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isProfileUpdates", false);
                    editor.commit();
                }
                startActivitiesUser(new Intent(this,
                        DealsActivity.class), this);
                finish();

            }
            catch (Exception e) {

                Log.e("Profile","null pointer crash");

            }

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        android.util.Log.d("MainActivity", "On Start");
        //EasyTracker.getInstance(this).activityStart(this);
        GoogleAnalytics.getInstance(this).reportActivityStart(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        android.util.Log.d("MainActivity", "On Restart Process");
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
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
        /*mp.stop();*/
        super.onDestroy();
    }
   /*This override method is used to send to settings page*/
    @Override
    public void onUndo(Parcelable token) {
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }
     /* Concrete method from useractivity used to receive datas from HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        storeInSharedPreference(message);
    }

     /*Parsing the JSON response from HB server and store it in shared preference*/
    private void storeInSharedPreference(Bundle message) {
        try {
            String response = message.getString(ServiceListener.RESPONSEDATA);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            List<ADSettingsDTO> settings = Arrays.asList(gson.fromJson(response,
                    ADSettingsDTO[].class));
            for (ADSettingsDTO advertise : settings) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(advertise.getSettingkey(), advertise.getValue());
                editor.commit();
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isSettings", true);
            editor.commit();
        }catch (Exception e){
             Util.customToastMessage(this,getString(R.string.nonetworkconn));
        }


    }


    private class LocationReceiver extends BroadcastReceiver {
        // Prevents instantiation
        private LocationReceiver() {

        }

        // Called when the BroadcastReceiver gets an Intent it's registered to
        // receive
        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.i("LocationReceiver", "LocationReceiver called");
            if (intent != null) {
                Location location = intent
                        .getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
                if (location != null) {
                    GlobalAppState appState = (GlobalAppState) getApplication();
                    appState.updateLocation(location, getBaseContext());
                    android.util.Log.e("LocationReceiver",
                            "LocationReceiver updated location");

                } else {
                    android.util.Log.e("LocationReceiver",
                            "LocationReceiver received null location");
                }
            } else {
                android.util.Log.e("LocationReceiver",
                        "LocationReceiver received null intent");
            }
        }

    }


}