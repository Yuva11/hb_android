package com.HungryBells.activity.adapter;

import java.util.List;

import com.HungryBells.DTO.Deals;
import com.HungryBells.fragments.ViewDealsFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/*Viewpager adapter for Deals*/
public class ViewDealsPagerAdapter extends FragmentStatePagerAdapter {

    /*user context*/
	protected Context mContext;

    /*Deals in the adapter*/
	List<Deals> deals;

    /*constructor method*/
	public ViewDealsPagerAdapter(FragmentManager fm, Context context,
			List<Deals> deals) {
		super(fm);
		mContext = context;
		this.deals = deals;
	}

    /* *
   * Creating fragments using ViewDealsFragment
   *Base adapter override methods*/
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
   /* adapter override method*/
	@Override
	public int getCount() {
		return deals.size();
	}

    /*if any change in the fragment then this method called*/
	@Override
	public int getItemPosition(Object object) {
		ViewDealsFragment f = (ViewDealsFragment) object;
		if (f != null) {
			f.update();
		}
		return super.getItemPosition(object);
	}
}