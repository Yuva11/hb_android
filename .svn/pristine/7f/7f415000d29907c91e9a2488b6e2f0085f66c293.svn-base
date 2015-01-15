package com.HungryBells.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.activity.SearchCityActivity;
import com.HungryBells.activity.adapter.AdvertisementAdapter;
import com.HungryBells.activity.subactivity.NewDealsProms;

public class WhatsNewFragment extends Fragment {
    View rootView;
    GlobalAppState appState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        rootView = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("onCreateView", "Create view Called" );
        rootView = inflater.inflate(R.layout.fragment_whatsnew, container,
                false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showResults();
    }

    private void showResults() {
        GridView stgv = (GridView) getActivity()
                .findViewById(R.id.stgv);
        List<ContentDealDTO> mItems = new ArrayList<ContentDealDTO>();
        appState = (GlobalAppState) getActivity().getApplication();
        if (appState.getAdvertisements() != null) {
            mItems = appState.getAdvertisements();
            NewDealsProms newDeals = new NewDealsProms(getActivity());
            mItems =  newDeals.getLists(mItems);
        }else {
            mItems = new ArrayList<ContentDealDTO>();
        }
        Log.e("Items","Items Size:"+mItems.size());
        Set<ContentDealDTO> setItems = new HashSet<ContentDealDTO>(mItems);
        mItems = new ArrayList<ContentDealDTO>(setItems);
        Log.e("Items","Items Size:"+setItems.size());
        appState.setAdvertisements(mItems);
        Log.e("Orientation","Orientations:"+getResources().getConfiguration().orientation);
        if (mItems.size() > 0) {
            stgv.setVisibility(View.VISIBLE);
            ((RelativeLayout) getActivity().findViewById(R.id.noads))
                    .setVisibility(View.GONE);
        } else {
            stgv.setVisibility(View.GONE);
            ((RelativeLayout) getActivity().findViewById(R.id.noads))
                    .setVisibility(View.VISIBLE);

        }
        SharedPreferences prefs = getActivity().getSharedPreferences("HB",
                Context.MODE_PRIVATE);
        String backColor = "#" + prefs.getString("BACKGROUND_COLOR", "000000");
        String textColor = "#" + prefs.getString("TEXT_COLOR", "ffffff");
        float textSize = Float.parseFloat(prefs.getString("FONT_SIZE", "16"));
        String textFont = "#" + prefs.getString("TEXT_FONT", "DEFAULT");
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int LargeSize = 0;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
           LargeSize = (int) ((height / 7) * 1.95);
        else{
            LargeSize = (int) ((height / 3) * 1.95);
        }
        AdvertisementAdapter mAdapter = new AdvertisementAdapter(getActivity(),
                getActivity().getApplication(), mItems, backColor, textColor,
                textSize, textFont,LargeSize,appState);
        stgv.setAdapter(mAdapter);
        if(appState.getCity().equals("Hungry Bells")){
            ((TextView) rootView.findViewById(R.id.textViewnoAds)).setText("Location services not enabled");
            ((TextView) rootView.findViewById(R.id.textViewnoAdsTry)).setText("Please Enable location services");
            ((ImageView) rootView.findViewById(R.id.imageViewretryads))
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    });
        }else{
            ((ImageView) rootView.findViewById(R.id.imageViewretryads))
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(!appState.getCity().equals("Hungry Bells"))
                                startActivity(new Intent(getActivity(),SearchCityActivity.class));

                        }
                    });}
        mAdapter.notifyDataSetChanged();
    }

}
