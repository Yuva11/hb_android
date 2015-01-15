package com.HungryBells.activity.adapter;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.activity.ViewCouponActivity;
import com.HungryBells.activity.ViewDealsActivity;

public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {
	private ViewPager mViewPager;
	private int mCurrentPosition;
	private int mScrollState;
	private UserActivity activity;

	public CircularViewPagerHandler(final ViewPager viewPager,
			UserActivity activity) {
		mViewPager = viewPager;
		this.activity = activity;
	}

	@Override
	public void onPageSelected(final int position) {
		mCurrentPosition = position;
		if (activity.getLocalClassName().contains("ViewDealsActivity")) {
			((ViewDealsActivity) activity).checkIsLiked(mCurrentPosition);
		} else if (activity.getLocalClassName().contains("ViewCouponActivity")) {
			((ViewCouponActivity) activity).checkIsLiked(mCurrentPosition);
		}else if(activity.getLocalClassName().contains("DealsActivity")){
            ((DealsActivity) activity).pageChanged(mCurrentPosition);
        }else{
            ((ViewAdsActivity) activity).pageChanged(mCurrentPosition);
        }
		// urlLoad = position;
		// checkIsLiked(position);
	}

	@Override
	public void onPageScrollStateChanged(final int state) {
		handleScrollState(state);
		mScrollState = state;
	}

	private void handleScrollState(final int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			setNextItemIfNeeded();
		}
	}

	private void setNextItemIfNeeded() {
		if (!isScrollStateSettling()) {
			handleSetNextItem();
		}
	}

	private boolean isScrollStateSettling() {
		return mScrollState == ViewPager.SCROLL_STATE_SETTLING;
	}

	private void handleSetNextItem() {
		final int lastPosition = mViewPager.getAdapter().getCount() - 1;

        if (mCurrentPosition == lastPosition) {
            Log.e("Position","Last Position:"+mCurrentPosition+":"+lastPosition);
			mViewPager.setCurrentItem(0, false);
            if (activity.getLocalClassName().contains("ViewDealsActivity")) {
                ((ViewDealsActivity) activity).viewDealDetails();
            } else if (activity.getLocalClassName().contains("ViewCouponActivity")) {
                ((ViewCouponActivity) activity).couponsView();
            }else if(activity.getLocalClassName().contains("DealsActivity")){

            }else{
                ((ViewAdsActivity) activity).setViewPagerAdapter();
            }
		}
	}

	@Override
	public void onPageScrolled(final int position, final float positionOffset,
			final int positionOffsetPixels) {
	}
}
