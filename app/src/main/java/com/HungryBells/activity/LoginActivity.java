package com.HungryBells.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.LoginRequestDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.intefaceImpl.LoginUpdate;
import com.HungryBells.activity.intefaces.LoginParsing;
import com.HungryBells.activity.subactivity.AnimationRunActivty;
import com.HungryBells.dialog.ForgotPasswordDialog;
import com.HungryBells.service.LoginService;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

import static com.HungryBells.util.ServiceType.FACEBOOK;
import static com.HungryBells.util.ServiceType.GOOGLEPLUS;
import static com.HungryBells.util.ServiceType.LINKEDIN;
import static com.HungryBells.util.ServiceType.TWITTER;

public class LoginActivity extends UserActivity implements OnClickListener {

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        easyTracker.send(MapBuilder.createEvent("Login", "Login Process",
                "Login", null).build());
        mUndoBarController = new UndoBarController(findViewById(R.id.undobar),
                this);
        prefs = getSharedPreferences("HB", MODE_PRIVATE);
        ((RelativeLayout) findViewById(R.id.facebookbutton))
                .setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.twitterbutton))
                .setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.linkedinbutton))
                .setOnClickListener(this);
        ((RelativeLayout) findViewById(R.id.googlebutton))
                .setOnClickListener(this);
        String htmlString = "<u><font color=\"#0000FF\">Terms of Use</font></u>";
        ((TextView) findViewById(R.id.textViewtermsuse)).setText(Html
                .fromHtml(htmlString));
        htmlString = "<u><font color=\"#0000FF\">Privacy Policy</font></u>";
        ((TextView) findViewById(R.id.textViewprivacy)).setText(Html
                .fromHtml(htmlString));
        ((Button) findViewById(R.id.buttonSignUp)).setOnClickListener(this);
        ((Button) findViewById(R.id.buttonSignIn)).setOnClickListener(this);
        ((TextView) findViewById(R.id.textViewprivacy))
                .setOnClickListener(this);
        ((TextView) findViewById(R.id.textViewtermsuse))
                .setOnClickListener(this);
        ((TextView) findViewById(R.id.textViewForgetPassword)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ForgotPasswordDialog(LoginActivity.this).show();
            }
        });
        if (!Util.checkNetworkAndLocation(this)) {
            mUndoBarController.showUndoBar(true,
                    getString(R.string.undobar_sample_message), null);

        }


        getActionBar().hide();

        // homePageNavigation();
    }

    @Override
    public void onClick(View v) {
        if (!Util.checkNetworkAndLocation(this)) {
            mUndoBarController.showUndoBar(true,
                    getString(R.string.undobar_sample_message), null);
        } else {
            //AnimationRunActivty animations = new AnimationRunActivty(this);
            //animations.runAnimation(R.id.imageViewlogos);
            enableDisableClick(false);
            switch (v.getId()) {
                case R.id.facebookbutton:
                    LoginService.login(this, FACEBOOK);
                    break;
                case R.id.twitterbutton:
                    LoginService.login(this, TWITTER);
                    break;
                case R.id.linkedinbutton:
                    LoginService.login(this, LINKEDIN);
                    break;
                case R.id.googlebutton:
                    LoginService.login(this, GOOGLEPLUS);
                    break;
                case R.id.buttonSignUp:
                    singnUpTransition();
                    break;
                case R.id.buttonSignIn:
                    loggingIn();
                    break;
                case R.id.textViewtermsuse:
                    InputMethodManager imm = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Intent intent = new Intent(this, TermsConditionActivity.class);
                    intent.putExtra("key", "terms");
                    intent.putExtra("keyName", "login");
                    startActivitiesUser(intent, this);
                    finish();
                    overridePendingTransition(0, R.anim.login_signupanimation);
                    break;
                case R.id.textViewprivacy:
                    InputMethodManager im = (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    Intent intentPrivacy = new Intent(this,
                            TermsConditionActivity.class);
                    intentPrivacy.putExtra("key", "privacy");
                    intentPrivacy.putExtra("keyName", "login");
                    startActivitiesUser(intentPrivacy, this);
                    finish();
                    overridePendingTransition(0, R.anim.login_signupanimation);
                    break;
            }

        }

    }

    private void singnUpTransition() {
        startActivitiesUser(new Intent(this, SignupActivity.class), this);
        finish();
        overridePendingTransition(0, R.anim.login_signupanimation);

    }

    private void singnInTransition() {
        startActivitiesUser(new Intent(this, LoginActivity.class), this);
        finish();
        overridePendingTransition(0, R.anim.login_signupanimation);

    }

    public  void submitMailId(String emailId){
        httpConnection = new ServiceListener(appState);
        String url = "mailer/forgotpassword/"
                + emailId+"/customer";
        httpConnection.sendRequest(url, null,
                ServiceListenerType.FEEDBACK_INSERT, SyncHandler, "GET",
                null);


    }

    private void loggingIn() {
        String mail = ((EditText) findViewById(R.id.editTextusersname))
                .getText().toString().trim();
        LoginRequestDto loginRequest = new LoginRequestDto();
        if (mail != null && mail.length() > 0) {
            if (Util.isValidEmailAddress(mail)) {
                byte[] byteArray = Base64.encodeBase64(mail.getBytes());
                String encodedString = new String(byteArray);
                loginRequest.setUserName(encodedString);
            } else {
                enableDisableClick(true);
                Util.customToastShort(this, "Username is not valid");
                return;
            }
        } else {
            enableDisableClick(true);
            Util.customToastShort(this,"Username should not be Empty");
            return;
        }
        view=(EditText) findViewById(R.id.editTextusersname);
        String password = ((EditText) findViewById(R.id.editTextemail))
                .getText().toString().trim();

        if (password != null && password.length() > 4) {
            byte[] byteArray = Base64.encodeBase64(password.getBytes());
            String encodedString = new String(byteArray);
            loginRequest.setPassWord(new String(encodedString));
        } else {
            enableDisableClick(true);
            Util.customToastShort(this, "Password should not be Empty");
            return;
        }
        Gson gson = new GsonBuilder().create();
        String customerData = gson.toJson(loginRequest);
        String url = "login/userauth";
        StringEntity custEntity = null;
        try {
            custEntity = new StringEntity(customerData, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpConnection = new ServiceListener(appState);
        progressBar.setCancelable(false);
        InputMethodManager im = (InputMethodManager)getSystemService(
                INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(view.getWindowToken(), 0);
        progressBar.show();
        httpConnection.sendRequest(url, null,
                ServiceListenerType.CUSTOMER_LOGIN, SyncHandler, "POST",
                custEntity);
    }

    @Override
    public void onStart() {
        super.onStart();
        android.util.Log.d("Loginactivity", "On start");
        enableDisableClick(true);
        try {
            ((ProgressBar) findViewById(R.id.progressBarhorizontal))
                    .setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e("error", e.toString(), e);
        }
        EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
    protected void onRestart() {
        super.onRestart();
        android.util.Log.d("Loginactivity", "On Restart Process");
    }

    @Override
    public void onUndo(Parcelable token) {
        // startActivity(new Intent(this, LoginActivity.class));
        // finish();
    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

        switch (what) {
            case CUSTOMER_LOGIN:
                progressBar.dismiss();
                LoginParsing login = new LoginUpdate();
                GlobalAppState appState = (GlobalAppState) getApplication();
                if (login.successfullLogin(message, this, mUndoBarController,
                        appState)) {
                    navigatePage();
                }
                break;
            case FEEDBACK_INSERT:
                progressBar.dismiss();
                String response = message.getString(ServiceListener.RESPONSEDATA);
                if(response.contains("true")){
                    Util.customToastMessage(this,getString(R.string.forgotpassmessage));
                }else{
                    Util.customToastMessage(this,"Customer does not exist");
                }
                Log.e("Message", "message:" + response);
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

    private void navigatePage() {
        startActivitiesUser(new Intent(this, DealsActivity.class), this);
    }

    private void enableDisableClick(boolean enable) {
        if (enable) {
            try {
                ((ProgressBar) findViewById(R.id.progressBarhorizontal))
                        .setVisibility(View.INVISIBLE);
            } catch (Exception e) {
                Log.e("error", e.toString(), e);
            }

        } else {
            ((ProgressBar) findViewById(R.id.progressBarhorizontal))
                    .setVisibility(View.VISIBLE);
        }
        ((RelativeLayout) findViewById(R.id.facebookbutton))
                .setClickable(enable);
        ((RelativeLayout) findViewById(R.id.twitterbutton))
                .setClickable(enable);
        ((RelativeLayout) findViewById(R.id.linkedinbutton))
                .setClickable(enable);
        ((Button) findViewById(R.id.buttonSignUp)).setClickable(enable);
        ((RelativeLayout) findViewById(R.id.googlebutton)).setClickable(enable);
    }
}
