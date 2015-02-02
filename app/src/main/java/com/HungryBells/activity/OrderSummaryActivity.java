package com.HungryBells.activity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.DeliveryTypeDto;
import com.HungryBells.DTO.MerchantDto;
import com.HungryBells.DTO.PaymentDTO;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.activity.subactivity.ChangeBackgroundViewDeal;
import com.HungryBells.dialog.ChangeQuantity;
import com.HungryBells.dialog.DeliveryAddressDialog;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.DeliveryTypes;
import com.HungryBells.util.OrderDetailsDTO;
import com.HungryBells.util.OrderStatusDTO;
import com.HungryBells.util.OrderSummaryDTO;
import com.HungryBells.util.UndoBarController;
import com.HungryBells.util.Util;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.ImageLoader;


/*This method used to get order summary after user select buy now from deals*/
@SuppressLint("SimpleDateFormat")
public class OrderSummaryActivity extends UserActivity implements
        OnClickListener {

    /*selected deal from user*/
    Deals viewDeals;

    /*number of quantity*/
    int quantity = 1;

    /*number converter from double to string*/
    NumberFormat formatters;

    /*background of deal delivery type*/
    ChangeBackgroundViewDeal changeBackground;


    Typeface face;

    /*text of deals amount*/
    TextView dealAmt;

    /*Image loader library for image loading*/
    ImageLoader imageLoader;

    /*Delivery type user currntly selected*/
    DeliveryTypes deliveryTypes;

 /*user current delivery address*/
    public static String deliveryAddress;

    /*Initializing current appstate , background for delivery*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vieworderdetails);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null&&appState.getUrl()!=null) {
            try {
                easyTracker.send(MapBuilder.createEvent("Orders summary",
                        "Orders summary Process", "Orders summary", null).build());
                mUndoBarController = new UndoBarController(
                        findViewById(R.id.undobar), this);
                changeBackground = new ChangeBackgroundViewDeal(this);
                Object dealObj = getIntent().getExtras().getSerializable(
                        "Deals");
                viewDeals = (Deals) dealObj;
                imageLoader = ImageLoader.getInstance();
            } catch (Exception e) {
                Log.e("Error", e.toString(), e);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    /*This method will change data whenever user change delivery type or quantity
    * If button checkout clicked it will send the data to HB server and navigate to payment
    * If Delivery type is  HOME_DELIVERY it will ask address from user
    * */
    private void orderSummary() {
        String url = viewDeals.getImageURL();
        ImageView imageViewItemImage = (ImageView) findViewById(R.id.imageorderdeals);
        imageLoader.displayImage(url, imageViewItemImage);
        dealAmt = (TextView) findViewById(R.id.imageorderdealsAmt);
        ((TextView) findViewById(R.id.imageorderdealsName)).setText(viewDeals
                .getName());
        formatters = new DecimalFormat("#0.00");
        face = Typeface.createFromAsset(getAssets(), "Rupee_Foradian.ttf");
        dealAmt.setTypeface(face);
        dealAmt.setText("`" + formatters.format(viewDeals.getDealPrice()));
        ((TextView) findViewById(R.id.quantitydata)).setText("" + quantity);
        ((TextView) findViewById(R.id.quantitydata))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ChangeQuantity print = new ChangeQuantity(
                                OrderSummaryActivity.this, quantity);
                        print.show();
                    }
                });
        ((Button) findViewById(R.id.buttoncheckout))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String address = "";
                        if(deliveryAddress!=null){
                            address = deliveryAddress;
                        }
                        if (deliveryTypes == DeliveryTypes.HOME_DELIVERY)
                               new DeliveryAddressDialog(OrderSummaryActivity.this,address).show();
                        else{
                            submitChanges("");
                        }
                        //submitChanges();

                    }

                });
        datachanges();
        deliveryAvailable(viewDeals.getDeliveryType());
    }
     /*This method is used to send payment attemt details to HB server*/
    public void submitChanges(String deliveryAddress) {
        OrderSummaryDTO summary = new OrderSummaryDTO();
        Customers customer = new Customers();
        customer.setId(appState.getProfile().getId());
        StringEntity custEntity = null;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Double price = quantity * viewDeals.getDealPrice();
        OrderDetailsDTO orders = new OrderDetailsDTO();
        orders.setCost(price);
        orders.setQuantity(quantity);
        orders.setDeliveryAddress(deliveryAddress);
        orders.setDeal(viewDeals);
        if(deliveryTypes == DeliveryTypes.HOME_DELIVERY){
            orders.setDeliveryType("HOME_DELIVERY");
        }else if(deliveryTypes == DeliveryTypes.DINEIN){
            orders.setDeliveryType("DINEIN");
        }else{
            orders.setDeliveryType("PICKUP");
        }
        OrderStatusDTO orderStatus = new OrderStatusDTO();
        List<OrderDetailsDTO> statuses = new ArrayList<OrderDetailsDTO>();
        statuses.add(orders);
        orderStatus.setOrderDetails(statuses);
        MerchantDto merchant = new MerchantDto();
        merchant.setId(viewDeals.getMerchantbranch().getMerchant().getId());
        orderStatus.setMerchant(merchant);
        orderStatus.setDeliveryStatus(false);
        orderStatus.setCustomer(customer);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        orderStatus.setDealordersdate(df.format(today));
        orderStatus.setStatus("LOCKED");
        orderStatus.setAmount((quantity * viewDeals.getDealPrice()));
        summary.setCustomer(customer);
        summary.setDOrders(orderStatus);
        try {
            String customerData = gson.toJson(summary);
            custEntity = new StringEntity(customerData, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        progressBar.setCancelable(false);
        progressBar.show();
        httpConnection = new ServiceListener(appState);
        String url = "payment/addpaymentattempt/"
                + appState.getProfile().getId();
        httpConnection.sendRequest(url, null, ServiceListenerType.SUBMITDATA,
                SyncHandler, "POST", custEntity);

    }

    /*This method will get the available delivery for deal and set background for that delivery*/
    private void deliveryAvailable(List<DeliveryTypeDto> deliveryType) {
        boolean valueForBackGorund = true;
        for (DeliveryTypeDto delivery : deliveryType) {
            if (delivery.getType().equals("DINEIN")) {
                ((LinearLayout) findViewById(R.id.layoutdineinviewdeal))
                        .setAlpha(1f);
                ((LinearLayout) findViewById(R.id.layoutdineinviewdeal))
                        .setOnClickListener(this);
                changeBackground.dineinBackground();
                valueForBackGorund = false;
                deliveryTypes = DeliveryTypes.DINEIN;
            }
            if (delivery.getType().equals("PICKUP")) {
                ((LinearLayout) findViewById(R.id.layouttakeawayviewdeal))
                        .setAlpha(1f);
                ((LinearLayout) findViewById(R.id.layouttakeawayviewdeal))
                        .setOnClickListener(this);
                if (valueForBackGorund) {
                    changeBackground.takeAwayBackground();
                    valueForBackGorund = false;
                    deliveryTypes = DeliveryTypes.PICKUP;
                }
            }
            if (delivery.getType().equals("HOMEDELIVERY")) {
                ((LinearLayout) findViewById(R.id.layoutdoorviewdeal))
                        .setAlpha(1f);
                ((LinearLayout) findViewById(R.id.layoutdoorviewdeal))
                        .setOnClickListener(this);
                if (valueForBackGorund) {
                    changeBackground.doortoDoorBackground();
                    valueForBackGorund = false;
                    deliveryTypes = DeliveryTypes.HOME_DELIVERY;
                }

            }
        }
        datachanges();
    }

    /*Checking network connection*/
    @Override
    public void onStart() {
        try {
            super.onStart();
            actionBarCreation(this);
            if (Util.checkNetworkAndLocation(this)) {
                orderSummary();
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
    protected void onResume() {
        super.onResume();
    }

    /*This method used to change quantity of current item*/
    public void changeQuantity(int quantity) {
        this.quantity = quantity;
        ((TextView) findViewById(R.id.quantitydata)).setText("" + quantity);
        dealAmt.setText("`"
                + formatters.format(quantity * viewDeals.getDealPrice()));
        datachanges();
    }
   /*Whenever quantity changed the details will be changed*/
    private void datachanges() {
        TextView totalvalue = (TextView) findViewById(R.id.totalvalue);
        TextView totalOrder = (TextView) findViewById(R.id.totalOrder);
        totalOrder.setTypeface(face);
        String amount = "`";
        totalOrder.setText(amount);
        Double price = quantity * viewDeals.getDealPrice();
        totalvalue.setText(formatters.format(price));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                startActivity(new Intent(this, DealsActivity.class));
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

    /*
    * Inflating the menus in the Action overflow button
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /*onclick method for delivery type*/
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutdineinviewdeal:
                changeBackground.dineinBackground();
                deliveryTypes = DeliveryTypes.DINEIN;
                datachanges();
                break;
            case R.id.layoutdoorviewdeal:
                changeBackground.doortoDoorBackground();
                deliveryTypes = DeliveryTypes.HOME_DELIVERY;
                datachanges();
                break;
            case R.id.layouttakeawayviewdeal:
                changeBackground.takeAwayBackground();
                deliveryTypes = DeliveryTypes.PICKUP;
                datachanges();
                break;
        }

    }

    /* Concrete method from useractivity used to receive datas to HB Server*/
    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {
        jsonParsingUrl(message);
        progressBar.dismiss();

    }

    /*Parsing the data from HB server and check availabilty
    If available Then navigate to payment else show error message*/
    private void jsonParsingUrl(Bundle data) {
        try {
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Log.e("Availables:", response);
            response = "{\"paymentDTO\":" + response + "}";
            Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
            PaymentDTO payment;

            System.out.println(datas.paymentDTO);
            payment = datas.paymentDTO;
            if (payment.getAvailability() >= quantity) {
                Intent intent = new Intent(this, PaymentActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("Deals", viewDeals);
                bundle.putSerializable("payment", payment);
                intent.putExtras(bundle);
                startActivity(intent);
            } else if (payment.getAvailability() > 0) {
                Util.customToast(this,
                        getString(R.string.avails) + payment.getAvailability());
            } else {
                Util.customToast(this, getString(R.string.soldout));
            }
        } catch (Exception e) {
            Log.e("Json Parsing", e.toString(), e);
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
}
