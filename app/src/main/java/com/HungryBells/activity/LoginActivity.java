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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.subactivity.AnimationRunActivty;
import com.HungryBells.service.LoginService;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import static com.HungryBells.util.ServiceType.FACEBOOK;
import static com.HungryBells.util.ServiceType.GOOGLEPLUS;
import static com.HungryBells.util.ServiceType.LINKEDIN;
import static com.HungryBells.util.ServiceType.TWITTER;

public class LoginActivity extends UserActivity implements OnClickListener {

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
            AnimationRunActivty animations = new AnimationRunActivty(this);
            animations.runAnimation(R.id.imageViewlogos);
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
                    singnInTransition();
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
        startActivitiesUser(new Intent(this, LoggingInActivity.class), this);
        finish();
        overridePendingTransition(0, R.anim.login_signupanimation);

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
        // TODO Auto-generated method stub

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
