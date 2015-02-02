package com.HungryBells.activity;

import java.util.Arrays;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.HungryBells.DTO.NotificationPreference;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.SettingsDTO;
import com.HungryBells.activity.adapter.NotificationPrefAdapter;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.FoodPreferenceSelection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 *This class is used to show Notification Preference
 * */
public class NotificationPreferenceSwitch extends UserActivity {

    /*notification instance*/
    Object notification;

    /*SettingsDTO instance*/
    SettingsDTO settingsContent;

    /*NotificationPreference list  instance*/
    List<NotificationPreference> notifications;

    /*Button instance*/
    Button buttonupdate;


    String notificationText;

    /*
  * Initiaizing sharedpreferences
  *
  * OnCreate Method for Notification preferences Activity page
  * This method will get the Notification parcelled from previous activity via intent
  * */
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_notifilist);
        prefs = getSharedPreferences("HB", MODE_PRIVATE);
        notification = getIntent().getExtras().getSerializable("Notific");
        ((TextView) findViewById(R.id.textViewsetts))
                .setText("Notification ");
        buttonupdate = (Button)findViewById(R.id.buttonupdate);
        GlobalAppState appState = (GlobalAppState) getApplication();
        httpConnection = new ServiceListener(appState);
        addPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
   /*This method used to convert JSON parcelled for activity to notification pojo*/
    private void addPreferences() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        notificationText = (String) notification;
        Gson gson = gsonBuilder.create();
        settingsContent = new SettingsDTO();
        settingsContent = gson.fromJson(notificationText, SettingsDTO.class);
        notifications = settingsContent
                .getCustomerNotificationPreferenceDTOList();
        showList();
    }

    /*This method update data into list*/
    private void showList(){
        ListView prefList = (ListView) findViewById(R.id.list_data);
        prefList.setAdapter(new NotificationPrefAdapter(notifications,this));

            buttonupdate.setText("Update");
            buttonupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateSettings();
                }
            });

    }
    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();

    }

    public void savePreferences(List<NotificationPreference>notificationPreference){
        notifications = notificationPreference;
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what){
            case GET_FOODCATEGORY:
                progressBar.dismiss();
                drawFoodCategoryBackground(message);
                break;
            default:

                    super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        addPreferences();
    }

    /*Navigate to food preference activity*/
    private void drawFoodCategoryBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            bundle.putSerializable("category", response);
            bundle.putSerializable("Notific", notificationText);
            Intent settings = new Intent(this, FoodPreferenceSwitch.class);
            settings.putExtras(bundle);
            startActivity(settings);}
        finish();
    }

    /*This method used to update notification preference to HB server*/
    private void updateSettings() {
        String url = "settings/updatenotification";
        try {
            progressBar.show();
            settingsContent = new SettingsDTO();
            settingsContent.setCustomerId(appState.getProfile().getId());
            settingsContent
                    .setCustomerNotificationPreferenceDTOList(notifications);
            Log.e("Message", notifications.toString());
            String notificDeals = new Gson().toJson(settingsContent);
            StringEntity se = new StringEntity(notificDeals, HTTP.UTF_8);
            httpConnection.sendRequest(url, null,
                    ServiceListenerType.NOTIFI_PREF_EDIT, SyncHandler, "PUT",
                    se);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }

    /*Instance state before orientation the data will be save*/
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String data = gson.toJson(notifications);
        savedInstanceState.putSerializable("values",data);


    }
     /*After the orientation the save datas are restored*/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String values =  (String)savedInstanceState.getSerializable("values");
        notifications = Arrays.asList(gson.fromJson(values,
                NotificationPreference[].class));
        showList();

    }
}
