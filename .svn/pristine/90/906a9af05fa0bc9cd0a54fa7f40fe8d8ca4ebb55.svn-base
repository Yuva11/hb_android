package com.HungryBells.fragments;

import java.text.DecimalFormat;
import java.text.NumberFormat;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.R;
import com.HungryBells.activity.ZoomImageActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("SimpleDateFormat")
public class ViewPromosFragment extends Fragment {
	ImageLoader imageLoader;
	Deals deals;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout resource that'll be returned
		rootView = inflater.inflate(R.layout.couponsview, container, false);
		imageLoader = ImageLoader.getInstance();
		Bundle args = getArguments();
		deals = (Deals) args.getSerializable("deals");
		addViews(rootView);

		return rootView;
	}

	public void update() {
	}

	private void addViews(View convertView) {
		try {
			ImageView itemImage = (ImageView) convertView
					.findViewById(R.id.couponMerchant);
			ImageView logoImage = (ImageView) convertView
					.findViewById(R.id.couponLogo);
			TextView merchantName = (TextView) convertView
					.findViewById(R.id.textViewnerchantName);
			TextView textViewcouponname = (TextView) convertView
					.findViewById(R.id.textViewcouponname);
			TextView textViewcoupondetails = (TextView) convertView
					.findViewById(R.id.textViewcoupondetails);
			TextView textViewrs = (TextView) convertView
					.findViewById(R.id.textViewrs);
			TextView textViewcouponcodes = (TextView) convertView
					.findViewById(R.id.textViewcouponcodes);
			TextView textViewDetails = (TextView) convertView
					.findViewById(R.id.textViewDetails);
			TextView textViewusethis = (TextView) convertView
					.findViewById(R.id.textViewusethis);
			TextView textViewamount = (TextView) convertView
					.findViewById(R.id.textViewamount);
			Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
					"Rupee_Foradian.ttf");
			textViewrs.setTypeface(face);
			textViewrs.setText("`");
			NumberFormat formatters = new DecimalFormat("#0");
			if (deals.getOriginalPrice() != null)
				textViewamount.setText(formatters.format(deals
						.getOriginalPrice()));
			textViewusethis.setText("Use This Coupon Code to Get");
			textViewDetails.setText(deals.getDetails());
			textViewcouponcodes.setText(deals.getCouponcode());

			imageLoader.displayImage(deals.getMerchantbranch().getMerchant()
					.getLogoUrl(), logoImage);
			imageLoader.displayImage(deals.getImageURL(), itemImage);
			merchantName.setText(deals
					.getMerchantbranch().getMerchant().getName().toUpperCase());
			textViewcouponname.setText(deals.getName());
			textViewcoupondetails.setText(deals.getDetails());

			itemImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(),
							ZoomImageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("url", deals.getImageURL());
					intent.putExtras(bundle);
					getActivity().startActivity(intent);
					// new ZoomImageDialog(ct, itemUrl).show();
				}
			});
		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}

	}

}