package com.HungryBells.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.UserLocation;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.subactivity.LoadChangeDealsActivity;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.JsonParsing;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;

import java.util.Date;

public abstract class UserActivity extends FragmentActivity implements
        UndoBarController.UndoListener {

    public abstract void processMessage(Bundle message, ServiceListenerType what);

    EasyTracker easyTracker = null;
    CustomProgressDialog progressBar;
    public ServiceListener httpConnection;
    ActionBar actionBar;

    public UndoBarController getmUndoBarController() {
        return mUndoBarController;
    }

    public void setmUndoBarController(UndoBarController mUndoBarController) {
        this.mUndoBarController = mUndoBarController;
    }

    public EasyTracker getEasyTracker() {
        return easyTracker;
    }

    public void setEasyTracker(EasyTracker easyTracker) {
        this.easyTracker = easyTracker;
    }

    protected UndoBarController mUndoBarController;
    SharedPreferences prefs;
    protected GlobalAppState appState;

    public UserActivity() {
        httpConnection = new ServiceListener(appState);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle arg0) {
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(arg0);
        appState = (GlobalAppState) getApplication();
        easyTracker = EasyTracker.getInstance(this);
        progressBar = new CustomProgressDialog(this);
        android.util.Log.d("Inside UserActivity",
                "Inside on create UserActivity");

    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        android.util.Log.i("onStart", "On Start Called");
    }

    @SuppressLint("HandlerLeak")
    protected Handler SyncHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ServiceListenerType type = (ServiceListenerType) msg.obj;
            switch (type) {
                case NOTIFI_PREF:
                    if (progressBar != null&&progressBar.isShowing())
                    progressBar.dismiss();
                    processMessage(msg.getData(), ServiceListenerType.NOTIFI_PREF);
                    // jsonParsingNotificationPreference(msg.getData());
                    break;
                case LOC_FIND:
                    processMessage(msg.getData(), ServiceListenerType.LOC_FIND);
                    break;
                case SUBMITDATA:
                    processMessage(msg.getData(), ServiceListenerType.SUBMITDATA);
                    break;
                case GET_ALLDEALS:
                    processMessage(msg.getData(), ServiceListenerType.GET_ALLDEALS);
                    break;
                case GET_LOCATION:
                    JsonParsing.jsonParsingCities(msg.getData(), appState);
                    break;
                case FAV_RESTARUNT:
                    processMessage(msg.getData(), ServiceListenerType.FAV_RESTARUNT);
                    break;
                case FEEDBACK_ADS:
                    processMessage(msg.getData(), ServiceListenerType.FEEDBACK_ADS);
                    break;
              /*  case VERSION_HISTORYS:
                    android.util.Log.e("Version","Versions");
                    processMessage(msg.getData(), ServiceListenerType.VERSION_HISTORYS);
                    break;*/

                case TERMS_CONDITIONS:
                    processMessage(msg.getData(),
                            ServiceListenerType.TERMS_CONDITIONS);
                    break;

                case FEEDBACK_INSERT:
                    processMessage(msg.getData(),
                            ServiceListenerType.FEEDBACK_INSERT);
                    break;
                case CUSTOMER_LOGIN:
                    processMessage(msg.getData(),
                            ServiceListenerType.CUSTOMER_LOGIN);
                    break;
                case LIKE_ADS:
                    processMessage(msg.getData(), ServiceListenerType.LIKE_ADS);
                    break;
                case FAV_REST:
                    processMessage(msg.getData(), ServiceListenerType.FAV_REST);
                    break;
                case AD_SETTINGS:
                    processMessage(msg.getData(), ServiceListenerType.AD_SETTINGS);
                    break;
                case NOTIFI_PREF_DETAILS:
                    processMessage(msg.getData(),
                            ServiceListenerType.NOTIFI_PREF_DETAILS);
                    break;
                case CUSTOMER_EDIT:
                    processMessage(msg.getData(), ServiceListenerType.CUSTOMER_EDIT);
                    break;
                case NOTIFI_PREF_PROFILE_DETAILS:
                    processMessage(msg.getData(),
                            ServiceListenerType.NOTIFI_PREF_PROFILE_DETAILS);
                    break;
                case CUSTOMER_ADD:
                    processMessage(msg.getData(), ServiceListenerType.CUSTOMER_ADD);
                    break;
                case LIKE_COUPONS:
                    processMessage(msg.getData(), ServiceListenerType.LIKE_COUPONS);
                    break;

                case NOTIFI_PREF_SIGNUP_DETAILS:
                    processMessage(msg.getData(),
                            ServiceListenerType.NOTIFI_PREF_SIGNUP_DETAILS);
                    break;
                case NOTIFI_PREF_EDIT:
                    processMessage(msg.getData(),
                            ServiceListenerType.NOTIFI_PREF_EDIT);
                    progressBar.dismiss();
                    break;
                case MY_ORDERS:
                    processMessage(msg.getData(), ServiceListenerType.MY_ORDERS);
                    break;
                case NOTIFI_PREF_SETTINGS:
                    processMessage(msg.getData(),
                            ServiceListenerType.NOTIFI_PREF_SETTINGS);
                    break;
                case GET_FOODCATEGORY:
                    processMessage(msg.getData(),
                            ServiceListenerType.GET_FOODCATEGORY);
                    break;
                default:
                    System.out.println("Inside here");
                    if (progressBar != null)
                        progressBar.dismiss();
                    processMessage(msg.getData(), ServiceListenerType.ERRORMSG);
                    break;
            }
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @SuppressLint("InflateParams")
    public void actionBarCreation(final Activity context) {
        try {
            actionBar = getActionBar();
            actionBar.setIcon(R.drawable.applogo);
            actionBar.setTitle("");
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.actionbar_custom,
                    null);
            android.util.Log.i("Action bar ", "Action bar created:"
                    + new Date());
            actionBar.setCustomView(mCustomView);

            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            if(!context.getLocalClassName().contains("DealsActivity")||context.getLocalClassName().contains("ViewDealsActivity")){
                actionBar.setDisplayHomeAsUpEnabled(true);
            }else
                actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            String location = appState.getCity();
            android.util.Log.i("Inside Search Locations", "Location data");
            if (appState.isSearchByLocation() == true)
                location = getLocationString(appState.getActionBarLocation());
            ((TextView) findViewById(R.id.textViewlocation)).setText(location);
            ImageView img = (ImageView) findViewById(R.id.imageViewicaction);
            if (!appState.getCity().equals("Hungry Bells"))
                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(UserActivity.this,SearchCityActivity.class));
                        overridePendingTransition(0,0);
                    }
                });

        } catch (Exception e) {
            android.util.Log.e("Error", e.toString());
        }

    }

    public String getLocationString(String location) {
        try {
            if (location.length() > 18) {
                location = location.substring(0, 15) + "..";
            }
        } catch (Exception e) {
            location = "";
        }
        return location;
    }

    public void searchByLocation(UserLocationDTO userLocation, Activity context) {
        try {
            UserLocation dealLoc = new UserLocation();
            dealLoc.setCustomerId(appState.getProfile().getId());
            if (userLocation.getName().contains("My Location")) {
                appState.setSearchByLocation(false);
                appState.setActionBarLocation(appState.getCity());
                dealLoc.setLatitude(appState.getLatitude());
                dealLoc.setLongitude(appState.getLongitude());
                appState.setDealChangeLocation(dealLoc);
                android.util.Log.i("Address", appState.getCity());
            } else {
                appState.setActionBarLocation(userLocation.getName());
                dealLoc.setLatitude(userLocation.getLatitude());
                dealLoc.setLongitude(userLocation.getLongitude());
                appState.setDealChangeLocation(dealLoc);
                appState.setSearchByLocation(true);
            }
            android.util.Log.i("UserLoc","User:"+userLocation);
            if (ContentsCache.getInstance().getCacheContents(
                    userLocation.getName()) == false) {
                LoadChangeDealsActivity loadDeals = new LoadChangeDealsActivity(
                        context, progressBar, appState);
                loadDeals.loadDeals();
            } else {
                LoadChangeDealsActivity loadDeals = new LoadChangeDealsActivity(
                        context, appState);
                loadDeals.dealsPageChange();
            }
        } catch (Exception e) {
            Log.e(e);
        }

    }
    protected boolean networkChanges(){
        if(Util.isNetworkConnectionAvailablity(this)){
            return true;
        }else{
            Util.customToast(this,"No network connection");
            return false;
        }
    }
    public void startActivitiesUser(Intent userIntent, Activity context) {
        try {
            if (Util.checkNetworkAndLocation(context)) {
                startActivity(userIntent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
            }
        } catch (Exception e) {
            android.util.Log.e("Error", e.toString(), e);

            Util.customToast(this, getString(R.string.nonetworkconn));

        }

    }
}
