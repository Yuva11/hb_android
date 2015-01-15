package com.HungryBells.activity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.HungryBells.DTO.FeedbackDTO;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FeedBackActivity extends UserActivity implements OnClickListener {
	int value = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
			try {
				httpConnection = new ServiceListener(appState);
				mUndoBarController = new UndoBarController(
						findViewById(R.id.undobar), this);
				easyTracker.send(MapBuilder.createEvent("FeedBacks Details",
						"FeedBacks Details Page", "FeedBacks", null).build());
				((ImageView) findViewById(R.id.buttonfeedbacksubmit))
						.setOnClickListener(this);
			} catch (Exception e) {
				startActivity(new Intent(this, MainActivity.class));
				finish();
			}
		} else {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onStart() {
		try {
			super.onStart();
			actionBarCreation(this);
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

	private void feedbackSubmit() {
		EditText feedback = (EditText) findViewById(R.id.editTextComments);
		FeedbackDTO feedBackDTO = new FeedbackDTO();
		String feedbackText = feedback.getText().toString().trim();
		if (feedbackText != null && feedbackText.length() == 0) {
			Util.customToast(this, getString(R.string.emptyreview));
			return;
		} else if (feedbackText.length() < 3) {
			Util.customToast(this, getString(R.string.emptyreviewchars));
			return;
		}
		feedBackDTO.setComment(feedbackText);
		StringEntity custEntity = null;
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.create();
		String customerData = gson.toJson(feedBackDTO);
		try {
			custEntity = new StringEntity(customerData, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		progressBar.setCancelable(false);
		progressBar.show();
		httpConnection = new ServiceListener(appState);
		GlobalAppState appState = (GlobalAppState) getApplication();
		String url = "feedback/add/" + appState.getProfile().getId();
		httpConnection.sendRequest(url, null,
				ServiceListenerType.FEEDBACK_INSERT, SyncHandler, "POST",
				custEntity);

	}

	private void jsonParsingFavRest(Bundle data) {
		Util.customToast(this, getString(R.string.feedbackscuess));
		startActivity(new Intent(this, DealsActivity.class));
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
			startActivitiesUser(new Intent(this, DealsActivity.class), this);
			finish();
			break;

		case R.id.action_favrestaurent:
			Intent cart = new Intent(this, FavorateRestaurentActivity.class);
			startActivitiesUser(cart, this);
			break;
		case R.id.action_profile:
			Intent profile = new Intent(this, ProfileActivity.class);
			startActivitiesUser(profile, this);
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
		getMenuInflater().inflate(R.menu.feedback, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (Util.checkNetworkAndLocation(this)) {
			feedbackSubmit();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {

		switch (what) {
		case FEEDBACK_INSERT:
			progressBar.dismiss();
			jsonParsingFavRest(message);
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
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub

	}
}
