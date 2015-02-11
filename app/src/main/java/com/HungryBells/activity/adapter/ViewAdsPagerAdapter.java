package com.HungryBells.activity.adapter;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.fragments.ViewAdsFragment;


/*Viewpager adapter for Advertisement*/
public class ViewAdsPagerAdapter extends FragmentStatePagerAdapter {

    /*user context*/
	protected Context mContext;

    /*Advertisement in the adapter*/
	List<ContentDealDTO> contents;

    /*constructor method*/
	public ViewAdsPagerAdapter(FragmentManager fm, Context context,
			List<ContentDealDTO> contents) {
		super(fm);
		mContext = context;
		this.contents = contents;
	}


    /* *
   * Creating fragments using ViewAdsFragment
   *Base adapter override methods*/
	@Override
	public Fragment getItem(int position) {

		// Create fragment object
		Fragment fragment = new ViewAdsFragment();
		Bundle args = new Bundle();
		args.putInt("page_position", position + 1);
		args.putSerializable("ContentDealDTO", contents.get(position));
		fragment.setArguments(args);

		return fragment;
	}
    /* adapter override method*/
	@Override
	public int getCount() {
		return contents.size();
	}

    /*if any change in the fragment then this method called*/
	@Override
	public int getItemPosition(Object object) {
		ViewAdsFragment f = (ViewAdsFragment) object;
        Log.e("f.isFrontPage()","f.isFrontPage():"+f.isFrontPage());
		if (f != null) {
			f.update();
            ((ViewAdsActivity)mContext).pageScrollAds(f.isFrontPage());
		}
		return super.getItemPosition(object);
	}
}