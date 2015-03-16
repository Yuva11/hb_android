package com.HungryBells.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.TermsConditions;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ritesh on 16/03/15.
 */
public class TermsPrivacyActivity extends UserActivity {

    /*List of terms elements*/
    List<TermsConditions> terms;
    String data;
    String navigate;

    /*Initializing tappstate
    get data parcelled from previous activity*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsconditions);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null && appState.getUrl() != null) {
            easyTracker.send(MapBuilder.createEvent("Terms",
                    "Terms And Condition", "Terms_Conditions", null).build());
            data = getIntent().getExtras().getString("key");
            navigate = getIntent().getExtras().getString("keyName");
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    /*This methos is used to set the given text to webview*/
    private void setText() {
        WebView wv = (WebView) findViewById(R.id.textViewcondition);
        String mimeType = "text/html";
        String encoding = "UTF-8";
        String html = "";
        if (terms.size() > 1) {
            html = terms.get(0).getValue();
            String heading = "Privacy Policy";
            if (data.equals("terms")) {
                html = terms.get(1).getValue();
                heading = "Terms & Conditions";
            }
            ((TextView) findViewById(R.id.textOnlyContent)).setText(heading);
        }
        wv.loadDataWithBaseURL("", html, mimeType, encoding, "");
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            if (Util.checkNetworkAndLocation(this)) {
                EasyTracker.getInstance(this).activityStart(this);
                getTermsAndCondition();
            }
        } catch (Exception e) {
            android.util.Log.e("Error", e.toString(), e);
        }
    }
    /*This method is used to receive the terms from HB server*/
    private void getTermsAndCondition() {
        progressBar.setCancelable(false);
        progressBar.show();
        httpConnection = new ServiceListener(appState);
        String url = "terms/get";
        httpConnection.sendRequest(url, null,
                ServiceListenerType.TERMS_CONDITIONS, SyncHandler, "GET", null);

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /*This method is used to navigate to previous page*/
    @Override
    public void onBackPressed() {

        startActivitiesUser(new Intent(this, DealsActivity.class), this);
        finish();

        /*
        if (navigate.equalsIgnoreCase("signup"))
            startActivitiesUser(new Intent(this, SignupActivity.class), this);
        else if (navigate.equalsIgnoreCase("login"))
            startActivitiesUser(new Intent(this, LoginActivity.class), this);
        else{
            startActivitiesUser(new Intent(this, LoggingInActivity.class), this);
        }
        finish();
        */
    }
    /* Concrete method from useractivity used to receive datas to HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        jsonParsing(message);
    }


    /*Parsing the json data from HB server*/
    private void jsonParsing(Bundle message) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        String response = message.getString(ServiceListener.RESPONSEDATA);
        response = "{\"Terms\":" + response + "}";
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
        terms = new ArrayList<TermsConditions>();
        Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
        try {
            terms = datas.Terms;
        } catch (Exception e) {
            Log.e("Json Parsing", e.toString(), e);
        }
        progressBar.dismiss();
        Log.d("Terms", terms.toString());
        setText();
    }

    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }
}
