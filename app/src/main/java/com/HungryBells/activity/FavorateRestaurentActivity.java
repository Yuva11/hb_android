package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.adapter.FavrestListAdapter;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.JsonParsing;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

/**
 *
 *This class is used to show users favorite restaurant
 * */
public class FavorateRestaurentActivity extends UserActivity {

    /*Third party library used to remove item in swiping on that item*/
    DynamicListView merchants;

    /*not used*/
    int value = 0;

    /*adapter to set list elements*/
    FavrestListAdapter favRest;

    /* Global application state Object where all contextual information is stored */
    GlobalAppState appState;

    /*
  * Initiaizing custom toast(UndoBarController)
  *
  * OnCreate Method for Fav restaurant Activity page

  * */
    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favrest);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                easyTracker.send(MapBuilder.createEvent("FAV Details",
                        "FAV Details Page", "FAV", null).build());
            } catch (Exception e) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }
      /*not used*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    /*
      * Inflating the menus in the Action overflow button
      * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favrest, menu);
        return true;
    }
    /*
        * onbackpressed method
         *
         * */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /*calls the favorite restaurant request*/
    @Override
    public void onStart() {
        super.onStart();
        try {
            actionBarCreation(this);
            if (Util.checkNetworkAndLocation(this)) {
                getAllFavRestaurent();
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
    /*This method is used to receive favorite restaurant request to HB server*/
    private void getAllFavRestaurent() {
        try {
            progressBar.setCancelable(false);
            progressBar.show();
            httpConnection = new ServiceListener(appState);
            String url = "customer/restaurants/" + appState.getProfile().getId();
            httpConnection.sendRequest(url, null,
                    ServiceListenerType.FAV_RESTARUNT, SyncHandler, "GET", null);
        }catch (Exception e){

        }

    }
    /*
    * Parsing the JSON favroite restaurant and converts to MerchantBranch POJO class
    *
    * After that it set the list in a listview
    * */
    private void jsonParsingFavRest() {
        List<MerchantBranchDto> merchatBranch = appState.getMerchatBranch();
        merchants = (DynamicListView) findViewById(R.id.listViewfavrest);
        if(merchatBranch.size()>0){
            ((RelativeLayout)findViewById(R.id.nofavorates)).setVisibility(View.GONE);
            merchants.setVisibility(View.VISIBLE);
        }else{
            ((RelativeLayout)findViewById(R.id.nofavorates)).setVisibility(View.VISIBLE);
            merchants.setVisibility(View.GONE);
        }

        favRest = new FavrestListAdapter(this, merchatBranch);
        merchants.setAdapter(favRest);
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(favRest);
        animationAdapter.setAbsListView(merchants);
        merchants.setAdapter(animationAdapter);
        merchants.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(final ViewGroup listView, final int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    customToast("Favourite removed successfully");
                    callDelete(position);
                }
            }
        });

    }


    /*This method is used to create custom toast*/
    private void customToast(String toastMessage) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.textToast);
        text.setText(toastMessage);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

   /*THis method is used to send favorite restaurant remove request to HB server*/
    private void callDelete(int position) {
        String url = "customer/updaterestaurant/"
                + appState.getProfile().getId() + "/"
                + appState.getMerchatBranch().get(position).getId() + "/" + 2;

        httpConnection.sendRequest(url, null, ServiceListenerType.FAV_REST,
                SyncHandler, "POST", null);
        appState.getMerchatBranch().remove(position);
        favRest.notifyDataSetChanged();
        if(favRest.getCount()==0){
            jsonParsingFavRest();
        }

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
                    startActivitiesUser(new Intent(this, DealsActivity.class), this);
                    finish();
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
    /*onPause method*/
    @Override
    protected void onPause() {
        super.onPause();
    }

    /* Concrete method from useractivity used to receive datas to HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FAV_RESTARUNT:
                progressBar.dismiss();
                JsonParsing.favorateRestaurent(message, appState);
                jsonParsingFavRest();
                break;
            case FAV_REST:
                progressBar.dismiss();
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
    /*not used*/
    @Override
    public void onUndo(Parcelable token) {

    }
  /*on resume method*/
    @Override
    protected void onResume() {
        super.onResume();
    }

}
