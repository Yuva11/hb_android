package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.brickred.socialauth.android.SocialAuthAdapter;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealFeedbackDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.adapter.CircularViewPagerHandler;
import com.HungryBells.activity.adapter.ViewPromosPagerAdapter;
import com.HungryBells.activity.subactivity.UpdateStatus;
import com.HungryBells.dialog.FeedbackAllDialog;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.DepthPageTransformer;
import com.HungryBells.util.JsonParsing;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewCouponActivity extends UserActivity implements OnClickListener {

	ImageLoader imageLoader;
	UpdateStatus update;
	Integer currentPage;
	GlobalAppState appState;
	ViewPromosPagerAdapter mCustomPagerAdapter;
	ViewPager mViewPager;
	List<Deals> deals;

	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewcoupon);
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
			try {
				httpConnection = new ServiceListener(appState);
				easyTracker.send(MapBuilder.createEvent("Coupon Code",
						"View Coupon Code", "View", null).build());
				imageLoader = ImageLoader.getInstance();
				currentPage = (Integer) getIntent().getExtras()
						.getSerializable("Coupon");
				// viewFlow = (ViewFlowEX) findViewById(R.id.couponsViewFlow);
				deals = appState.getAllPromos();
				mViewPager = (ViewPager) findViewById(R.id.pager);
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
	protected void onResume() {
		super.onResume();
	}

    public void couponsView() {
		mCustomPagerAdapter = new ViewPromosPagerAdapter(
				getSupportFragmentManager(), this, deals);

		mViewPager.setAdapter(mCustomPagerAdapter);
		mViewPager.setOnPageChangeListener(new CircularViewPagerHandler(
				mViewPager, this));
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setCurrentItem(currentPage);
		update = new UpdateStatus(this);
		((ImageView) findViewById(R.id.textViewlike)).setOnClickListener(this);
		((ImageView) findViewById(R.id.textViewshare)).setOnClickListener(this);
		((ImageView) findViewById(R.id.imageViewfb)).setOnClickListener(this);
		((ImageView) findViewById(R.id.imageViewtw)).setOnClickListener(this);
		((ImageView) findViewById(R.id.imageViewli)).setOnClickListener(this);
	}

	@Override
	public void onStart() {
		super.onStart();
		actionBarCreation(this);
		couponsView();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
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
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		case R.id.action_favrestaurent:
			Intent cart = new Intent(this, FavorateRestaurentActivity.class);
			startActivitiesUser(cart, this);
			break;
		case R.id.action_profile:
			Intent profile = new Intent(this, ProfileActivity.class);
			startActivitiesUser(profile, this);
			break;
		case R.id.action_feedback:
			Intent feedback = new Intent(this, FeedBackActivity.class);
			startActivity(feedback);
			break;
		case R.id.action_myorders:
			Intent myOrders = new Intent(this, MyOrdersActivity.class);
			startActivity(myOrders);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	int value = 0;

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
        if(networkChanges())
		switch (what) {
		case GET_ALLDEALS:
			JsonParsing.allDealContents(message, appState);
			value++;
			loadMainpage();
			break;
		case LIKE_COUPONS:
			checkAndChange();
			break;
		case GET_LOCATION:
			JsonParsing.jsonParsingCities(message, appState);
			value++;
			loadMainpage();
			break;
		case FEEDBACK_ADS:
			progressBar.dismiss();
			Util.customToast(this, getString(R.string.feedbackscuess));
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

	private void loadMainpage() {
		if (value == 2) {
			progressBar.dismiss();
			Intent home = new Intent(this, DealsActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivitiesUser(home, this);
			finish();
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

	private void checkAndChange() {
		boolean like = false;
		progressBar.dismiss();
		if (appState.getAllPromos().get(currentPage).getIsliked()) {
            Util.customToast(this, getString(R.string.likeremovesucuess));
			((ImageView) findViewById(R.id.textViewlike))
					.setImageResource(R.drawable.likes);
		} else {
			like = true;
            Util.customToast(this, getString(R.string.likesucuess));
			((ImageView) findViewById(R.id.textViewlike))
					.setImageResource(R.drawable.unlikes);
		}
		appState.getAllPromos().get(currentPage).setIsliked(like);
		Map<String, ContentDTO> contents = ContentsCache.getInstance()
				.getContents();
		ContentDTO contentsNew = contents.get(appState.getActionBarLocation());
		contentsNew.setPromos(appState.getAllPromos());
		ContentsCache.getInstance().getContents()
				.put(appState.getActionBarLocation(), contentsNew);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.textViewlike:
			addCouponLike();
			break;
		case R.id.textViewshare:
			new FeedbackAllDialog(this).show();
			break;
		case R.id.imageViewfb:
            if(networkChanges()) {
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                update.updateStatus(SocialAuthAdapter.Provider.FACEBOOK, progressBar);
            }
			break;
		case R.id.imageViewtw:
            if(networkChanges()) {
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                update.updateStatus(SocialAuthAdapter.Provider.TWITTER, progressBar);
            }
			break;
		case R.id.imageViewli:
            if(networkChanges()) {
                progressBar = new CustomProgressDialog(this);
                progressBar.show();
                update.updateStatus(SocialAuthAdapter.Provider.LINKEDIN, progressBar);
            }
			break;
		default:
			break;
		}
		// TODO Auto-generated method stub

	}

	public void submitFeedback(String editTextComments) {
		try {
			String url = "deal/feedback";
			DealFeedbackDTO ads = new DealFeedbackDTO();
			Customers customer = new Customers();
			Deals content = new Deals();
			customer.setId(appState.getProfile().getId());
			content.setId(appState.getAllPromos().get(currentPage).getId());
			ads.setCustomer(customer);
			ads.setDeal(content);
			ads.setFeedback(editTextComments);
			StringEntity custEntity = null;
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
			progressBar.setCancelable(false);
			progressBar.show();
			String customerData = gson.toJson(ads);
			custEntity = new StringEntity(customerData, HTTP.UTF_8);
			httpConnection.sendRequest(url, null,
					ServiceListenerType.FEEDBACK_ADS, SyncHandler, "POST",
					custEntity);
		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}

	}

	public boolean checkIsLiked(int position) {
		currentPage = position;
		if (appState.getAllPromos().get(position).getIsliked()) {
			((ImageView) findViewById(R.id.textViewlike))
					.setImageResource(R.drawable.unlikes);
			return true;
		} else {
			((ImageView) findViewById(R.id.textViewlike))
					.setImageResource(R.drawable.likes);

			return false;
		}

	}

	private void addCouponLike() {
		progressBar.setCancelable(false);
		progressBar.show();
		String url = "deal/like/"
				+ appState.getAllPromos().get(currentPage).getId() + "/"
				+ appState.getProfile().getId();
		httpConnection.sendRequest(url, null, ServiceListenerType.LIKE_COUPONS,
				SyncHandler, "PUT", null);

	}
}
