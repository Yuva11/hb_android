package com.HungryBells.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.AdViewsDTO;
import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.ContentTemplate;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealViewsDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AdvertisementAdapter extends BaseAdapter {
	private Context mContext;
	private Application mAppContext;
	private List<ContentDealDTO> mItems = new ArrayList<ContentDealDTO>();
	String backColor;
	String textColor;
	float textSize;
	String fontName;
	ImageLoader imageLoader;
    int sizeOfLayout;
    GlobalAppState appState;
	public AdvertisementAdapter(Context context, Application app,
			List<ContentDealDTO> mItems2, String backColor, String textColor,
			float textSize, String fontName,int sizeOfLayout,GlobalAppState appState) {
		mContext = context;
		mAppContext = app;
		mItems = mItems2;
		this.backColor = backColor;
		this.textColor = textColor;
		this.textSize = textSize;
		this.fontName = fontName;
        this.sizeOfLayout = sizeOfLayout;
		imageLoader = ImageLoader.getInstance();
        this.appState = appState;
	}

	@Override
	public int getCount() {
		return mItems == null ? 0 : mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		final ContentDealDTO item = mItems.get(position);

		/*String url = item.getUrl();*/
		if (convertView == null) {
			Holder holder = new Holder();
			view = View.inflate(mContext, R.layout.cell_advertisement, null);
			holder.advertisementLayout = (LinearLayout) view
					.findViewById(R.id.advertisementLayout);
			holder.textLayout = (RelativeLayout) view
					.findViewById(R.id.textLayout);
			holder.imageLayout = (RelativeLayout) view
					.findViewById(R.id.imageLayout);
			holder.textOnlyContent = (TextView) view
					.findViewById(R.id.textOnlyContent);
			holder.imageViewImageWithVideo = (ImageView) view
					.findViewById(R.id.imageViewImageWithVideo);
			holder.imageViewImagesOnly = (ImageView) view
					.findViewById(R.id.imageViewImagesOnly);
			holder.imageViewPlayButton = (ImageView) view
					.findViewById(R.id.imageViewPlayButton);
			holder.imageWithText = (TextView) view
					.findViewById(R.id.imageWithText);
			view.setTag(holder);
		} else {
			view = convertView;
		}

		Holder holder = (Holder) view.getTag();
		holder.advertisementLayout
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (item.getId() != null)
                            sendDelasList(item);
							((DealsActivity) mContext)
									.dealsAdsViewPage(position);
					}
				});
		if (item.getContentTemplate() == ContentTemplate.TEXT_ONLY) {
			holder.imageLayout.setVisibility(View.GONE);
			holder.textLayout.setVisibility(View.VISIBLE);
			try {
				holder.textLayout.setBackgroundColor(Color
						.parseColor(backColor));
			} catch (Exception e) {
				holder.textLayout.setBackgroundColor(Color
						.parseColor("#ffffff"));
			}

			if (fontName.equalsIgnoreCase("MONOSPACE")) {
				holder.textOnlyContent.setTypeface(Typeface.MONOSPACE);
			} else if (fontName.equalsIgnoreCase("SANS_SERIF")) {
				holder.textOnlyContent.setTypeface(Typeface.SANS_SERIF);
			} else if (fontName.equalsIgnoreCase("SERIF")) {
				holder.textOnlyContent.setTypeface(Typeface.SERIF);
			} else if (fontName.equalsIgnoreCase("Pristina")) {
				Typeface face = Typeface.createFromAsset(
						mAppContext.getAssets(), "Pristina.ttf");
				holder.textOnlyContent.setTypeface(face);
			} else {
				holder.textOnlyContent.setTypeface(Typeface.DEFAULT);
			}
			holder.textOnlyContent.setTextColor(Color.parseColor(textColor));
			holder.textOnlyContent.setTextSize(textSize);
			holder.textOnlyContent.setText(item.getThumbnailText());
		} else {
			holder.imageLayout.setVisibility(View.VISIBLE);
			holder.textLayout.setVisibility(View.GONE);
			if (item.getContentTemplate() == ContentTemplate.IMAGE_ONLY
					|| item.getContentTemplate() == ContentTemplate.IMAGE_AND_TEXT) {
				holder.imageViewPlayButton.setVisibility(View.GONE);
			}
			if (item.getContentTemplate() == ContentTemplate.IMAGE_ONLY) {
				holder.imageWithText.setVisibility(View.GONE);
			} else {
				holder.imageWithText.setText(item.getThumbnailText());
			}
			Log.d("Inside Adapter", item.getThumbNailURL());
			imageLoader.displayImage(item.getThumbNailURL(), holder.imageViewImageWithVideo);
		}
		LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, sizeOfLayout);
		LinearLayout topLayout = new LinearLayout(mContext);
		holder.advertisementLayout.addView(topLayout, rlp);
		return view;
	}

	class Holder {
		LinearLayout advertisementLayout;
		ImageView imageViewImageWithVideo, imageViewImagesOnly,
				imageViewPlayButton;
		RelativeLayout textLayout, imageLayout;
		TextView textOnlyContent, imageWithText;
	}
    private void sendDelasList(ContentDealDTO deals){
        Customers customer = appState.getProfile();
        AdViewsDTO deal = new AdViewsDTO();
        ContentDealDTO myDeal = new ContentDealDTO();
        myDeal.setId(deals.getId());
        deal.setCustomer(customer);
        deal.setAdvertisement(myDeal);
        String url = mContext.getString(R.string.serverurl_test)
                + "content/captureview";
        Ion.with(mContext).load(url).setJsonPojoBody(deal).asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                    }
                });
    }
}
