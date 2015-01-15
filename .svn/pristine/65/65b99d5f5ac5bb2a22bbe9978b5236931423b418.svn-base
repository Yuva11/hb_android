package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.taptwo.android.widget.ViewFlowEX;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.HungryBells.DTO.AdFeedbackDTO;
import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.adapter.AdvertisementViewAdapter;
import com.HungryBells.activity.adapter.CircularViewPagerHandler;
import com.HungryBells.activity.adapter.ViewAdsPagerAdapter;
import com.HungryBells.activity.subactivity.UpdateStatus;
import com.HungryBells.dialog.FeedbackAdDialog;
import com.HungryBells.fragments.ViewAdsFragment;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.CustomViewPager;
import com.HungryBells.util.DepthPageTransformer;
import com.HungryBells.util.Util;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@SuppressLint("InflateParams")
public class ViewAdsActivity extends UserActivity implements OnClickListener {
	private CustomViewPager viewPager;
	Integer urlLoad;
	UpdateStatus update;
	ViewAdsPagerAdapter mCustomPagerAdapter;
	GlobalAppState appState;
    private ViewFlowEX viewFlow;
    RelativeLayout customLayout;
    AdvertisementViewAdapter mAdsPagerAdapter;
    private boolean isPlayVideo = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion>18)
		setContentView(R.layout.activity_viewaddeals);
        else{
            setContentView(R.layout.acitvity_viewadsdeals);
        }
		appState = (GlobalAppState) getApplication();
		if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
			try {
				httpConnection = new ServiceListener(appState);
				urlLoad = (Integer) getIntent().getExtras().getSerializable(
						"ADV URL");

                if(currentapiVersion>18) {
                    viewPager = (CustomViewPager) findViewById(R.id.adspager);
                    setViewPagerAdapter();
                }else{
                    viewFlow = (ViewFlowEX) findViewById(R.id.advertisementFlow);
                    setViewFlowAdapter();
                }
			} catch (Exception e) {
				Log.e("View Ads", e.toString(), e);
			}
		} else {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}

	}

	public void setViewPagerAdapter() {
		try {
			mCustomPagerAdapter = new ViewAdsPagerAdapter(
					getSupportFragmentManager(), this,
					appState.getAdvertisements());
			viewPager.setAdapter(mCustomPagerAdapter);
			viewPager.setOnPageChangeListener(new CircularViewPagerHandler(
					viewPager, this));
			viewPager.setPageTransformer(true, new DepthPageTransformer());
			viewPager.setCurrentItem(urlLoad);
			update = new UpdateStatus(this);
            customLayout = (RelativeLayout)getLayoutInflater().
                    inflate(R.layout.pop_up_action, null);

            FloatingActionButton pink_icon= (FloatingActionButton)
                    findViewById(R.id.pink_icon);
            pink_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkIsLiked(urlLoad);
                    mCustomPagerAdapter.notifyDataSetChanged();
                 /*   customQuickAction.show(view);*/
                }
            });

		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}
	}
    public void pageChanged(int position){
        urlLoad = position;
    }

    private void setViewFlowAdapter() {
        try {
            prefs = getSharedPreferences("HB", MODE_PRIVATE);
            String backColor = "#"
                    + prefs.getString("BACKGROUND_COLOR", "000000");
            mAdsPagerAdapter=new AdvertisementViewAdapter(this,
                    getApplication(), getWindowManager().getDefaultDisplay(),
                    appState.getAdvertisements(), viewFlow);
            viewFlow.setAdapter(mAdsPagerAdapter);
            viewFlow.setSelection(urlLoad);
            update = new UpdateStatus(this);
            customLayout = (RelativeLayout)getLayoutInflater().
                    inflate(R.layout.pop_up_action, null);

            FloatingActionButton pink_icon= (FloatingActionButton)
                    findViewById(R.id.pink_icon);
            pink_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAdsPagerAdapter.pageChange(urlLoad);
                }
            });
            viewFlow.setOnViewSwitchListener(new ViewFlowEX.ViewSwitchListener() {

                @Override
                public void onSwitched(View view, int position) {
                    Log.e(":" + position, checkIsLiked(position) + "");
                    urlLoad = position;

                }
            });

        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }
	public boolean checkIsLiked(int position) {
		urlLoad = position;
		if (appState.getAdvertisements().get(position).getIsliked()) {
			((ImageView) findViewById(R.id.textViewAdslike))
					.setImageResource(R.drawable.unlikes);
			return true;
		} else {
			((ImageView)findViewById(R.id.textViewAdslike))
					.setImageResource(R.drawable.likes);

			return false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
 public void pageScrollAds(boolean scroll){
     viewPager.setPagingEnabled(scroll);
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
			startActivitiesUser(feedback, this);
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
		actionBarCreation(this);
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
    public void playVideo(boolean value){
        isPlayVideo = value;
    }
	@Override
	public void onBackPressed() {
        if(isPlayVideo){
            isPlayVideo = false;
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if(currentapiVersion>18)
              setViewPagerAdapter();
            return;
        }
		super.onBackPressed();
	}

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
		switch (what) {
		case LIKE_ADS:
			checkAndChange();
			break;
		case FEEDBACK_ADS:
			progressBar.dismiss();
            dataChange();
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
  private void dataChange(){
      int currentApiVersion = android.os.Build.VERSION.SDK_INT;
      if(currentApiVersion>18) {
          mCustomPagerAdapter.notifyDataSetChanged();
      }else{
          mAdsPagerAdapter.pageChange(urlLoad);
          mAdsPagerAdapter.notifyDataSetChanged();
      }
  }
	private void checkAndChange() {
		boolean like = false;
		progressBar.dismiss();
		if (appState.getAdvertisements().get(urlLoad).getIsliked()) {
			Util.customToast(this, getString(R.string.likeremovesucuess));

			((ImageView)  findViewById(R.id.textViewAdslike))
					.setImageResource(R.drawable.likes);
		} else {
			like = true;
			Util.customToast(this, getString(R.string.likesucuess));
			((ImageView)  findViewById(R.id.textViewAdslike))
					.setImageResource(R.drawable.unlikes);
		}
		appState.getAdvertisements().get(urlLoad).setIsliked(like);
		Map<String, ContentDTO> contents = ContentsCache.getInstance()
				.getContents();
		ContentDTO contentsNew = contents.get(appState.getActionBarLocation());
		contentsNew.setAdvertisements(appState.getAdvertisements());
		ContentsCache.getInstance().getContents()
				.put(appState.getActionBarLocation(), contentsNew);
        dataChange();
	}

	@Override
	public void onUndo(Parcelable token) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textViewfeedback:
			new FeedbackAdDialog(this).show();
			break;
		case R.id.imageViewfb:
			update.updateStatus(SocialAuthAdapter.Provider.FACEBOOK,progressBar);
			break;
		case R.id.imageViewtw:
			update.updateStatus(SocialAuthAdapter.Provider.TWITTER,progressBar);
			break;
		case R.id.imageViewli:
			update.updateStatus(SocialAuthAdapter.Provider.LINKEDIN,progressBar);
			break;
		default:
			break;
		}
		// TODO Auto-generated method stub

	}
   public void adFeedBack(){
       Log.e("Ads","Ad changed feedback");
       new FeedbackAdDialog(this).show();
   }

    public void updateStatus(SocialAuthAdapter.Provider provider){
        progressBar = new CustomProgressDialog(this);
        progressBar.show();
        Log.e("Provider","Provider"+provider.toString());
        update.updateStatus(provider,progressBar);
        dataChange();
    }


	public void submitFeedback(String editTextComments) {
        StringEntity custEntity = null;
		try {
			String url = "content/feedback";
			AdFeedbackDTO ads = new AdFeedbackDTO();
			Customers customer = new Customers();
			ContentDealDTO content = new ContentDealDTO();
			customer.setId(appState.getProfile().getId());
			content.setId(appState.getAdvertisements().get(urlLoad).getId());
			ads.setCustomer(customer);
			ads.setAdvertisement(content);
			ads.setFeedback(editTextComments);

			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.create();
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

	public void addAdvertisementLike() {
		progressBar.setCancelable(false);
		progressBar.show();

		String url = "content/like/"
				+ appState.getAdvertisements().get(urlLoad).getId() + "/"
				+ appState.getProfile().getId();
		httpConnection.sendRequest(url, null, ServiceListenerType.LIKE_ADS,
				SyncHandler, "PUT", null);

	}

	// To get status of message after authentication

}
