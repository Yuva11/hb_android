package com.HungryBells.activity.adapter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.DealTemplateDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class DealsPageAdapter extends BaseAdapter {
	List<Deals> list;
	private LayoutInflater mInflater;
	Activity activity;
	ImageLoader imageLoader;
	GlobalAppState appState;

	public DealsPageAdapter(Activity _MyContext, List<Deals> _list,
			GlobalAppState appState) {
		activity = _MyContext;
		mInflater = LayoutInflater.from(_MyContext);
		list = _list;
		this.appState = appState;
		imageLoader = ImageLoader.getInstance();
	}

	public void setListData(List<Deals> data) {
		list = data;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return (list.get(position).getDealTemplate() == DealTemplateDTO.FULL_IMAGE) ? 1
				: 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		int theType = getItemViewType(position);
		if (convertView == null) {
			if (theType == 1) {
                //convertView = mInflater.inflate(R.layout.fragment_imagelayout,null);
                convertView = mInflater.inflate(R.layout.fragment_bestpick_list_item ,null);
            }
			else {
                convertView = mInflater.inflate(R.layout.fragment_dealwithpercentage, null);
            }

			holder = new ViewHolder();
			holder.imageDealId = (RelativeLayout) convertView
					.findViewById(R.id.imagedealback);

			holder.imageLayout = (ImageView) convertView
					.findViewById(R.id.imagelinearBack);

			holder.textName = (TextView) convertView
					.findViewById(R.id.textViewimgname);

			//holder.textdesc = (TextView) convertView
			//		.findViewById(R.id.textViewimgdesc);

			holder.textoldPrice = (TextView) convertView
					.findViewById(R.id.textViewamt);

			holder.textNewPrice = (TextView) convertView
					.findViewById(R.id.textViewnewamt);

			holder.textViewvaluedis = (TextView) convertView
					.findViewById(R.id.textViewvaluedis);

			//holder.textDiscount = (TextView) convertView
			//		.findViewById(R.id.textViewdiscount);

			holder.logoImg = (ImageView) convertView
					.findViewById(R.id.logoImage);

			holder.soldout = (ImageView) convertView
					.findViewById(R.id.imageViewsoldout);

			//holder.linear = (LinearLayout) convertView
			//		.findViewById(R.id.linearview);

			//holder.ratings = (RatingBar) convertView
			//		.findViewById(R.id.ratingBar1);


            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Deals deal = list.get(position);
		String url = deal.getImageURL();
		imageLoader.displayImage(url, holder.imageLayout);
		holder.imageLayout.setMaxHeight(20);
		holder.textName.setText(deal.getName());

       // holder.textdesc.setText(deal.getDetails());

        holder.textViewvaluedis.setText(Util.getDistance(deal.getDistance()));
		String value = "";
		NumberFormat formatters = new DecimalFormat("#0");
		if (deal.getOriginalPrice() != null)
			value = "`" + formatters.format(deal.getOriginalPrice());
		Typeface face = Typeface.createFromAsset(activity.getAssets(),
				"Rupee_Foradian.ttf");
		holder.textoldPrice.setTypeface(face);
		holder.textoldPrice.setText(value);
		holder.textoldPrice.setPaintFlags(holder.textoldPrice.getPaintFlags()
				| Paint.STRIKE_THRU_TEXT_FLAG);

		holder.textNewPrice.setTypeface(face);
		if (theType == 1) {
			holder.textNewPrice.setText("`");
			((TextView) convertView.findViewById(R.id.textViewnewamtValue))
					.setText(formatters.format(deal.getDealPrice()));
		} else {
			holder.textNewPrice.setText("`"
					+ formatters.format(deal.getDealPrice()));
		}

        /*
		if (deal.getRating() != null) {
			holder.ratings.setRating(deal.getRating());
		} else {
			holder.ratings.setRating(0f);
		}
		*/

       /*
       if(deal.getDealDiscountPercent()!=null) {
           holder.textDiscount.setText(formatters.format(deal
                   .getDealDiscountPercent()) + "%");
       }
       */


		String merchantUrl = deal.getMerchantbranch().getMerchant()
				.getLogoUrl();
		imageLoader.displayImage(merchantUrl, holder.logoImg);
		if (deal.getAvailability() != null && deal.getAvailability() == 0) {
			holder.soldout.setVisibility(View.VISIBLE);

            //holder.linear.setAlpha(0.5f);
			if (theType == 1) {
				holder.imageLayout.setAlpha(0.5f);
			}
		} else {
			holder.soldout.setVisibility(View.GONE);

            //holder.linear.setAlpha(1.0f);
			if (theType == 1) {
				holder.imageLayout.setAlpha(1.0f);
			}
		}

		return convertView;
	}

	@Override
	public Deals getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	class ViewHolder {
		RelativeLayout imageDealId;
		ImageView imageLayout;
		TextView textName;

		//TextView textdesc;

		TextView textoldPrice;
		TextView textNewPrice, textViewnewamtValue;
		TextView textViewvaluedis;

        //TextView textDiscount;

        ImageView logoImg;
		ImageView soldout;

        //LinearLayout linear;
		//RatingBar ratings;
	}

}
