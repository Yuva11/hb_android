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

/*This class is used to view Advertisement*/
@SuppressLint("InflateParams")
public class ViewAdsActivity extends UserActivity implements OnClickListener {

   /*Viewpager instance*/
	private CustomViewPager viewPager;

    /*User ads current index*/
	Integer urlLoad;

    /*Interface for status update*/
	UpdateStatus update;

    /*Ads page adapter*/
	ViewAdsPagerAdapter mCustomPagerAdapter;

    /* Global application state Object where all contextual information is stored */
	GlobalAppState appState;

/*Viewflow instance*/
    private ViewFlowEX viewFlow;

    /*Relativelayout instance*/
    RelativeLayout customLayout;

   /*adapter instance*/
    AdvertisementViewAdapter mAdsPagerAdapter;

    /*check for playvideo*/
    private boolean isPlayVideo = false;

    MenuItem like_button;

    /*Check for version and change to different layout
    * Layout for more than 18 view pager and other viewflow
    * */
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

    /*  This method is used to create adapter using viewpager
    And set that adapter to Viewpager
    Set animation for swipe using  DepthPageTransformer*/
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

            // dice button set visibility to gone
            //pink_icon.setVisibility(View.GONE);
            pink_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkIsLiked(urlLoad);
                    mCustomPagerAdapter.notifyDataSetChanged();
                /*customQuickAction.show(view); */
                }
            });

		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}
	}
    public void pageChanged(int position){
        urlLoad = position;
    }

    /*  This method is used to create adapter using viewflow*/
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

            // dice button set visibility to gone
            //pink_icon.setVisibility(View.GONE);
            pink_icon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //mAdsPagerAdapter.pageChange(urlLoad);
                    adFeedBack();
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

    /*Check if the current deal is liked by user or not*/
	public boolean checkIsLiked(int position) {
		urlLoad = position;
		if (appState.getAdvertisements().get(position).getIsliked()) {

            like_button.setIcon(R.drawable.ic_action_like_full);
            //((ImageView) findViewById(R.id.textViewAdslike)).setImageResource(R.drawable.unlikes);
			return true;
		} else {
            like_button.setIcon(R.drawable.ic_action_like_empty);

			//((ImageView)findViewById(R.id.textViewAdslike)).setImageResource(R.drawable.likes);

			return false;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

    /*this method cloteol viewpager scroll*/
 public void pageScrollAds(boolean scroll){
     viewPager.setPagingEnabled(scroll);
 }

    /*
* This method will fetch menu icons to be dispalyed dispalyed in action overflow button
* */
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

    /*
    * This method will be called when the action overflow button is pressed and the menu item is selected
    * */
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
            case R.id.action_like:

                addAdvertisementLike();

                break;
            case R.id.action_share:
                // Share the item on web
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Content");
                startActivity(Intent.createChooser(shareIntent, "Share via"));
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

    /*
    * Inflating the menus in the Action overflow button
    * */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.login, menu);
        getMenuInflater().inflate(R.menu.whats_new_detail_menu, menu);
        like_button = menu.findItem(R.id.action_like);

        checkIsLiked(urlLoad);


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

    /*This method is used to check videoplay*/
    public void playVideo(boolean value){
        isPlayVideo = value;
    }

    /*on backpressed check*/
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

    /* Concrete method from useractivity used to receive datas to HB Server*/
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
    /*reload the adapter*/
  private void dataChange(){
      int currentApiVersion = android.os.Build.VERSION.SDK_INT;
      if(currentApiVersion>18) {
          mCustomPagerAdapter.notifyDataSetChanged();
      }else{
          mAdsPagerAdapter.pageChange(urlLoad);
          mAdsPagerAdapter.notifyDataSetChanged();
      }
  }


    /*This method check the user ad liked or not and change background and show toast to user*/
	private void checkAndChange() {
		boolean like = false;
		progressBar.dismiss();
		if (appState.getAdvertisements().get(urlLoad).getIsliked()) {
			Util.customToast(this, getString(R.string.likeremovesucuess));

            like_button.setIcon(R.drawable.ic_action_like_empty);
			//((ImageView)  findViewById(R.id.textViewAdslike)).setImageResource(R.drawable.likes);
		} else {
			like = true;
			Util.customToast(this, getString(R.string.likesucuess));

            like_button.setIcon(R.drawable.ic_action_like_full);

			//((ImageView)  findViewById(R.id.textViewAdslike)).setImageResource(R.drawable.unlikes);
		}
		appState.getAdvertisements().get(urlLoad).setIsliked(like);

        /*
		Map<String, ContentDTO> contents = ContentsCache.getInstance()
				.getContents();
		ContentDTO contentsNew = contents.get(appState.getActionBarLocation());
		contentsNew.setAdvertisements(appState.getAdvertisements());
		ContentsCache.getInstance().getContents()
				.put(appState.getActionBarLocation(), contentsNew);
        dataChange();
        */
	}

	@Override
	public void onUndo(Parcelable token) {

	}
    /*on click method to the button*/
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
    /*This method is used to create dialog box for feedback*/
   public void adFeedBack(){
       Log.e("Ads","Ad changed feedback");
       new FeedbackAdDialog(this).show();
   }
    /*Used to call the status update*/
    public void updateStatus(SocialAuthAdapter.Provider provider){
        progressBar = new CustomProgressDialog(this);
        progressBar.show();
        Log.e("Provider","Provider"+provider.toString());
        update.updateStatus(provider,progressBar);
        dataChange();
    }

    /*This method is used to send feedback for current ad user viewing to HB server*/
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
    /*This method is used to send like request to HB server*/
	public void addAdvertisementLike() {
		//progressBar.setCancelable(false);
		//progressBar.show();

		String url = "content/like/"
				+ appState.getAdvertisements().get(urlLoad).getId() + "/"
				+ appState.getProfile().getId();
		httpConnection.sendRequest(url, null, ServiceListenerType.LIKE_ADS,
				SyncHandler, "PUT", null);

	}

	// To get status of message after authentication

}
