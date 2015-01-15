package com.HungryBells.fragments;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.DeliveryTypeDto;
import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.activity.R;
import com.HungryBells.activity.ViewDealsActivity;
import com.HungryBells.activity.ZoomImageActivity;
import com.HungryBells.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("SimpleDateFormat")
public class ViewDealsFragment extends Fragment {
	ImageLoader imageLoader;
	Deals deals;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout resource that'll be returned
		rootView = inflater
				.inflate(R.layout.adapter_viewflow, container, false);
		imageLoader = ImageLoader.getInstance();
		Bundle args = getArguments();
		deals = (Deals) args.getSerializable("deals");
		addViews(rootView);

		return rootView;
	}

	public void update() {
		ImageView imageViewfavrest = (ImageView) rootView
				.findViewById(R.id.imageViewfavrest);
		getIsFavorate(deals);
		imageBackground(deals.isFavourite(), imageViewfavrest);
	}

	private void addViews(View rootView) {
		try {
			TextView textViewQuantity, textOldPrice, textNewPrice;
			TextView availabileTime;
			textViewQuantity = (TextView) rootView
					.findViewById(R.id.textViewquantity);
			ImageView imageViewfavrest = (ImageView) rootView
					.findViewById(R.id.imageViewfavrest);
			TextView textViewDescription;
			RelativeLayout buythisdeal = (RelativeLayout) rootView
					.findViewById(R.id.buythisdeal);
			ImageView logoImage = (ImageView) rootView
					.findViewById(R.id.imageViewviewlogo);
			imageLoader.displayImage(deals.getMerchantbranch().getMerchant()
					.getLogoUrl(), logoImage);
			ImageView imageViewitemimg = (ImageView) rootView
					.findViewById(R.id.imageViewitemimg);

			imageLoader.displayImage(deals.getImageURL(), imageViewitemimg);
			TextView findDistance = (TextView) rootView
					.findViewById(R.id.findDistance);
			((TextView) rootView.findViewById(R.id.textViewRestName))
					.setText(deals.getMerchantbranch().getMerchant().getName());
			((TextView) rootView.findViewById(R.id.textViewRestaddress))
					.setText(deals.getMerchantbranch().getBranchName() + "\n"
							+ deals.getMerchantbranch().getAddress());
			ImageView soldOut = (ImageView) rootView
					.findViewById(R.id.imageViewviewsoldout);

            String distance = Util.getDistance(deals.getDistance());
            Log.e("Distances","Distant:"+deals.getDistance()+"::"+distance);
            if(distance.equals("0 m")){
                findDistance.setVisibility(View.INVISIBLE);
            }else
			findDistance.setText(Util.getDistance(deals.getDistance()));
			RelativeLayout fullLayout = (RelativeLayout) rootView
					.findViewById(R.id.viewfullLayoyt);
			if (deals.getAvailability() != null) {
				if (deals.getAvailability() <= 0) {
					soldOut.setVisibility(View.VISIBLE);
					fullLayout.setAlpha(0.5f);
					textViewQuantity.setText("0");
				} else {
					soldOut.setVisibility(View.GONE);
					textViewQuantity.setText(deals.getAvailability() + "");
					imageViewitemimg.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(),
									ZoomImageActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("url", deals.getImageURL());
							intent.putExtras(bundle);
							getActivity().startActivity(intent);

						}
					});
					buythisdeal.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							((ViewDealsActivity) getActivity())
									.buyNowThisDeal(deals);

						}
					});
					imageViewfavrest.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							((ViewDealsActivity) getActivity())
									.addToFavRestaurent(deals);

						}
					});

				}
			} else {
				textViewQuantity.setText(deals.getOpeningQuantity() + "");
			}
			RatingBar ratingBar = (RatingBar) rootView
					.findViewById(R.id.ratingBar1);
			if (deals.getRating() != null) {
				ratingBar.setRating(deals.getRating());
			} else {
				ratingBar.setRating(0.0f);
			}
			TextView dealEndTime = (TextView) rootView
					.findViewById(R.id.dealendtime);
			dealEndTime.setText("Till " + getDate(deals.getEndDate()));
			TextView textViewDiscount = (TextView) rootView
					.findViewById(R.id.textViewdiscount);
			NumberFormat formatters = new DecimalFormat("#0");
			textViewDiscount.setText(formatters.format(deals
					.getDealDiscountPercent()) + "%");

			textOldPrice = (TextView) rootView
					.findViewById(R.id.textViewvieworiginaltext);
			textNewPrice = (TextView) rootView
					.findViewById(R.id.textViewviewnewprice);

			Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
					"Rupee_Foradian.ttf");
			textOldPrice.setTypeface(face);
			textNewPrice.setTypeface(face);
			String amount = "`";
			availabileTime = (TextView) rootView
					.findViewById(R.id.availabiletime);
			availabileTime.setText(getTime(deals.getStartDate()) + " - "
					+ getTime(deals.getEndDate()));
			textOldPrice.setText(amount
					+ formatters.format(deals.getOriginalPrice()));
			textOldPrice.setPaintFlags(textOldPrice.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			textNewPrice.setText(amount
					+ formatters.format(deals.getDealPrice()));
			List<DeliveryTypeDto> deliveryType = deals.getDeliveryType();
			if (deliveryType == null) {
				deliveryType = new ArrayList<DeliveryTypeDto>();
			}
			deliveryAvailable(deliveryType, rootView);
			textViewDescription = (TextView) rootView
					.findViewById(R.id.textViewdiscription);
			textViewDescription.setText(deals.getDetails());
			getIsFavorate(deals);
			imageBackground(deals.isFavourite(), imageViewfavrest);
		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}

	}

	private String getTime(String dateParam) {
		String dateFind = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date date = formatter.parse(dateParam);
			DateFormat dateFormat = new SimpleDateFormat("hh a");
			dateFind = dateFormat.format(date);
		} catch (Exception e) {
			Log.e("Date Conversion", e.toString(), e);
		}
		return dateFind;

	}

	private void deliveryAvailable(List<DeliveryTypeDto> deliveryType,
			View convertView) {
		for (DeliveryTypeDto delivery : deliveryType) {
			if (delivery.getType().equals("DINEIN")) {
				((ImageView) convertView
						.findViewById(R.id.imageViewdinedealavailability))
						.setImageResource(R.drawable.dealavailable);
			}
			if (delivery.getType().equals("PICKUP")) {
				((ImageView) convertView
						.findViewById(R.id.imageViewtakeawaydealavailability))
						.setImageResource(R.drawable.dealavailable);
			}
			if (delivery.getType().equals("HOMEDELIVERY")) {
				((ImageView) convertView
						.findViewById(R.id.imageViewdoortodoordealavailability))
						.setImageResource(R.drawable.dealavailable);
			}
		}

	}

	private String getDate(String dateParam) {
		String dateFind = "";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date date = formatter.parse(dateParam);
			DateFormat dateFormat = new SimpleDateFormat("MMM dd");
			dateFind = dateFormat.format(date);
		} catch (Exception e) {
			Log.e("Date Conversion", e.toString(), e);
		}
		return dateFind;

	}

	private void imageBackground(boolean value, ImageView viewImage) {
		if (value) {
			Bitmap bImage = BitmapFactory.decodeResource(getActivity()
					.getResources(), R.drawable.favstar);
			viewImage.setImageBitmap(bImage);
		} else {
			Bitmap bImage = BitmapFactory.decodeResource(getActivity()
					.getResources(), R.drawable.favunstar);
			viewImage.setImageBitmap(bImage);
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
}