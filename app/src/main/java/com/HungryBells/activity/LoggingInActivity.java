package com.HungryBells.activity;

import static com.HungryBells.util.ServiceType.FACEBOOK;
import static com.HungryBells.util.ServiceType.GOOGLEPLUS;
import static com.HungryBells.util.ServiceType.LINKEDIN;
import static com.HungryBells.util.ServiceType.TWITTER;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.HungryBells.DTO.LoginRequestDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.intefaceImpl.LoginUpdate;
import com.HungryBells.activity.intefaces.LoginParsing;
import com.HungryBells.dialog.DeliveryAddressDialog;
import com.HungryBells.dialog.ForgotPasswordDialog;
import com.HungryBells.service.LoginService;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoggingInActivity extends UserActivity implements OnClickListener {
   View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlogin);

		mUndoBarController = new UndoBarController(findViewById(R.id.undobar),
				this);
		easyTracker.send(MapBuilder.createEvent("Signup Details",
				"Signup Details Page", "Signup", null).build());
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(" Log In");
		((LinearLayout) findViewById(R.id.facebookbutton))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.twitterbutton))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.linkedinbutton))
				.setOnClickListener(this);
		((LinearLayout) findViewById(R.id.googlebutton))
				.setOnClickListener(this);
		String htmlString = "<u><font color=\"#0000FF\">Terms of Use</font></u>";
		((TextView) findViewById(R.id.textViewtermsuse)).setText(Html
				.fromHtml(htmlString));
		htmlString = "<u><font color=\"#0000FF\">Privacy Policy</font></u>";
		((TextView) findViewById(R.id.textViewprivacy)).setText(Html
				.fromHtml(htmlString));
		((Button) findViewById(R.id.buttonLogin)).setOnClickListener(this);
		String forgetString = "<u>Forgot Password?</u>";
		((TextView) findViewById(R.id.textViewForgetPassword)).setText(Html
				.fromHtml(forgetString));
        ((TextView) findViewById(R.id.textViewForgetPassword)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new ForgotPasswordDialog(LoggingInActivity.this).show();
            }
        });
		((TextView) findViewById(R.id.textViewprivacy))
				.setOnClickListener(this);
		((TextView) findViewById(R.id.textViewtermsuse))
				.setOnClickListener(this);
	}
    public  void submitMailId(String emailId){
        httpConnection = new ServiceListener(appState);
        String url = "mailer/forgotpassword/"
                + emailId+"/customer";
        httpConnection.sendRequest(url, null,
                ServiceListenerType.FEEDBACK_INSERT, SyncHandler, "GET",
                null);


    }
	@Override
	public void onClick(View v) {
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
		case R.id.buttonLogin:
			loggingIn();
			break;

		case R.id.textViewtermsuse:
			Intent intent = new Intent(this, TermsConditionActivity.class);
			intent.putExtra("key", "terms");
            intent.putExtra("keyName","up");
			startActivitiesUser(intent, this);
			finish();
			overridePendingTransition(0, R.anim.login_signupanimation);
			break;
		case R.id.textViewprivacy:
			Intent intentPrivacy = new Intent(this,
					TermsConditionActivity.class);
			intentPrivacy.putExtra("key", "privacy");
            intentPrivacy.putExtra("keyName","up");
			startActivitiesUser(intentPrivacy, this);
			finish();
			overridePendingTransition(0, R.anim.login_signupanimation);
			break;
		}

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

	public void enableDisableClick(boolean enable) {
		((LinearLayout) findViewById(R.id.facebookbutton)).setClickable(enable);
		((LinearLayout) findViewById(R.id.twitterbutton)).setClickable(enable);
		((LinearLayout) findViewById(R.id.linkedinbutton)).setClickable(enable);
		((LinearLayout) findViewById(R.id.googlebutton)).setClickable(enable);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
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
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        startActivity(new Intent(this,LoginActivity.class));
	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub

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


	@Override
	protected void onResume() {
		super.onResume();
	}

}
