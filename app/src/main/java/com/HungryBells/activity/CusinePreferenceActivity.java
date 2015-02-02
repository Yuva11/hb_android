package com.HungryBells.activity;

import java.util.Arrays;
import java.util.List;

import org.apache.http.entity.StringEntity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.HungryBells.DTO.CusineNotification;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.SettingsDTO;
import com.HungryBells.service.ServiceListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 *This class is used to show Cusines
 * */
public class CusinePreferenceActivity extends UserActivity implements
        OnClickListener {
    /*notification object*/
    Object notification;

    /*Dto for settings*/
    SettingsDTO settingsContent;

    /*string variable*/
    String category;

    /*list of elements in cusines*/
    List<CusineNotification> cusineNotification, cusines;


    Button buttonupdate;

    /*
  * Initiaizing sharedpreferences
  *
  * OnCreate Method for cusine preferences Activity page
  * This method will get the ncusines and notifications parcelled from previous activity via intent
  * */
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_cusinelist);
        prefs = getSharedPreferences("HB", MODE_PRIVATE);
        notification = getIntent().getExtras().getSerializable("Notific");//received from previous activity
        category = (String) getIntent().getExtras().getSerializable("cusine");//received from previous activity

        buttonupdate = (Button)findViewById(R.id.buttonupdate);
    }
 /*onstart method*/
    @Override
    protected void onStart() {
        super.onStart();
        createNotification();
    }

    /*This method is used to create cusine notifications*/
    private void createNotification() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String notificationText = (String) notification;
        Gson gson = gsonBuilder.create();
        settingsContent = new SettingsDTO();
        settingsContent = gson.fromJson(notificationText, SettingsDTO.class);
        cusineNotification = settingsContent.getCuisineDTOList();
        cusines = Arrays.asList(gson.fromJson(category,
                CusineNotification[].class));
        ViewGroup flowContainer = (ViewGroup) findViewById(R.id.flow_container);
        flowContainer.removeAllViews();
        int position = 0;
        for (CusineNotification cusine : cusines) {
            String cusineName = cusine.getName();
            if (!cusineName.isEmpty()) {
                /*items add to container in a viewgroup*/
                flowContainer
                        .addView(
                                createDummyTextView(position, cusineName,
                                        flowContainer), new LayoutParams(
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT));
            }
            position++;
        }
        for (int i = 0; i < cusines.size(); i++) {
            Log.e("Size", "Back:" + cusineBackGround(cusines.get(i).getName()));
            if (cusineBackGround(cusines.get(i).getName())) {
                View view = flowContainer.getChildAt(i);
                ViewGroup parent = (ViewGroup) view.getParent();
                int index = parent.indexOfChild(view);
                parent.removeView(view);
                view = getLayoutInflater().inflate(R.layout.adapt_cusineselect,
                        parent, false);
                TextView textLayout = (TextView) view
                        .findViewById(R.id.textViewlays);
                textLayout.setText(cusines.get(i).getName());
                view.setId(i);
                view.setOnClickListener(this);
                parent.addView(view, index);
            }
        }

            buttonupdate.setText("Update");
            buttonupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateSettings();
                }
            });

    }
    /*This method is used to create textview inside the viewgroup*/
    private View createDummyTextView(int position, String text,
                                     ViewGroup flowContainer) {
        View hiddenInfo = getLayoutInflater().inflate(R.layout.adapt_cusines,
                flowContainer, false);
        TextView textLayout = (TextView) hiddenInfo
                .findViewById(R.id.textViewlays);
        textLayout.setText(text);
        hiddenInfo.setId(position);
        hiddenInfo.setOnClickListener(this);
        return hiddenInfo;
    }

    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();


    }
  /*concreate method for user activity*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
               super.onBackPressed();

    }
    /*This method is used to send updated request to HB server*/
    private void updateSettings() {
        String url = "settings/updatecuisine";
        try {
            progressBar.show();
            GlobalAppState appState = (GlobalAppState) getApplication();
            httpConnection = new ServiceListener(appState);
            settingsContent = new SettingsDTO();
            settingsContent.setCustomerId(appState.getProfile().getId());
            settingsContent.setCuisineDTOList(cusineNotification);
            String notificDeals = new Gson().toJson(settingsContent);
            StringEntity se = new StringEntity(notificDeals);
            httpConnection.sendRequest(url, null,
                    ServiceListenerType.NOTIFI_PREF_EDIT, SyncHandler, "PUT",
                    se);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }
 /*onclick action for layout of cusine*/
    @Override
    public void onClick(View v) {
        // v.get
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = parent.indexOfChild(v);
        parent.removeView(v);
        int id = v.getId();
       /*check if cusine is already selected*/
        if (cusineBackGround(cusines.get(v.getId()).getName())) {
            removeCusine(cusines.get(v.getId()).getName());
            v = getLayoutInflater().inflate(R.layout.adapt_cusines, parent,
                    false);
            TextView textLayout = (TextView) v.findViewById(R.id.textViewlays);
            textLayout.setText(cusines.get(id).getName());

        } else {
            cusineNotification.add(cusines.get(v.getId()));
            v = getLayoutInflater().inflate(R.layout.adapt_cusineselect,
                    parent, false);
            TextView textLayout = (TextView) v.findViewById(R.id.textViewlays);
            textLayout.setText(cusines.get(id).getName());
        }
        v.setId(id);
        v.setOnClickListener(this);
        parent.addView(v, index);



    }

    /*remove cusine from selection*/
    private boolean removeCusine(String name){
        for (int i = 0; i < cusineNotification.size(); i++) {
            if (cusineNotification.get(i).getName().equals(name)) {
                cusineNotification.remove(i);
                return true;
            }
        }
        return false;
    }

    /*returns true if cusine is already selected else false*/
    private boolean cusineBackGround(String name) {
        for (int i = 0; i < cusineNotification.size(); i++) {

            if (cusineNotification.get(i).getName().equals(name)) {
                Log.e("Cusine",cusineNotification.get(i).getName());
                return true;
            }
        }
        return false;

    }
}
