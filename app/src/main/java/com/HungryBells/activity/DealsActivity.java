package com.HungryBells.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.User;
import com.HungryBells.DTO.UserLocation;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.adapter.CircularViewPagerHandler;
import com.HungryBells.activity.service.GooglePlayServices;
import com.HungryBells.activity.subactivity.LoadChangeDealsActivity;
import com.HungryBells.dialog.SettingsDialog;
import com.HungryBells.fragments.CouponsFragment;
import com.HungryBells.fragments.DealsListFragment;
import com.HungryBells.fragments.WhatsNewFragment;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ChromeHelpPopup;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.JsonParsing;
import com.HungryBells.util.PagerSlidingTabStrip;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Log;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressLint("InflateParams")
public class DealsActivity extends UserActivity implements
        ActionBar.TabListener, OnPageChangeListener {


    PagerSlidingTabStrip tabs;
    private ViewPager viewPager;
    Handler handler;
    private boolean isBackPressed = false;
    TimerTask timerTask;
    Timer locationTimer;
    Timer timer;
    MyTimerTask myTimerTask;
    protected GlobalAppState appState;
    int currentPosition= 0;
    private long time;
    private  boolean showProgress = false;
    ChromeHelpPopup chromeHelpPopup;
    TextView center;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealslayout);
        appState = (GlobalAppState) getApplication();
        prefs = getSharedPreferences("HB",MODE_PRIVATE);
        android.util.Log.d("Deals Activity", "Deals Activity onCreate");
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                httpConnection = new ServiceListener(appState);
                actionBarCreation(this);
                android.util.Log.e("Deals Activity", "On Create Page Called");
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                forceShowActionBarOverflowMenu();
                handler = new Handler();
                viewPager = (ViewPager) findViewById(R.id.pager);
                getSearchLocations();
                GooglePlayServices gs = new GooglePlayServices(this);
                gs.start();
                center = (TextView)findViewById(R.id.textViewcenter);
                chromeHelpPopup = new ChromeHelpPopup(DealsActivity.this,getString(R.string.loadstring));
                IntentFilter mIntentFilter = new IntentFilter("LOCATIONUPDATE");
                LocationReceiver mLocationReceiver = new LocationReceiver();
                LocalBroadcastManager.getInstance(this).registerReceiver(
                        mLocationReceiver, mIntentFilter);
                if(!prefs.getBoolean("isProfileUpdates",false)){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("isProfile", true);
                    startActivity(new Intent(this, AccountManagerActivity.class));
                    editor.commit();
                    updateProfile();
                }
            } catch (Exception e) {
                android.util.Log.e("Main", e.toString(), e);
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void loadDeals() throws Exception {
        android.util.Log.e("Inside Main", "Inside Main Activity");
        UserLocation dealLoc;
        if (appState.isSearchByLocation()) {
            dealLoc = appState.getDealChangeLocation();
        } else {
            dealLoc = new UserLocation();
            dealLoc.setLatitude(appState.getLatitude());
            dealLoc.setLongitude(appState.getLongitude());
            appState.setActionBarLocation(appState.getCity());
        }
        String location = appState.getCity();
        android.util.Log.e("Inside Search Locations", "Location data");
        if (appState.isSearchByLocation())
            location = getLocationString(appState.getActionBarLocation());
        ((TextView) findViewById(R.id.textViewlocation)).setText(location);

        if (appState.getProfile() == null) {
            String profile = prefs.getString("Profile", "");
            Customers customerProfile = Customers.create(profile);
            appState.setProfile(customerProfile);
            android.util.Log.e("Getting Profile",
                    "Getting from Shanred Preference");
        }
        dealLoc.setCustomerId(appState.getProfile().getId());
        android.util.Log.i("Location", dealLoc.toString());
        String notificDeals = new Gson().toJson(dealLoc);
        StringEntity se = new StringEntity(notificDeals, HTTP.UTF_8);
        progressBar.setCancelable(false);
        progressBar.show();
        showProgress = true;
        timer = new Timer();
        myTimerTask = new MyTimerTask();
            //singleshot delay 1000 ms
            timer.schedule(myTimerTask, 4000);
        String url = "searchdeals/" + appState.getProfile().getId();
        httpConnection.sendRequest(url, null, ServiceListenerType.GET_ALLDEALS,
                SyncHandler, "POST", se);
        if (appState.getMerchatBranch() == null) {
            String favRestaurent = "customer/restaurants/"
                    + appState.getProfile().getId();
            httpConnection
                    .sendRequest(favRestaurent, null,
                            ServiceListenerType.FAV_RESTARUNT, SyncHandler,
                            "GET", null);
        }

    }

    private void getSearchLocations() {
        String url = "location/getnearestlocation/" + appState.getLatitude()
                + "/" + appState.getLongitude() + ".";
        httpConnection.sendRequest(url, null, ServiceListenerType.GET_LOCATION,
                SyncHandler, "GET", null);
    }
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable(){
                @Override
                public void run() {
                   if(!showProgress){
                       /*chromeHelpPopup.show(center);*/
                       center.setVisibility(View.VISIBLE);
                       android.util.Log.e("Inside","Insides");
                   }
                }});
        }

    }
    private void forceShowActionBarOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            Log.e("Error:" + e);
        }
    }

    public void setPages() {
        try {
            android.util.Log.d("Inside set Pages", "Set pages called");
            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            MyPagerAdapter mAdapter = new MyPagerAdapter(
                    getSupportFragmentManager());
            viewPager.setAdapter(mAdapter);

           /* viewPager.setOnPageChangeListener(new CircularViewPagerHandler(
                    viewPager, this));*/
           /* viewPager.setPageTransformer(true, new DepthPageTransformer());*/
           /* viewPager.setCurrentItem(currentPage);*/
            final int pageMargin = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                            .getDisplayMetrics());
            viewPager.setPageMargin(pageMargin);
            Typeface type = Typeface.createFromAsset(getAssets(),
                    "Pristina.ttf");

            tabs.setTypeface(type, Typeface.NORMAL);
            tabs.setViewPager(viewPager);
            getSupportFragmentManager().executePendingTransactions();
            mAdapter.notifyDataSetChanged();
            android.util.Log.i("sdfsdfsdf","dsfsdfdsf:"+appState.getSelectedItem());

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(appState.getSelectedItem());; //Where "2" is the position you want to go
                }
            });
            tabs.setSelectedText(appState.getSelectedItem());
            viewPager.setOnPageChangeListener(new CircularViewPagerHandler(viewPager,this));

        } catch (Exception e) {
            android.util.Log.e("Error in SetPages", e.toString(), e);
        }
    }

    private void allDealContents(Bundle data) {
        try {
            showProgress = false;
            progressBar.dismiss();
            center.setVisibility(View.GONE);
            timer.cancel();
          /*  chromeHelpPopup.dismiss();
           */
            JsonParsing.allDealContents(data, appState);
            LoadChangeDealsActivity loadDeals = new LoadChangeDealsActivity(
                    this, appState);
            loadDeals.dealsPageChange();
        } catch (Exception e) {
            Log.e("Error" + e);
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private final String[] titles = { "What's New ", " Best Pick ",
                "  Coupons " };

        public MyPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int currentPage) {

            if (currentPage == 0) {
                return new WhatsNewFragment();
            } else if (currentPage == 1)
                return new DealsListFragment();
            else
                return new CouponsFragment();
        }
        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }
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
                    android.util.Log.e("HOME", "onMenuOpened", e);
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
                    appState.setSelectedItem(0);
                    if (Util.checkNetworkAndLocation(this)) {
                        UserLocationDTO location = new UserLocationDTO();
                        location.setName("My Location");
                        searchByLocation(location, this);
                    }
                    break;

                case R.id.action_favrestaurent:
                    appState.setSelectedItem(0);
                    Intent cart = new Intent(this, FavorateRestaurentActivity.class);
                    startActivitiesUser(cart, this);
                    break;
                case R.id.action_profile:
                    appState.setSelectedItem(0);
                    Intent profile = new Intent(this, ProfileActivity.class);
                    startActivitiesUser(profile, this);
                    break;
                case R.id.action_feedback:
                    appState.setSelectedItem(0);
                    Intent feedback = new Intent(this, FeedBackActivity.class);
                    startActivitiesUser(feedback, this);
                    break;
                case R.id.action_myorders:
                    appState.setSelectedItem(0);
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
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        isBackPressed = true;
        SettingsDialog exit = new SettingsDialog(this);
        exit.showExitAlert();
    }

    public void onBackPressed(boolean value) {
        if(value){
            finish();
        }else{
            isBackPressed = false;
        }
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        tabs.setSelectedText(tab.getPosition());
        viewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int arg0) {

    }
    public void pageChanged(int currentPosition){
        tabs.setSelectedText(currentPosition);
    }
    public void dealsAdsViewPage(int custId) {
        Intent viewadvertisement = new Intent(this, ViewAdsActivity.class);
        Bundle bundle = new Bundle();
        appState.setSelectedItem(0);
        bundle.putSerializable("ADV URL", custId);
        viewadvertisement.putExtras(bundle);
        startActivitiesUser(viewadvertisement, this);
    }

    public void dealsCouponViewPage(int custId) {
        Intent viewCoupons = new Intent(this, ViewCouponActivity.class);
        Bundle bundle = new Bundle();
        appState.setSelectedItem(0);
        bundle.putSerializable("Coupon", custId);
        viewCoupons.putExtras(bundle);
        startActivitiesUser(viewCoupons, this);
    }

    public void dealsViewPage(int custId) {
        Intent viewDeals = new Intent(this, ViewDealsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Deals", custId);
        viewDeals.putExtras(bundle);
        appState.setSelectedItem(0);
        startActivitiesUser(viewDeals, this);
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case GET_ALLDEALS:
                allDealContents(message);
                break;
            case GET_LOCATION:
                JsonParsing.jsonParsingCities(message, appState);
                break;
            case FAV_RESTARUNT:
                JsonParsing.favorateRestaurent(message, appState);
                break;
            case CUSTOMER_EDIT:
                profileUpdate();
                break;
            case ERRORMSG:
                try {
                    center.setVisibility(View.GONE);
                    timer.cancel();
                    if (Util.checkNetworkAndLocation(this)) {
                        Util.customToast(this, getString(R.string.nonetwork));
                    }
                } catch (Exception e) {
                    Util.customToast(this, getString(R.string.nonetworkconn));
                }
                break;

        }


    }
    private void profileUpdate(){
        android.util.Log.e("Updated","updates done");
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isProfileUpdates", true);
        editor.putBoolean("isProfile", true);
        editor.commit();
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        String lastLocation = ((TextView) findViewById(R.id.textViewlocation))
                                .getText().toString();
                        String currentLocation = appState.getCity();
                        android.util.Log.i("Timer Location", lastLocation
                                + " ,currentLocation:" + currentLocation);
                        if (lastLocation.equals(currentLocation)) {
                            android.util.Log.e("Location Timer",
                                    "Location not changed");
                            return;
                        } else {
                            try {
                                if (!appState.isSearchByLocation()) {
                                    android.util.Log.e("Location Timer",
                                            "Search by Location is False");
                                    appState.setActionBarLocation(appState
                                            .getCity());
                                    loadDeals();
                                } else {
                                    android.util.Log
                                            .e("Location Timer",
                                                    "Search by Location is True.So Deals not loades");
                                }
                            } catch (Exception e) {
                                android.util.Log.e("Error", e.toString(), e);
                            }
                        }
                        android.util.Log.i("Timer", "Timer Expired");
                    }
                });
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        android.util.Log.d("Deals Activity", "Deals Activity onStart");
        if (appState.getCity() != null) {
            try {

                android.util.Log.i("On Start", "On start Process called");

                if (Util.checkNetworkAndLocation(this)) {

                    String location = appState.getCity();
                    locationTimer = new Timer();
                    initializeTimerTask();
                    locationTimer.schedule(timerTask, 60 * 1000, 60 * 1000);
                    if (appState.isSearchByLocation()) {
                        location = appState.getCity();
                    }
                    getSearchLocations();
                    if (ContentsCache.getInstance().getCacheContents(location)) {
                        android.util.Log
                                .d("on Start", "Viewing Cached Content");
                        LoadChangeDealsActivity loadDeals = new LoadChangeDealsActivity(
                                this, appState);
                        loadDeals.dealsPageChange();
                    } else {
                        android.util.Log.d("on Start", "Loading Deals");
                        loadDeals();
                    }
                }
                EasyTracker.getInstance(this).activityStart(this);
            } catch (Exception e) {
                android.util.Log.e("Error", e.toString(), e);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timerTask != null)
            timerTask.cancel();
        android.util.Log.i("On Stop", "Cancelling Timer");
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.e("On Destroy", "Destroy Called");
    }

    @Override
    public void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onUndo(Parcelable token) {
        onStart();
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
                    android.util.Log.i("LocationReceiver",
                            "LocationReceiver updated location");

                } else {
                    android.util.Log.i("LocationReceiver",
                            "LocationReceiver received null location");
                }
            } else {
                android.util.Log.i("LocationReceiver",
                        "LocationReceiver received null intent");
            }
        }

    }
    private void updateProfile() {
        SharedPreferences preferencesReader = getSharedPreferences(
                "HB", Context.MODE_PRIVATE);
        String profileSerialized = preferencesReader.getString("Profile", "");
        Customers profile = Customers.create(profileSerialized);
        User user = new User();
        android.util.Log.e("Profile","update profile");
        user.setName(profile.getFirstName());
        user.setUserName(  profile.getEmail());
        try {
            profile.setUser(user);
            profile.setAuthenticationId(getRegistrationId(this));
            String url = "customer/updatecustomer/" + profile.getId();
            StringEntity custEntity = null;
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String customerData = gson.toJson(profile);
            custEntity = new StringEntity(customerData, HTTP.UTF_8);
            if(profile.getAuthenticationId()!=null&&profile.getAuthenticationId().length()>2)
                httpConnection.sendRequest(url, null,
                        ServiceListenerType.CUSTOMER_EDIT, SyncHandler, "PUT",
                        custEntity);
        } catch (Exception e) {
            android.util.Log.e("Error", "Excep", e);
        }

    } private SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString("registration_id", "");
        if (registrationId.isEmpty()) {
            android.util.Log.i("GCMRegistrationActivity", "Registration not found.");
            return "";
        }
        return registrationId;
    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("values",isBackPressed);
        android.util.Log.e("Saved State", "is Boolean:"+isBackPressed);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isBackPressed = savedInstanceState.getBoolean("values");
        if(isBackPressed)
            onBackPressed();
        android.util.Log.e("Restore State", "is Boolean");

    }
}
