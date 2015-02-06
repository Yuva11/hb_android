package com.HungryBells.activity.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealViewsDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CouponAdapter extends BaseAdapter {

    /*list of coupon elements*/
    List<Deals> dealsList;

    /*user context*/
    Context context;

    /*Inflater elements*/
    private LayoutInflater mInflater;

    /*Image loader library for image loading*/
    ImageLoader imageLoader;

    /* Gobal application state Object where all contextual information is stored */
    GlobalAppState appState;

    /*adapter constructor*/
    public CouponAdapter(Context context, List<Deals> dealsList,GlobalAppState appState) {
        this.dealsList = dealsList;
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        this.appState = appState;
        try {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
        }
    }

    public void setListData(List<Deals> data) {
        dealsList = data;
    }

    @Override
    public int getCount() {
        return dealsList.size();
    }

    @Override
    public Deals getItem(int position) {
        return dealsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.fragment_coupon, null);
            holder = new ViewHolder();
            holder.dealImage = (ImageView) convertView
                    .findViewById(R.id.imagelinearBack);
            holder.logoImage = (ImageView) convertView
                    .findViewById(R.id.logoImage);
            holder.couponName = (TextView) convertView
                    .findViewById(R.id.textViewimgname);
            holder.textViewimgdesc = (TextView) convertView
                    .findViewById(R.id.textViewimgdesc);
            holder.buttoncouponcode = (Button) convertView
                    .findViewById(R.id.buttoncouponcode);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String logoUrl = dealsList.get(position).getMerchantbranch()
                .getMerchant().getLogoUrl();
        imageLoader.displayImage(logoUrl, holder.logoImage);
        String couponImageUrl = dealsList.get(position).getImageURL();
        imageLoader.displayImage(couponImageUrl, holder.dealImage);
        holder.couponName.setText(dealsList.get(position).getMerchantbranch()
                .getMerchant().getName());
        holder.textViewimgdesc.setText(dealsList.get(position).getDetails());
        holder.buttoncouponcode.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sendDelasList(dealsList.get(position));
                ((DealsActivity) context).dealsCouponViewPage(position);

            }
        });
        return convertView;
    }

    class ViewHolder {
        ImageView dealImage, logoImage;
        TextView couponName, textViewimgdesc;
        Button buttoncouponcode;
    }

    // THis method is used to capture which coupon is selected currently
    //It will send data to HB server
    private void sendDelasList(Deals deals){
        Customers customer = appState.getProfile();
        DealViewsDTO deal = new DealViewsDTO();
        Deals myDeal = new Deals();
        myDeal.setId(deals.getId());
        deal.setCustomer(customer);
        deal.setDeal(myDeal);
        String url = context.getString(R.string.serverurl_test)
                + "deal/captureview";
        Ion.with(context).load(url).setJsonPojoBody(deal).asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                    }
                });
    }
}
