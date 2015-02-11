package com.HungryBells.activity.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/*This method is used to view favorite restaurent*/
public class FavrestListAdapter extends BaseAdapter {

    /*list of favorite restaurent elements*/
    List<MerchantBranchDto> dealsList;

    /*user context*/
    Context context;

    /*Inflater elements*/
    private LayoutInflater mInflater;

    /*Image loader library for image loading*/
    ImageLoader imageLoader;

    /*adapter constructor*/
    public FavrestListAdapter(Context context, List<MerchantBranchDto> dealsList) {
        this.dealsList = dealsList;
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        try {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
        }
    }

    @Override
    public int getCount() {
        return dealsList.size();// total number of elements in the list
    }

    @Override
    public MerchantBranchDto getItem(int position) {
        return dealsList.get(position);// single item in the list
    }

    @Override
    public long getItemId(int position) {
        return position; // index number
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.favrest_adapters, null);
            holder = new ViewHolder();
            holder.logoImage = (ImageView) convertView
                    .findViewById(R.id.imageViewmerchant);//Merchant logo image
            holder.storeName = (TextView) convertView
                    .findViewById(R.id.textMerchant);//Merchant name
            holder.textMerchantBranch = (TextView) convertView
                    .findViewById(R.id.textMerchantBranch);
            holder.ratingBar1=(RatingBar) convertView
                    .findViewById(R.id.ratingBar1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.storeName.setText(dealsList.get(position).getMerchant()
                .getName());
        holder.textMerchantBranch.setText(dealsList.get(position)
                .getBranchName());
        String logoUrl = dealsList.get(position).getMerchant().getLogoUrl();
        imageLoader.displayImage(logoUrl, holder.logoImage);
        if (dealsList.get(position).getRating()!=null)
            holder.ratingBar1.setRating(dealsList.get(position).getRating());
       /* if(dealsList.get(position).get)*/
        return convertView;
    }

    class ViewHolder {
        TextView storeName, textMerchantBranch;
        ImageView logoImage;
        RatingBar ratingBar1;
    }

}
