package com.HungryBells.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.activity.SearchCityActivity;
import com.HungryBells.activity.adapter.CouponAdapter;

public class CouponsFragment extends Fragment {
	View rootView;
	GlobalAppState appState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.fragment_grids, container, false);

		return rootView;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		rootView = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewAllDetails();
	}

	private void viewAllDetails() {
		appState = (GlobalAppState) getActivity().getApplication();
		List<Deals> allPromos = new ArrayList<Deals>();
		if (appState.getAllPromos() != null) {
			allPromos = appState.getAllPromos();
		} else {
			allPromos = new ArrayList<Deals>();
		}
		GridView grid = (GridView) getActivity().findViewById(R.id.gridcoup);
		if (allPromos.size() > 0) {
			grid.setVisibility(View.VISIBLE);
			((RelativeLayout) getActivity().findViewById(R.id.nocouponvalue))
					.setVisibility(View.GONE);
		} else {
			grid.setVisibility(View.GONE);
			((RelativeLayout) getActivity().findViewById(R.id.nocouponvalue))
					.setVisibility(View.VISIBLE);

		}
        if(appState.getCity().equals("Hungry Bells")) {
            ((TextView) rootView.findViewById(R.id.textViewnoAds)).setText("Location services not enabled");
            ((TextView) rootView.findViewById(R.id.textViewnoAdsTry)).setText("Please Enable locations");
            ((ImageView) rootView.findViewById(R.id.imageViewretry))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    });
        }else{
            ((ImageView) rootView.findViewById(R.id.imageViewretry))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(!appState.getCity().equals("Hungry Bells"))
                                startActivity(new Intent(getActivity(),SearchCityActivity.class));

                        }
                    });
        }

		CouponAdapter couponAdapter = new CouponAdapter(getActivity(),
				allPromos,appState);
		grid.setAdapter(couponAdapter);
		couponAdapter.notifyDataSetChanged();
	}
}
