package com.HungryBells.activity.adapter;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.activity.ViewCouponActivity;
import com.HungryBells.activity.ViewDealsActivity;


/* used to show the next item in swiping of viewpager*/
public class CircularViewPagerHandler implements ViewPager.OnPageChangeListener {

    /*Viewpager initialized in an actvity*/
	private ViewPager mViewPager;

    /*current position of the viewpager fragment*/
	private int mCurrentPosition;

    /*scroll state of the viewpager fragment*/
	private int mScrollState;

    /*current uses activity*/
	private UserActivity activity;

    /*adapter constructor*/
	public CircularViewPagerHandler(final ViewPager viewPager,
			UserActivity activity) {
		mViewPager = viewPager;
		this.activity = activity;
	}

    /*called in every swipping*/
	@Override
	public void onPageSelected(final int position) {
		mCurrentPosition = position;
		if (activity.getLocalClassName().contains("ViewDealsActivity")) {
            /*Used to check whether item liked or not*/
			((ViewDealsActivity) activity).checkIsLiked(mCurrentPosition);
		} else if (activity.getLocalClassName().contains("ViewCouponActivity")) {
             /*Used to check whether item liked or not*/
			((ViewCouponActivity) activity).checkIsLiked(mCurrentPosition);
		}else if(activity.getLocalClassName().contains("DealsActivity")){
            ((DealsActivity) activity).pageChanged(mCurrentPosition);
        }else{
             /*Used to check whether item liked or not*/
            ((ViewAdsActivity) activity).pageChanged(mCurrentPosition);
        }
		// urlLoad = position;
		// checkIsLiked(position);
	}
    /*called in every swipping*/
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
   /*
   * checks last item called or not
   * if fragment is last item then it will navigate to first item in the viewpager fragment
   * */
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
