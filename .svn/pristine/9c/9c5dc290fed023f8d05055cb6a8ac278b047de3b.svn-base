package com.HungryBells.activity;

import java.io.File;
import java.io.FileOutputStream;
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

import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

public class MainActivity extends UserActivity {

    Handler handler;
    long startTime;
    AnimationRunActivty animations;
    public static int width;
    private GlobalAppState appState;
   /* MediaPlayer mp;*/
    ProgressWheel progressBarWheel;
    public MainActivity() {
        handler = new Handler();
        animations = new AnimationRunActivty(this);
        startTime = new Date().getTime();
    }

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
           /* mp = MediaPlayer.create(this, R.raw.bellsound);
            mp.setLooping(true);
            mp.start();
*/
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

        } catch (Exception e) {
            Util.customToast(this, getString(R.string.internalerror));
            android.util.Log.e("Internal error", e.toString(), e);
        }
    }

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

    private void updateData(String apkUrl){
        String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

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
    }



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

    }

    @Override
    public void onStart() {
        super.onStart();
        android.util.Log.d("MainActivity", "On Start");
        EasyTracker.getInstance(this).activityStart(this);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        android.util.Log.d("MainActivity", "On Restart Process");
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

    @Override
    public void onUndo(Parcelable token) {
        startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        finish();
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        storeInSharedPreference(message);
    }
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