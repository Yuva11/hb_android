package com.HungryBells.activity.subactivity;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.JsonParsing;
import com.HungryBells.util.Util;
import com.google.gson.Gson;

public class LoadChangeDealsActivity {
	ServiceListener httpConnection;
	CustomProgressDialog progressBar;
	Activity context;
	GlobalAppState appState;

	public LoadChangeDealsActivity(Activity activity,
			CustomProgressDialog progress, GlobalAppState appState) {
		httpConnection = new ServiceListener(appState);
		context = activity;
		progressBar = progress;
		this.appState = appState;
	}

	public void loadDeals() {
		String notificDeals = new Gson().toJson(appState
				.getDealChangeLocation());
		StringEntity se = null;
		try {
			se = new StringEntity(notificDeals, HTTP.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}

		progressBar.setCancelable(false);
		progressBar.show();
		String url = "searchdeals/" + appState.getProfile().getId();
		httpConnection.sendRequest(url, null, ServiceListenerType.GET_ALLDEALS,
				SyncHandler, "POST", se);

	}

	@SuppressLint("HandlerLeak")
	protected Handler SyncHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ServiceListenerType type = (ServiceListenerType) msg.obj;
			switch (type) {
			case GET_ALLDEALS:
				JsonParsing.allDealContents(msg.getData(), appState);
				progressBar.dismiss();
				dealsPageChange();
				break;
			default:
				progressBar.dismiss();
                try {
                    if (Util.checkNetworkAndLocation(context)) {
                        Util.customToast(context, context.getString(R.string.nonetwork));
                    }
                } catch (Exception e) {
                    Util.customToast(context, context.getString(R.string.nonetworkconn));
                }

				break;
			}

		}

	};

	public LoadChangeDealsActivity(Activity activity, GlobalAppState appState) {
		context = activity;
		this.appState = appState;
	}

	public void dealsPageChange() {
		String location = appState.getCity();
		Log.e("dealsPageChange", "Retrieving current Location " + location);
		if (appState.isSearchByLocation() == true) {
			location = appState.getActionBarLocation();
			Log.e("dealsPageChange", "Search by location is " + location);
		}
		if (location == null) {
			Log.d("Error in Location", "Serious Error:Location is null");
			return;
		}
		ContentDTO allContents = ContentsCache.getInstance().getContents()
				.get(location);

		appState.setAdvertisements(allContents.getAdvertisements());
		appState.setAllDeals(allContents.getDeals());
		appState.setAllPromos(allContents.getPromos());
		((TextView) context.findViewById(R.id.textViewlocation))
				.setText(truncateLocationString(location));
		if (context.getComponentName().getClassName()
				.contains("DealsActivity")) {
			Log.e("Inside", "com.finateltech.activity.DealsActivity");
			((DealsActivity) context).setPages();
		} else {
			Intent home = new Intent(context, DealsActivity.class);
			context.startActivity(home);
			context.overridePendingTransition(0, 0);
			context.finish();
		}
	}

	private String truncateLocationString(String location) {
		if (location.length() > 20) {
			location = location.substring(0, 18) + "..";
		}
		return location;
	}

}
