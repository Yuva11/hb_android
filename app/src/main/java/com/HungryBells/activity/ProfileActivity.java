package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.intefaceImpl.ProfileUpdateStatus;
import com.HungryBells.activity.intefaces.ProfileUpdate;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.Notifications;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressLint({ "InflateParams", "HandlerLeak", "SimpleDateFormat" })
public class ProfileActivity extends UserActivity implements OnClickListener {
    Customers profile;
    private int year, month, day;
    private Calendar calendar;
    TextView bDate;
    private Notifications notify;
    String notificationData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                profile = new Customers();
                httpConnection = new ServiceListener(appState);
                prefs = getSharedPreferences("HB", MODE_PRIVATE);
                String profileSerialized = prefs.getString("Profile", "");
                profile = Customers.create(profileSerialized);
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                easyTracker.send(MapBuilder.createEvent("Profile",
                        "Profiles Process", "Customers", null).build());
                bDate = (TextView) findViewById(R.id.textViewprodob);
                ((RelativeLayout) findViewById(R.id.notificationprefs))
                        .setOnClickListener(this);
                ((RelativeLayout) findViewById(R.id.foodtypes))
                        .setOnClickListener(this);
                ((RelativeLayout) findViewById(R.id.cusines))
                        .setOnClickListener(this);
            } catch (Exception e) {
                Log.e("Error", e.toString());
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private void updateProfile() {
        ((EditText) findViewById(R.id.textViewprofirstname)).setText(profile
                .getFirstName());
        if (profile.getEmail().contains("@hbell.com")) {
            ((EditText) findViewById(R.id.textViewpromailid)).setEnabled(true);
        } else {
            ((EditText) findViewById(R.id.textViewpromailid)).setText(profile
                    .getEmail());
            ((EditText) findViewById(R.id.textViewpromailid)).setEnabled(false);
        }
        ((EditText) findViewById(R.id.textViewpromobile)).setText(profile
                .getMobileNumber());
        ((Button) findViewById(R.id.buttonsinguupsubmit))
                .setOnClickListener(this);
        ((Button) findViewById(R.id.buttonsinguupsubmit)).setText("Update");

        ((TextView) findViewById(R.id.textViewprodob)).setOnClickListener(this);

    }
    private String getDate(String deliveryDate) {
        Log.e("Date", deliveryDate);
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        try {
            Date date = format.parse(deliveryDate);
            System.out.println(date);
            DateFormat myFormater = new SimpleDateFormat(
                    "dd-MM-yyyy");
            deliveryDate = myFormater.format(date);
            Log.e("Delivery",deliveryDate);
            return deliveryDate;
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
        return "";
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 111:
                return new DatePickerDialog(this, pickerListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            showDate(year, month + 1, day);

        }
    };

    private void showDate(int year, int month, int day) {
        bDate.setText(new StringBuilder().append(day).append("/").append(month)
                .append("/").append(year));
    }

    @SuppressLint("SimpleDateFormat")
    protected void updateCustomer() {
        ProfileUpdate updateProfile = new ProfileUpdateStatus();
        updateProfile.profileUpdate(this, httpConnection, progressBar,
                SyncHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void jsonParsing(Bundle data) {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Gson gson = gsonBuilder.create();
            profile = gson.fromJson(response, Customers.class);
            GlobalAppState appState = (GlobalAppState) getApplication();
            if (profile != null) {
                appState.setProfile(profile);
            }
            Util.customToast(this, getString(R.string.profilescuess));
            Customers myData = profile;
            String serializedData = myData.serialize();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("Profile", serializedData);
            editor.putBoolean("isProfile", true);
            editor.apply();
            progressBar.dismiss();
            Intent intent = new Intent(getApplicationContext(), DealsActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            /*Intent in = new Intent(this,DealsActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            finish();*/
            /*startActivitiesUser(new Intent(this, DealsActivity.class), this);
            finish();*/
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
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
                    Intent deals = new Intent(this, DealsActivity.class);
                    startActivitiesUser(deals, this);
                    break;
                case R.id.action_favrestaurent:
                    Intent cart = new Intent(this, FavorateRestaurentActivity.class);
                    startActivitiesUser(cart, this);
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
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            calendar = Calendar.getInstance();
            actionBarCreation(this);
            if (profile.getDob() != null) {
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "dd-MM-yyyy hh:mm:ss");
                try {
                    Date date = formatter.parse(profile.getDob());
                    calendar.setTime(date);
                } catch (Exception e) {
                    Log.e("Error", "Parse", e);
                }

            }
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
            updateProfile();
            if (Util.checkNetworkAndLocation(this)) {
                EasyTracker.getInstance(this).activityStart(this);
            }
        } catch (Exception e) {
            android.util.Log.e("Error", e.toString(), e);
        }

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
    private void drawFoodCategoryBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            bundle.putSerializable("category", response);
            bundle.putSerializable("Notific", notificationData);
            Intent settings = new Intent(this, FoodPreferenceSwitch.class);
            settings.putExtras(bundle);
            startActivity(settings);}

    }
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        switch (what) {
            case GET_FOODCATEGORY:
                progressBar.dismiss();
                drawFoodCategoryBackground(message);
                break;
            case CUSTOMER_EDIT:
                jsonParsing(message);
                break;
            case NOTIFI_PREF_PROFILE_DETAILS:
                startActivity(new Intent(this, UserSettingActivity.class));
                finish();
                break;
            case NOTIFI_PREF:
                jsonParsingNotificationPreference(message);
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
    private void drawCusineBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            bundle.putSerializable("cusine", response);
            bundle.putSerializable("Notific", notificationData);
            Intent settings = new Intent(this, CusinePreferenceActivity.class);
            settings.putExtras(bundle);
            startActivity(settings);
        }
    }

    private void jsonParsingNotificationPreference(Bundle data) {
        notificationData = data.getString(ServiceListener.RESPONSEDATA);
        switch (notify){
            case NOTIFICATION:
                progressBar.dismiss();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Notific", notificationData);
                Intent settings = new Intent(this,
                        NotificationPreferenceSwitch.class);
                settings.putExtras(bundle);
                startActivitiesUser(settings,this);
                break;
            case FOODTYPE:
                String urlFoodtype = "foodtype/get";
                httpConnection.sendRequest(urlFoodtype, null,
                        ServiceListenerType.GET_FOODCATEGORY, SyncHandler, "GET",
                        null);
                break;

            case CUSINES:
                String urlCusine = "cusine/get/";
                httpConnection.sendRequest(urlCusine, null,
                        ServiceListenerType.NOTIFI_PREF_SETTINGS, SyncHandler,
                        "GET", null);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonsinguupsubmit:
                try {
                    ContentsCache.getInstance().setContents(
                            new HashMap<String, ContentDTO>());
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    updateCustomer();
                } catch (Exception e) {
                    Log.e("Error", "updateCustomer Exception", e);
                }
                break;
            case R.id.textViewprodob:
                try {
                    showDialog(111);
                } catch (Exception e) {
                    Log.e("Error", "Date Picker Exception", e);
                }
                break;
            case R.id.notificationprefs:
                notify = Notifications.NOTIFICATION;
                notificationPreference();
                break;
            case R.id.foodtypes:
                notify = Notifications.FOODTYPE;
                notificationPreference();
                break;
            case R.id.cusines:
                notify = Notifications.CUSINES;
                notificationPreference();
                break;
            default:
                break;
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
}
