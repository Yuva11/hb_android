package com.HungryBells.activity;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.HungryBells.DTO.DealSummaryDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.dialog.RatingsDialog;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.OrderDetailsDTO;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")

/*This class is used to show the past orders from user*/
public class MyOrdersActivity extends UserActivity {

    /*list of elements in Deal summary*/
    List<DealSummaryDto> orders;


   /* Initializing app state
    initializing mUndoBarController*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                easyTracker.send(MapBuilder.createEvent("MyOrdersActivity",
                        "MyOrders Process", "MyOrders", null).build());
                String callUs = "<font color=#454545>Call us:</font> <font color=#4e6412>7 days a week</font>";
                ((TextView) findViewById(R.id.textViewcallus)).setText(Html
                        .fromHtml(callUs));
                ((TextView) findViewById(R.id.textViewphone))
                        .setText("+918088002288");
                ((TextView) findViewById(R.id.textViewTime))
                        .setText("9 A.M - 6 P.M");
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
    public void onStart() {
        try {
            super.onStart();
            actionBarCreation(this);
            if (Util.checkNetworkAndLocation(this)) {
                getMyOrders();
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


    /*This method is used to receive last 5 orders from HB server*/
    private void getMyOrders() {
        progressBar.setCancelable(false);
        progressBar.show();
        httpConnection = new ServiceListener(appState);
        if (appState.getProfile() != null) {
            String url = "payment/attemptbycustomer/"
                    + appState.getProfile().getId();
            httpConnection.sendRequest(url, null,
                    ServiceListenerType.FEEDBACK_INSERT, SyncHandler, "GET",
                    null);
        }
    }

    /*This method is used to parse JSON response from HB server and convert to list of orders*/
    private void jsonParsingMyOrders(Bundle data) {
        String response = data.getString(ServiceListener.RESPONSEDATA);
        Log.e("Json Resp:",response);
        response = "{\"orderSummary\":"
                + response.substring(0, response.length() - 1) + "}";
        response = response.replace("\n", "");
        Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
        try {
            orders = datas.orderSummary;
        } catch (Exception e) {
            Log.e("Json Parsing", e.toString(), e);
        }
        showSummary();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*This method is used to show summary of past orders from HB server
    * Check for order size if order size is zero then show message no orders and button to make new order
    *
    *
    * Else there load the orders in layout
    *
    * if order already rated then show rating else there will be abutton to rate
    *
    * */
    private void showSummary() {
        ImageLoader images = ImageLoader.getInstance();
        LinearLayout itemsSummary = (LinearLayout) findViewById(R.id.myIdValue);
        itemsSummary.removeAllViews();
        if(orders.size()>0){
            ((RelativeLayout)findViewById(R.id.noorders)).setVisibility(View.GONE);
            ((ScrollView)findViewById(R.id.scrollMyOrder)).setVisibility(View.VISIBLE);
            for (int i = 0; i < orders.size(); i++) {
                DealSummaryDto dealSummary = orders.get(i);
                Log.e("Deals Summary", dealSummary.toString());
                LayoutInflater lin = LayoutInflater.from(this);
                View convertView = lin.inflate(R.layout.orders_summarys, null);
                ((TextView) convertView.findViewById(R.id.timeOfDelivery))
                        .setText(getDate(dealSummary.getPaymentdate()));
                String orderNos = dealSummary.getDOrders().getId() + "";
                if(dealSummary.getDOrders().getOrderId()!=null){
                    orderNos = dealSummary.getDOrders().getOrderId();
                }
                ((TextView) convertView.findViewById(R.id.orderRefNo)).setText(orderNos);
                ((TextView) convertView.findViewById(R.id.orderedItems)).setText("");
                try {
                    if( dealSummary.getDOrders().getOrderDetails().get(0).getDeal().getName()!=null){
                        ((TextView) convertView.findViewById(R.id.orderedItems)).setText(dealSummary.getDOrders().getOrderDetails().get(0).getDeal().getName());
                    }
                }catch(Exception e){
                         Log.e("Error",e.toString(),e);
                }


                TextView textAmt = (TextView) convertView
                        .findViewById(R.id.orderAmt);
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "Rupee_Foradian.ttf");
                textAmt.setTypeface(face);
                textAmt.setText("`");
                NumberFormat formatters = new DecimalFormat("#0.00");
                ((TextView) convertView.findViewById(R.id.orderTotalAmt)).setText(formatters.format(dealSummary.getDOrders().getAmount()));

                TextView deliveryStatus = (TextView) convertView
                        .findViewById(R.id.deliveryStatus);
                RatingBar ratings = (RatingBar) convertView.findViewById(R.id.ratingBarUser1);
                final LinearLayout ratebars = (LinearLayout) convertView.findViewById(R.id.ratebars);
                LinearLayout ratedbars = (LinearLayout) convertView.findViewById(R.id.ratedbars);
                if(i==orders.size()-1){
                    ((View)convertView.findViewById(R.id.viewLines)).setVisibility(View.GONE);
                }
                if (dealSummary.getDOrders().getOrderDetails().get(0).getRating() == null) {
                    ratebars.setVisibility(View.VISIBLE);
                    ratedbars.setVisibility(View.GONE);
                    TextView animbar = (TextView) convertView.findViewById(R.id.animbar);
               /* ValueAnimator colorAnimator = (ValueAnimator) AnimatorInflater.loadAnimator(this, R.animator.textanim);
                colorAnimator.setTarget(ratebars);
                colorAnimator.setEvaluator(new ArgbEvaluator());
                colorAnimator.start();*/
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500); //You can manage the blinking time with this parameter
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
                    animbar.startAnimation(anim);
                    ratebars.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("Distances", "Distant: MyOrdersActivity");
                            new RatingsDialog(MyOrdersActivity.this, orders.get(ratebars.getId() - 100)).show();
                        }
                    });
                }else{
                    ratedbars.setVisibility(View.VISIBLE);
                    ratebars.setVisibility(View.GONE);
                    ratings.setRating((float)dealSummary.getDOrders().getOrderDetails().get(0).getRating());
                }
                ratebars.setId(100+i);
                if (!dealSummary.getDOrders().isDeliveryStatus()) {
                    deliveryStatus.setText("Booked");
                    deliveryStatus.setTextColor(Color.parseColor("#c81612"));
                } else {
                    deliveryStatus.setText("Delivered");
                    deliveryStatus.setTextColor(Color.parseColor("#6a930f"));
                }
                LinearLayout categoryslayout = (LinearLayout) convertView
                        .findViewById(R.id.orderslayoutSummary);
                categoryslayout.setId(i);
                ImageView img = (ImageView) convertView
                        .findViewById(R.id.imageViewMetchant);
                itemsSummary.addView(convertView);
                images.displayImage(dealSummary.getDOrders().getMerchant()
                        .getLogoUrl(), img);
            }
        }else{
            Log.e("On Click","Clicked");
            ((RelativeLayout)findViewById(R.id.noorders)).setVisibility(View.VISIBLE);
            ((ScrollView)findViewById(R.id.scrollMyOrder)).setVisibility(View.GONE);

        }
    }

    /*This method is used to navigate to deals page if there are no past orders and user select make order*/
    public void clickonOrders(View v){
        Log.e("On Click","Clicked");
        appState.setSelectedItem(1);
        startActivity(new Intent(this,DealsActivity.class));
    }

    /*This method is used to send rating to the HB server*/
    public void getDeals(OrderDetailsDTO dealsRatings){
        String url = getString(R.string.serverurl_test)
                + "deal/rating";
        Ion.with(this).load(url).setJsonPojoBody(dealsRatings).asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        Log.e("Result",result);
                        getMyOrders();
                    }
                });
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
     * Inflating the menus in the Action overflow button
     * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myorders, menu);
        return true;
    }

   /* This method is used to convert date format into MMM dd, yyyy hh:mm a*/
    @SuppressLint("SimpleDateFormat")
    private String getDate(String deliveryDate) {
        Log.e("Date", deliveryDate);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = format.parse(deliveryDate);
            System.out.println(date);
            SimpleDateFormat postFormater = new SimpleDateFormat(
                    "MMM dd, yyyy hh:mm a");
            deliveryDate = postFormater.format(date);
        } catch (ParseException e) {
            Log.e("Error", e.toString(), e);
        }
        return deliveryDate;
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
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    /* Concrete method from useractivity used to receive datas to HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        switch (what) {
            case FEEDBACK_INSERT:
                progressBar.dismiss();
                jsonParsingMyOrders(message);
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
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onUndo(Parcelable token) {

    }
}
