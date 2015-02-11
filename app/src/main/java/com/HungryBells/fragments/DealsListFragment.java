package com.HungryBells.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.DealViewsDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.activity.DealsActivity;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.activity.SearchCityActivity;
import com.HungryBells.activity.adapter.DealsPageAdapter;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

/*This is the fragment for deals list*/
public class DealsListFragment extends Fragment {
	List<Deals> tempDeals;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		viewAllDetails();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.activity_tab_deallist, container,
				false);

		return rootView;
	}

    /*This method is used to show deals in the list*/
	private void viewAllDetails() {
		appState = (GlobalAppState) getActivity().getApplication();
		tempDeals = new ArrayList<Deals>();
		if (appState.getAllDeals() != null) {
			tempDeals = appState.getAllDeals();
		} else
			tempDeals = new ArrayList<Deals>();
		GridView grid = (GridView) getActivity().findViewById(R.id.grids);
		if (tempDeals.size() > 0) {
			grid.setVisibility(View.VISIBLE);
			((RelativeLayout) getActivity().findViewById(R.id.nocoupon))
					.setVisibility(View.GONE);
		} else {
			grid.setVisibility(View.GONE);
			((RelativeLayout) getActivity().findViewById(R.id.nocoupon))
					.setVisibility(View.VISIBLE);
		}
		/*((ImageView) getActivity().findViewById(R.id.imageViewretrydeals))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						new SearchCityDialog(getActivity(),
								R.style.myBackgroundStyle, appState).show();

					}
				});*/
        if(appState.getCity().equals("Hungry Bells")) {
            Log.e("Inside Data","Inside");
            ((TextView) rootView.findViewById(R.id.textViewnoAds)).setText("Location services not enabled");
            ((TextView) rootView.findViewById(R.id.textViewnoAdsTry)).setText("Please Enable locations");
            ((ImageView) rootView.findViewById(R.id.imageViewretrydeals))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                        }
                    });
        }else{
            ((ImageView) rootView.findViewById(R.id.imageViewretrydeals))
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if(!appState.getCity().equals("Hungry Bells"))
                                startActivity(new Intent(getActivity(),SearchCityActivity.class));

                        }
                    });
        }
		DealsPageAdapter adapter = new DealsPageAdapter(getActivity(),
				tempDeals, appState);
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
                    sendDelasList(tempDeals.get(position));
					((DealsActivity) getActivity()) // Crash
							.dealsViewPage(position);
				} catch (Exception e) {
					((DealsActivity) getActivity()).onRestart();

				}

			}
		});
		adapter.notifyDataSetChanged();

	}

    /*If the used click on deals the data will send to HBserver*/
     private void sendDelasList(Deals deals){
         Customers customer = appState.getProfile();
         DealViewsDTO deal = new DealViewsDTO();
         Deals myDeal = new Deals();
         myDeal.setId(deals.getId());
         deal.setCustomer(customer);
         deal.setDeal(myDeal);
         String url = getActivity().getString(R.string.serverurl_test)
                 + "deal/captureview";
         Ion.with(getActivity()).load(url).setJsonPojoBody(deal).asString()
                 .setCallback(new FutureCallback<String>() {
                     @Override
                     public void onCompleted(Exception e, String result) {
                     }
                 });
     }
	public static void setAdaptesValue() {

	}
}
