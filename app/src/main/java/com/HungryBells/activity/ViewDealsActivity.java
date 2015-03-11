package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.brickred.socialauth.android.SocialAuthAdapter;

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
import android.widget.Toast;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealFeedbackDTO;
import com.HungryBells.DTO.DealStatus;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.adapter.CircularViewPagerHandler;
import com.HungryBells.activity.adapter.ViewDealsPagerAdapter;
import com.HungryBells.activity.subactivity.ChangeBackgroundViewDeal;
import com.HungryBells.activity.subactivity.UpdateStatus;
import com.HungryBells.dialog.FeedbackAllDialog;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.ContentsCache;
import com.HungryBells.util.CustomProgressDialog;
import com.HungryBells.util.DepthPageTransformer;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/*This class is used to view deals*/
public class ViewDealsActivity extends UserActivity implements
        UndoBarController.UndoListener, OnClickListener {

    /*Custome toast with undo*/
    UndoBarController mUndoBarController;

    /*not used*/
    ChangeBackgroundViewDeal changeBackground;

    /*Interface for status update*/
    UpdateStatus update;

    /*User deals current index*/
    int currentPage;

    /*list of deals elements*/
    List<Deals> deals;

    /* Global application state Object where all contextual information is stored */
    public static GlobalAppState appState;

    /*Deals page adapter*/
    ViewDealsPagerAdapter mCustomPagerAdapter;

    /* ViewDealsactivity swipe using viewpager*/
    ViewPager mViewPager;

    MenuItem like_button;

    /*
  * Initiaizing httpConnection,viewpager
  *
  * OnCreate Method for Deals Activity page
  * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_viewdeals);
        appState = (GlobalAppState) getApplication();
        android.util.Log.d("Inside View deals", "Inside on create View Deals");
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                httpConnection = new ServiceListener(appState);
                easyTracker.send(MapBuilder.createEvent("View Deal",
                        "View Single Deal Process", "View", null).build());
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                deals = appState.getAllDeals();
                currentPage = (Integer) getIntent().getExtras()
                        .getSerializable("Deals");
                mViewPager = (ViewPager) findViewById(R.id.pager);

            } catch (Exception e) {
                Log.e(e.toString(), e.toString(), e);
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

    @Override
    public void onStart() {
        super.onStart();
        actionBarCreation(this);
        //actionBarDetailsCreation(this);
        viewDealDetails();
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

  /*  This method is used to create adapter
    And set that adapter to Viewpager
    Set animation for swipe using  DepthPageTransformer*/
    public void viewDealDetails() {
        try {
            mCustomPagerAdapter = new ViewDealsPagerAdapter(
                    getSupportFragmentManager(), this, deals);

            mViewPager.setAdapter(mCustomPagerAdapter);

            /*
            mViewPager.setOnPageChangeListener(new CircularViewPagerHandler(
                    mViewPager, this));
            mViewPager.setPageTransformer(true, new DepthPageTransformer());
            */
            mViewPager.setCurrentItem(currentPage);
            update = new UpdateStatus(this);
            ((ImageView) findViewById(R.id.textViewlike))
                    .setOnClickListener(this);
            ((ImageView) findViewById(R.id.textViewshare))
                    .setOnClickListener(this);
            ((ImageView) findViewById(R.id.imageViewfb))
                    .setOnClickListener(this);
            ((ImageView) findViewById(R.id.imageViewtw))
                    .setOnClickListener(this);
            ((ImageView) findViewById(R.id.imageViewli))
                    .setOnClickListener(this);

        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }

    }
    /*Check if the current deal is liked by user or not*/
    public boolean checkIsLiked(int position) {
        currentPage = position;
        if (appState.getAllDeals().get(position).getIsliked()) {
            like_button.setIcon(R.drawable.unlikes);
            //((ImageView) findViewById(R.id.textViewlike))
                    //.setImageResource(R.drawable.unlikes);
            return true;
        } else {
            like_button.setIcon(R.drawable.likes);
            //((ImageView) findViewById(R.id.textViewlike))
                    //.setImageResource(R.drawable.likes);

            return false;
        }

    }

    /*This method is used to send feedback for current deal user viewing to HB server*/
    public void submitFeedback(String editTextComments) {
        try {
            String url = "deal/feedback";
            DealFeedbackDTO ads = new DealFeedbackDTO();
            Customers customer = new Customers();
            Deals content = new Deals();
            customer.setId(appState.getProfile().getId());
            content.setId(appState.getAllDeals().get(currentPage).getId());
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

    /*This method is used to set current deals restaurant into favorite*/
    public void addToFavRestaurent(Deals deals) {
        try {
            progressBar = new CustomProgressDialog(this);
            progressBar.setCancelable(false);
            progressBar.show();
            Deals currentDeal = deals;
            int favId = 1;
            if (getIsFavorate(deals)) {
                favId = 2;
            }
            String url = "customer/updaterestaurant/"
                    + appState.getProfile().getId() + "/"
                    + currentDeal.getMerchantbranch().getId() + "/" + favId;
            httpConnection.sendRequest(url, null, ServiceListenerType.FAV_REST,
                    SyncHandler, "POST", null);
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
        }
    }

    /*This method return true if the current restaurant is favorite for user*/
    private boolean getIsFavorate(Deals item) {
        try {
            for (MerchantBranchDto merchatBranch : ViewDealsActivity.appState
                    .getMerchatBranch()) {
                if (item.getMerchantbranch().getId()
                        .equals(merchatBranch.getId())) {
                    item.setFavourite(true);
                    return true;
                }
            }
            item.setFavourite(false);
            return false;
        } catch (Exception e) {
            Log.e("Error", e.toString(), e);
            return false;
        }

    }

    /*This method is used to favorite the restaurant or removes*/
    private void jsonParsinginDeals(Bundle data) {
        String response = data.getString(ServiceListener.RESPONSEDATA);
        if (response.contains("true")) {
            Deals currentDeal = appState.getAllDeals().get(currentPage);
            if (!currentDeal.isFavourite()) {
                Util.customToastMessage(this, getString(R.string.addedfav));
                addMerchantBranch(currentDeal.getMerchantbranch());
            } else {
                Util.customToastMessage(this, getString(R.string.removefav));
                removeMerchantBranch(currentDeal.getMerchantbranch());
            }
        }
    }

    /*This method navigate the used to order the deal
    And also this method is used to get last order address for m the HB server*/
    public void buyNowThisDeal(Deals item) {
        if (item.getOpeningQuantity() == 0||!getStatus(item)) {
            return;
        }
        String url = getString(R.string.serverurl_test)+"dealorder/getmyaddress/"+ appState.getProfile().getId();
        Log.e("Message",url);
        Ion.with(this).load(url).asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.e("Result","Result"+result);
                        if(result!=null)
                            OrderSummaryActivity.deliveryAddress = result;
                        else{
                            OrderSummaryActivity.deliveryAddress = "";
                        }
                    }
                });
        Intent viewadvertisement = new Intent(this,
                OrderSummaryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Deals", item);
        viewadvertisement.putExtras(bundle);
        startActivitiesUser(viewadvertisement, this);



    }

    /*This method is used to send current status of the deal*/
    private boolean getStatus(Deals item){
        DealStatus currentStatus = item.getStatus();
        switch(currentStatus){
            case ACTIVE:
                return false;
            case PUBLISHED:
                return true;
            case EXPIRED:
                Toast.makeText(this,"Deal time Expired",Toast.LENGTH_SHORT).show();
                return false;
            case BLOCKED:
                Toast.makeText(this,"Deal Blocked",Toast.LENGTH_SHORT).show();
                return false;
        }
        return false;
    }

    /*
  * This method will fetch menu icons to be dispalyed dispalyed in action overflow button
  * */

    /*
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
    */

    /*
   * This method will be called when the action overflow button is pressed and the menu item is selected
   * */
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
                startActivitiesUser(feedback, this);
                break;
            case R.id.action_myorders:
                Intent myOrders = new Intent(this, MyOrdersActivity.class);
                startActivitiesUser(myOrders, this);
                break;


            case R.id.action_like:

                addCouponLike();

                break;
            case R.id.action_share:
                // Share the item on web
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Content");
                startActivity(Intent.createChooser(shareIntent, "Share via"));

                break;
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
        getMenuInflater().inflate(R.menu.best_pick_detail_menu, menu);

        like_button = menu.findItem(R.id.action_like);

        checkIsLiked(currentPage);

        return true;
    }

    @Override
    public void onUndo(Parcelable token) {

    }

    /* Concrete method from useractivity used to receive datas to HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        if(networkChanges())
            switch (what) {
                case FAV_REST:
                    progressBar.dismiss();
                    jsonParsinginDeals(message);
                    break;
                case LIKE_COUPONS:
                    checkAndChange();
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

    /*This method check the user deal liked or not and change background and show toast to user*/
    private void checkAndChange() {
        progressBar.dismiss();
        boolean like = false;
        if (appState.getAllDeals().get(currentPage).getIsliked()) {
            Util.customToast(this, getString(R.string.likeremovesucuess));
            like_button.setIcon(R.drawable.likes);
            //((ImageView) findViewById(R.id.textViewlike)).setImageResource(R.drawable.likes);
        } else {
            like = true;
            Util.customToast(this, getString(R.string.likesucuess));
            like_button.setIcon(R.drawable.unlikes);
            //((ImageView) findViewById(R.id.textViewlike)).setImageResource(R.drawable.unlikes);
        }
        appState.getAllDeals().get(currentPage).setIsliked(like);
        Map<String, ContentDTO> contents = ContentsCache.getInstance()
                .getContents();
        ContentDTO contentsNew = contents.get(appState.getActionBarLocation());
        contentsNew.setDeals(appState.getAllDeals());
        ContentsCache.getInstance().getContents()
                .put(appState.getActionBarLocation(), contentsNew);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*on click method to the button*/
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.textViewlike:
                addCouponLike();
                break;
            case R.id.textViewshare:
                new FeedbackAllDialog(this).show();
                break;
            case R.id.imageViewfavrest:
                break;
            case R.id.buythisdeal:
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

    /*This method will send the like request to HB server*/
    private void addCouponLike() {
        progressBar.setCancelable(false);
        progressBar.show();
        String url = "deal/like/"
                + appState.getAllDeals().get(currentPage).getId() + "/"
                + appState.getProfile().getId();
        httpConnection.sendRequest(url, null, ServiceListenerType.LIKE_COUPONS,
                SyncHandler, "PUT", null);

    }

    /*This method will remove the like from appstate favorite restaurant*/
    private void removeMerchantBranch(MerchantBranchDto merchantBranchDto) {
        List<MerchantBranchDto> favMerchants = appState.getMerchatBranch();
        favMerchants.remove(merchantBranchDto);
        appState.setMerchatBranch(favMerchants);
        mCustomPagerAdapter.notifyDataSetChanged();
    }

    /*This method used to add like to favorite restaurant in cache*/
    private void addMerchantBranch(MerchantBranchDto merchantBranchDto) {
        if (appState.getMerchatBranch() == null) {
            appState.setMerchatBranch(new ArrayList<MerchantBranchDto>());
        }
        List<MerchantBranchDto> favMerchants = appState.getMerchatBranch();
        favMerchants.add(0, merchantBranchDto);
        appState.setMerchatBranch(favMerchants);
        mCustomPagerAdapter.notifyDataSetChanged();
    }

}
