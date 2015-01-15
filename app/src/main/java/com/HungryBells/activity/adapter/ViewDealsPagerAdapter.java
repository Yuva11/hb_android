package com.HungryBells.activity.adapter;

import java.util.List;

import com.HungryBells.DTO.Deals;
import com.HungryBells.fragments.ViewDealsFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewDealsPagerAdapter extends FragmentStatePagerAdapter {

	protected Context mContext;
	List<Deals> deals;

	public ViewDealsPagerAdapter(FragmentManager fm, Context context,
			List<Deals> deals) {
		super(fm);
		mContext = context;
		this.deals = deals;
	}

	@Override
	public Fragment getItem(int position) {

		// Create fragment object
		Fragment fragment = new ViewDealsFragment();
		Bundle args = new Bundle();
		args.putInt("page_position", position + 1);
		args.putSerializable("deals", deals.get(position));
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public int getCount() {
		return deals.size();
	}

	@Override
	public int getItemPosition(Object object) {
		ViewDealsFragment f = (ViewDealsFragment) object;
		if (f != null) {
			f.update();
		}
		return super.getItemPosition(object);
	}
}