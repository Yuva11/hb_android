package com.HungryBells.activity.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.HungryBells.DTO.Deals;
import com.HungryBells.fragments.ViewPromosFragment;

/*Viewpager adapter for coupons*/
public class ViewPromosPagerAdapter extends FragmentStatePagerAdapter {
    /*user context*/
	protected Context mContext;

    /*Deals in the adapter*/
	List<Deals> deals;

   /*constructor method*/
	public ViewPromosPagerAdapter(FragmentManager fm, Context context,
			List<Deals> deals) {
		super(fm);
		mContext = context;
		this.deals = deals;
	}
    /*
    *
    * Creating fragments using ViewPromosFragment
    *Base adapter override methods*/
	@Override
	public Fragment getItem(int position) {

		// Create fragment object
		Fragment fragment = new ViewPromosFragment();
		Bundle args = new Bundle();
		args.putInt("page_position", position + 1);
		args.putSerializable("deals", deals.get(position));
		fragment.setArguments(args);

		return fragment;
	}
    /*adapter override methods*/
	@Override
	public int getCount() {
		return deals.size();
	}

    /*if any change in the fragment then this method called*/
	@Override
	public int getItemPosition(Object object) {
		ViewPromosFragment f = (ViewPromosFragment) object;
		if (f != null) {
			f.update();
		}
		return super.getItemPosition(object);
	}
}