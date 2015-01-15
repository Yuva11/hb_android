package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
import android.widget.Toast;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealFeedbackDTO;
import com.HungryBells.DTO.DealStatus;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.Status;
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

@SuppressLint("InflateParams")
public class ViewDealsActivity extends UserActivity implements
        UndoBarController.UndoListener, OnClickListener {
    UndoBarController mUndoBarController;
    ChangeBackgroundViewDeal changeBackground;
    UpdateStatus update;
    int currentPage;
    List<Deals> deals;
    public static GlobalAppState appState;
    ViewDealsPagerAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

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

    public void viewDealDetails() {
        try {
            mCustomPagerAdapter = new ViewDealsPagerAdapter(
                    getSupportFragmentManager(), this, deals);

            mViewPager.setAdapter(mCustomPagerAdapter);
            mViewPager.setOnPageChangeListener(new CircularViewPagerHandler(
                    mViewPager, this));
            mViewPager.setPageTransformer(true, new DepthPageTransformer());
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

    public boolean checkIsLiked(int position) {
        currentPage = position;
        if (appState.getAllDeals().get(position).getIsliked()) {
            ((ImageView) findViewById(R.id.textViewlike))
                    .setImageResource(R.drawable.unlikes);
            return true;
        } else {
            ((ImageView) findViewById(R.id.textViewlike))
                    .setImageResource(R.drawable.likes);

            return false;
        }

    }

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
    public void onUndo(Parcelable token) {

    }

    int value = 0;

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

    private void checkAndChange() {
        progressBar.dismiss();
        boolean like = false;
        if (appState.getAllDeals().get(currentPage).getIsliked()) {
            Util.customToast(this, getString(R.string.likeremovesucuess));
            ((ImageView) findViewById(R.id.textViewlike))
                    .setImageResource(R.drawable.likes);
        } else {
            like = true;
            Util.customToast(this, getString(R.string.likesucuess));
            ((ImageView) findViewById(R.id.textViewlike))
                    .setImageResource(R.drawable.unlikes);
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

    private void addCouponLike() {
        progressBar.setCancelable(false);
        progressBar.show();
        String url = "deal/like/"
                + appState.getAllDeals().get(currentPage).getId() + "/"
                + appState.getProfile().getId();
        httpConnection.sendRequest(url, null, ServiceListenerType.LIKE_COUPONS,
                SyncHandler, "PUT", null);

    }

    private void removeMerchantBranch(MerchantBranchDto merchantBranchDto) {
        List<MerchantBranchDto> favMerchants = appState.getMerchatBranch();
        favMerchants.remove(merchantBranchDto);
        appState.setMerchatBranch(favMerchants);
        mCustomPagerAdapter.notifyDataSetChanged();
    }

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
