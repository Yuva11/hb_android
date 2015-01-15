package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.HungryBells.DTO.ADSettingsDTO;
import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.FoodTypeDto;
import com.HungryBells.DTO.NotificationPreference;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.SettingsDTO;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressLint("InflateParams")
public class UserSettingActivity extends UserActivity implements
        OnClickListener {
    SettingsDTO settingsContent;
    List<NotificationPreference> notifications;
    List<FoodTypeDto> foodType;
    Object notification;
    GlobalAppState appState;
    String notificationData;
    boolean update_data = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysettings);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                httpConnection = new ServiceListener(appState);
                prefs = getSharedPreferences("HB", MODE_PRIVATE);
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                ((RelativeLayout) findViewById(R.id.notificationprefs))
                        .setOnClickListener(this);
                ((RelativeLayout) findViewById(R.id.foodtypes))
                        .setOnClickListener(this);
                ((RelativeLayout) findViewById(R.id.cusines))
                        .setOnClickListener(this);


            } catch (Exception e) {
                Log.e("Settings Error", e.toString(), e);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if(networkChanges())
            updateSettings();
    }

    private void gotoHome() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSettings", true);
        editor.commit();
        Util.customToast(this, getString(R.string.settingsucuess));
        startActivitiesUser(new Intent(this, DealsActivity.class), this);
        finish();

    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e("HOME", "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(networkChanges())
            switch (item.getItemId()) {
                case android.R.id.home:
                    updateSettings();
                    break;
                case R.id.action_favrestaurent:
                    Intent cart = new Intent(this, FavorateRestaurentActivity.class);
                    startActivitiesUser(cart, this);
                    break;
                case R.id.action_profile:
                    Intent profile = new Intent(this, ProfileActivity.class);
                    startActivitiesUser(profile, this);
                    break;
                case R.id.action_feedback:
                    Intent feedback = new Intent(this, FeedBackActivity.class);
                    startActivitiesUser(feedback, this);
                    break;
                case R.id.action_myorders:
                    Intent myOrders = new Intent(this, MyOrdersActivity.class);
                    startActivitiesUser(myOrders, this);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!prefs.getBoolean("isSettings", false)) {
            return true;
        } else {
            getMenuInflater().inflate(R.menu.settings, menu);
            return true;
        }
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
            actionBarCreation(this);
            if (Util.checkNetworkAndLocation(this)) {
                EasyTracker.getInstance(this).activityStart(this);
                notificationPreference();
            }

        } catch (Exception e) {
            android.util.Log.e("Error", e.toString(), e);
        }
    }

    private void notificationPreference() {
        progressBar.setCancelable(false);
        progressBar.show();
        String profileSerialized = prefs.getString("Profile", "");
        Customers profile = Customers.create(profileSerialized);
        String url = "settings/get/" + profile.getId();
        httpConnection.sendRequest(url, null, ServiceListenerType.NOTIFI_PREF,
                SyncHandler, "GET", null);
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
        super.onDestroy();
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case NOTIFI_PREF_EDIT:
                progressBar.dismiss();
                gotoHome();
                break;
            case NOTIFI_PREF:
                progressBar.dismiss();
                jsonParsingNotificationPreference(message);
                break;
            case GET_FOODCATEGORY:
                progressBar.dismiss();
                drawFoodCategoryBackground(message);
                break;
            case AD_SETTINGS:
                storeInSharedPreference(message);
                break;
            case NOTIFI_PREF_SETTINGS:
                progressBar.dismiss();
                drawCusineBackground(message);
                break;
            case ERRORMSG:
                try {
                    if (Util.checkNetworkAndLocation(this)) {
                        Util.customToast(this, getString(R.string.nonetwork));
                    }
                } catch (Exception e) {
                    Util.customToast(this, getString(R.string.nonetworkconn));
                }
                break;
        }
    }

    private void drawFoodCategoryBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            update_data = true;
            bundle.putSerializable("category", response);
            bundle.putSerializable("Notific", notificationData);
            Intent settings = new Intent(this, FoodPreferenceSwitch.class);
            settings.putExtras(bundle);
            startActivity(settings);}

    }

    private void drawCusineBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            update_data = true;
            bundle.putSerializable("cusine", response);
            bundle.putSerializable("Notific", notificationData);
            Intent settings = new Intent(this, CusinePreferenceActivity.class);
            settings.putExtras(bundle);
            startActivity(settings);}

    }

    private void jsonParsingNotificationPreference(Bundle data) {
        notificationData = data.getString(ServiceListener.RESPONSEDATA);
        if(!prefs.getBoolean("isSettings",false)){
            Bundle bundle = new Bundle();
            bundle.putSerializable("Notific", notificationData);
            Intent settings = new Intent(this,
                    NotificationPreferenceSwitch.class);
            settings.putExtras(bundle);
            update_data = true;
            startActivitiesUser(settings,this);
            finish();
        }
    }

    private void storeInSharedPreference(Bundle message) {
        String response = message.getString(ServiceListener.RESPONSEDATA);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        List<ADSettingsDTO> settings = Arrays.asList(gson.fromJson(response,
                ADSettingsDTO[].class));
        for (ADSettingsDTO advertise : settings) {
            Editor editor = prefs.edit();
            editor.putString(advertise.getSettingkey(), advertise.getValue());
            editor.commit();
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSettings", true);
        editor.commit();
        progressBar.dismiss();

        if(update_data){
            Util.customToast(this, getString(R.string.settingsucuess));
            ContentsCache.getInstance().setContents(
                    new HashMap<String, ContentDTO>());
        }
        startActivity(new Intent(this, DealsActivity.class));
        finish();
    }

    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        if(networkChanges())
            switch (v.getId()) {
                case R.id.notificationprefs:
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Notific", notificationData);
                    Intent settings = new Intent(this,
                            NotificationPreferenceSwitch.class);
                    settings.putExtras(bundle);
                    update_data = true;
                    startActivity(settings);
                    break;
                case R.id.foodtypes:
                    progressBar.setCancelable(false);
                    progressBar.show();
                    String urlFoodtype = "foodtype/get";
                    httpConnection.sendRequest(urlFoodtype, null,
                            ServiceListenerType.GET_FOODCATEGORY, SyncHandler, "GET",
                            null);
                    break;
                case R.id.cusines:
                    progressBar.setCancelable(false);
                    progressBar.show();
                    String urlCusine = "cusine/get/";
                    httpConnection.sendRequest(urlCusine, null,
                            ServiceListenerType.NOTIFI_PREF_SETTINGS, SyncHandler,
                            "GET", null);
                    break;
                case R.id.buttoncheckout:
                    ContentsCache.getInstance().setContents(
                            new HashMap<String, ContentDTO>());
                    updateSettings();
                    break;
                default:
                    break;
            }

        //
    }

    private void updateSettings() {
        try {
            progressBar.show();
            httpConnection = new ServiceListener(appState);
            String settingUrl = "adsettings/get";
            httpConnection.sendRequest(settingUrl, null,
                    ServiceListenerType.AD_SETTINGS, SyncHandler, "GET", null);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }

}
