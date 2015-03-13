package com.HungryBells.activity.adapter;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
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
import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridView;
import com.felipecsl.asymmetricgridview.library.widget.AsymmetricGridViewAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Sample adapter implementation extending from AsymmetricGridViewAdapter<DemoItem>.
// This is the easiest way to get started.
public class DefaultListAdapter extends AsymmetricGridViewAdapter<ContentDealDTO> {

    /*user context*/
    private Context mContext;

    /*Application context */
    private Application mAppContext;

    /*Stores the advertisement*/
    public List<ContentDealDTO> mItems = new ArrayList<ContentDealDTO>();
    Object[] mItemsArray;

    /*background color for layout*/
    String backColor;

    /*text color for text layout*/
    String textColor;


    /*text size used to show*/
    float textSize;

    /*font to show advertisement*/
    String fontName;


    /*Image loader library for image loading*/
    ImageLoader imageLoader;

    /*size of layout */
    int sizeOfLayout;

    /* Gobal application state Object where all contextual information is stored */

    GlobalAppState appState;


    public DefaultListAdapter(final Context context, final AsymmetricGridView listView, final List<ContentDealDTO> items) {
        super(context, listView, items);

        mContext = context;
        mItems = items;
    }

    public DefaultListAdapter(final Context context, final AsymmetricGridView listView, final List<ContentDealDTO> items, Application app,String backColor, String textColor,
                              float textSize, String fontName,int sizeOfLayout,GlobalAppState appState) {
        super(context, listView, items);

        mContext = context;
        mItems = items;
        mItemsArray = mItems.toArray();
        mAppContext = app;
        this.backColor = backColor;
        this.textColor = textColor;
        this.textSize = textSize;
        this.fontName = fontName;
        this.sizeOfLayout = sizeOfLayout;
        imageLoader = ImageLoader.getInstance();
        this.appState = appState;
    }


    @Override
    @SuppressWarnings("deprecation")
    public View getActualView(final int position, final View convertView, final ViewGroup parent) {

        View view = null;
        final ContentDealDTO item = (ContentDealDTO)mItemsArray[position];

		/*String url = item.getUrl();*/
        if (convertView == null) {
            view = View.inflate(mContext, R.layout.cell_advertisement, null);

            Holder holder = new Holder();
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

            Holder holder = new Holder();
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

        }

        Holder holder = (Holder) view.getTag();

        /*
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
                */
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

        int padding_right = 0, padding_left = 0, padding_top = 0, padding_bottom = 0;
        int val = 10;
        if (position == 0) {
            padding_left = val;
            padding_top = val;
            padding_right = val;
            padding_bottom = 0;
        } else {
            if (position % 2 == 0) {

                padding_left = 0;
                padding_top = val;
                padding_right = val;
                padding_bottom = 0;
            } else {

                padding_left = val;
                padding_top = val;
                padding_right = 0;
                padding_bottom = 0;
            }
        }

        holder.advertisementLayout.setPadding(padding_left, padding_top, padding_right, padding_bottom);

        return view;
    }



    /*Holder method */
    class Holder {
        LinearLayout advertisementLayout;
        ImageView imageViewImageWithVideo, imageViewImagesOnly,
                imageViewPlayButton;
        RelativeLayout textLayout, imageLayout;
        TextView textOnlyContent, imageWithText;
    }

    /*used to send details of advertisement viewd to server*/
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