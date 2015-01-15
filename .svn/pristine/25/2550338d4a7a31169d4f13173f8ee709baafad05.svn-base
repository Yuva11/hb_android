package com.HungryBells.activity.subactivity;

import android.app.Activity;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.HungryBells.activity.R;

public class ChangeBackgroundViewDeal {
	Activity activity;

	public ChangeBackgroundViewDeal(Activity con) {
		activity = con;
	}

	public void dineinBackground() {
		TextView dineIn = (TextView) activity
				.findViewById(R.id.textdineinviewdeal);
		TextView takeaway = (TextView) activity
				.findViewById(R.id.texttakeawayviewdeal);
		TextView delivery = (TextView) activity
				.findViewById(R.id.textdoorviewdeal);
		((LinearLayout) activity.findViewById(R.id.layoutdineinviewdeal))
				.setBackgroundColor(Color.parseColor("#8a8a8a"));
		((LinearLayout) activity.findViewById(R.id.layoutdoorviewdeal))
				.setBackgroundColor(Color.parseColor("#00000000"));
		((LinearLayout) activity.findViewById(R.id.layouttakeawayviewdeal))
				.setBackgroundColor(Color.parseColor("#00000000"));

		dineIn.setTextColor(Color.parseColor("#ffffff"));
		dineIn.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.selected_dinein, 0, 0);
		takeaway.setTextColor(Color.parseColor("#8a8a8a"));
		takeaway.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_deleviery, 0, 0);
		delivery.setTextColor(Color.parseColor("#393939"));
		delivery.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_doordelivery, 0, 0);
	}

	public void doortoDoorBackground() {
		TextView dineIn = (TextView) activity
				.findViewById(R.id.textdineinviewdeal);
		TextView takeaway = (TextView) activity
				.findViewById(R.id.texttakeawayviewdeal);
		TextView delivery = (TextView) activity
				.findViewById(R.id.textdoorviewdeal);
		((LinearLayout) activity.findViewById(R.id.layoutdoorviewdeal))
				.setBackgroundColor(Color.parseColor("#8a8a8a"));
		((LinearLayout) activity.findViewById(R.id.layoutdineinviewdeal))
				.setBackgroundColor(Color.parseColor("#00000000"));
		((LinearLayout) activity.findViewById(R.id.layouttakeawayviewdeal))
				.setBackgroundColor(Color.parseColor("#00000000"));

		dineIn.setTextColor(Color.parseColor("#393939"));
		dineIn.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_dinein, 0, 0);
		takeaway.setTextColor(Color.parseColor("#393939"));
		takeaway.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_deleviery, 0, 0);
		delivery.setTextColor(Color.parseColor("#ffffff"));
		delivery.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.selected_doordelivery, 0, 0);

	}

	public void takeAwayBackground() {
		TextView dineIn = (TextView) activity
				.findViewById(R.id.textdineinviewdeal);
		TextView takeaway = (TextView) activity
				.findViewById(R.id.texttakeawayviewdeal);
		TextView delivery = (TextView) activity
				.findViewById(R.id.textdoorviewdeal);
		((LinearLayout) activity.findViewById(R.id.layouttakeawayviewdeal))
				.setBackgroundColor(Color.parseColor("#8a8a8a"));
		((LinearLayout) activity.findViewById(R.id.layoutdineinviewdeal))
				.setBackgroundColor(Color.parseColor("#00000000"));
		((LinearLayout) activity.findViewById(R.id.layoutdoorviewdeal))
				.setBackgroundResource(R.drawable.selected_delivery);

		dineIn.setTextColor(Color.parseColor("#393939"));
		dineIn.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_dinein, 0, 0);
		takeaway.setTextColor(Color.parseColor("#ffffff"));
		takeaway.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.selected_delivery, 0, 0);
		delivery.setTextColor(Color.parseColor("#393939"));
		delivery.setCompoundDrawablesWithIntrinsicBounds(0,
				R.drawable.unselected_doordelivery, 0, 0);

	}
}
