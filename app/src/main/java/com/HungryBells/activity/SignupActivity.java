package com.HungryBells.activity;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.User;
import com.HungryBells.activity.intefaceImpl.LoginUpdate;
import com.HungryBells.activity.intefaces.LoginParsing;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SignupActivity extends UserActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        appState = (GlobalAppState) getApplication();
        mUndoBarController = new UndoBarController(
                findViewById(R.id.undobar), this);
        easyTracker.send(MapBuilder.createEvent("Signup Details",
                "Signup Details Page", "Signup", null).build());
        ActionBar actionBar = getActionBar();
        actionBar.setTitle(" Sign Up");
        String htmlString = "<u><font color=\"#0000FF\">Terms of Use</font></u>";
        ((TextView) findViewById(R.id.textViewtermsuse)).setText(Html
                .fromHtml(htmlString));
        htmlString = "<u><font color=\"#0000FF\">Privacy Policy</font></u>";
        ((TextView) findViewById(R.id.textViewprivacy)).setText(Html
                .fromHtml(htmlString));
        ((Button) findViewById(R.id.buttonsinguupsubmit))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        submitAllData();
                    }
                });
        ((TextView) findViewById(R.id.textViewprivacy))
                .setOnClickListener(this);
        ((TextView) findViewById(R.id.textViewtermsuse))
                .setOnClickListener(this);

        getActionBar().hide();
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
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
    public void onBackPressed() {
        startActivity(new Intent(this,LoginActivity.class));
    }


    private void submitAllData() {
        try {

            View view=(EditText) findViewById(R.id.editTextusersname);
            InputMethodManager im = (InputMethodManager)getSystemService(
                    INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Customers cust = new Customers();
            User user = new User();
            String fName = ((EditText) findViewById(R.id.editTextusersname))
                    .getText().toString().trim();
            if (fName != null && fName.length() > 0) {
                cust.setFirstName(fName);
                user.setName(fName);
            } else {
                Util.customToast(this, getString(R.string.nameempty));
                return;
            }
            String mail = ((EditText) findViewById(R.id.editTextemail))
                    .getText().toString().trim();

            if (mail != null && mail.length() > 0) {
                if (Util.isValidEmailAddress(mail)) {
                    cust.setEmail(mail);
                    user.setUserName(mail);
                } else {
                    Util.customToast(this, getString(R.string.emailvalid));
                    return;
                }
            } else {
                Util.customToast(this, getString(R.string.emailempty));
                return;
            }
            String password = ((EditText) findViewById(R.id.editTextpassword))
                    .getText().toString().trim();
            if (password != null && password.length() > 4) {
                byte[] byteArray = Base64.encodeBase64(password.getBytes());
                String encodedString = new String(byteArray);
                user.setPasswordHash(new String(encodedString));
                user.setConfirmPassword(new String(encodedString));
            } else {
                Util.customToast(this,
                        getString(R.string.passempty));
                return;
            }
            String mobile = ((EditText) findViewById(R.id.editTextmobile))
                    .getText().toString().trim();
            if (mobile.length() > 0) {
                if (Util.isValidMobile(mobile)) {
                    cust.setMobileNumber(mobile);
                    user.setContactNumber(Long.parseLong(mobile));
                } else {
                    Util.customToast(this, getString(R.string.mobilevalid));
                    return;
                }
            } else {
                Util.customToast(this, getString(R.string.mobileempty));
                return;
            }
            user.setUserType("USER");
            SharedPreferences prefs = getSharedPreferences(
                    MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
            cust.setAuthenticationId(prefs.getString("REG_ID", ""));
            cust.setUser(user);
            Gson gson = new GsonBuilder().create();
            String customerData = gson.toJson(cust);
            String url = "customer/addcustomer";
            StringEntity custEntity = new StringEntity(customerData, HTTP.UTF_8);
            httpConnection = new ServiceListener(appState);
            progressBar.setCancelable(false);
            progressBar.show();

            httpConnection.sendRequest(url, null,
                    ServiceListenerType.CUSTOMER_ADD, SyncHandler, "POST",
                    custEntity);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    private void responseFromServer() {
           startActivitiesUser(new Intent(this, DealsActivity.class), this);
    }

    @Override
    public void onUndo(Parcelable token) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case CUSTOMER_ADD:
                progressBar.dismiss();
                LoginParsing login = new LoginUpdate();
                GlobalAppState appState = (GlobalAppState) getApplication();
                if (login.successfullLogin(message, this, mUndoBarController,
                        appState)) {
                    responseFromServer();
                }
                break;
            case NOTIFI_PREF_SIGNUP_DETAILS:
                progressBar.dismiss();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textViewtermsuse:
                Intent intent = new Intent(this, TermsConditionActivity.class);
                intent.putExtra("key", "terms");
                intent.putExtra("keyName","signup");
                startActivity(intent);
                startActivitiesUser(intent, this);
                finish();
                overridePendingTransition(0, R.anim.login_signupanimation);
                break;
            case R.id.textViewprivacy:
                Intent intentPrivacy = new Intent(this,
                        TermsConditionActivity.class);
                intentPrivacy.putExtra("key", "privacy");
                intentPrivacy.putExtra("keyName","signup");
                startActivitiesUser(intentPrivacy, this);
                finish();
                overridePendingTransition(0, R.anim.login_signupanimation);
                break;
        }

    }

}
