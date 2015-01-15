package com.HungryBells.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;
import com.HungryBells.activity.adapter.PlaceListAdapter;

public class SearchCityDialog extends Dialog implements OnItemClickListener {
	Activity context;
	ListView listPlace;
	EditText placeSearchText;
	List<UserLocationDTO> array_sort = new ArrayList<UserLocationDTO>();
	int textlength = 0;
	List<UserLocationDTO> allLocations = new ArrayList<UserLocationDTO>();
	GlobalAppState appState;

	public SearchCityDialog(Activity _context, int style,
			GlobalAppState appState) {
		super(_context, style);
		context = _context;
		this.appState = appState;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.fragment_search);
		setCanceledOnTouchOutside(true);
		setCancelable(true);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.y = 65;
		params.x = 10;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		getWindow().setAttributes(params);
		listPlace = (ListView) findViewById(R.id.ListView01);

		placeSearchText = (EditText) findViewById(R.id.EditText01);
		allLocations = appState.getUserLocations();
		if (allLocations == null) {
			allLocations = new ArrayList<UserLocationDTO>();
		}
		if (allLocations.size() > 0)
			if (allLocations.get(0).getName().contains("My Location")) {
				allLocations.remove(0);
			}
		UserLocationDTO locationNow = new UserLocationDTO();
		locationNow.setName("My Location (" + appState.getCity() + ")");
		allLocations.add(0, locationNow);
		// placeSearchText.setText("");

		listPlace.setAdapter(new PlaceListAdapter(context, allLocations));
		listPlace.setOnItemClickListener(this);
		listPlace.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, getSize(allLocations.size())));
		placeSearchText.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				textlength = placeSearchText.getText().length();
				array_sort.clear();
				for (int i = 0; i < allLocations.size(); i++) {
					if (textlength <= allLocations.get(i).getName().length()) {
						if (placeSearchText
								.getText()
								.toString()
								.equalsIgnoreCase(
										(String) allLocations.get(i).getName()
												.subSequence(0, textlength))) {
							array_sort.add(allLocations.get(i));

						}
					}
				}
				if (array_sort.size() == 0) {
					UserLocationDTO locations = new UserLocationDTO();
					locations.setName("My Location (" + appState.getCity()
							+ ")");
					array_sort.add(locations);
				}
				listPlace.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, getSize(array_sort.size())));
				listPlace.setAdapter(new PlaceListAdapter(context, array_sort));

			}
		});
	}

	protected int getSize(int size) {
		if (size > 5) {
			return 425;
		} else {
			return size * 85;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
				.getWindowToken(), 0);
		if (array_sort.size() > 0) {
			((UserActivity) context).searchByLocation(array_sort.get(position),
					context);
		} else {
			((UserActivity) context).searchByLocation(
					allLocations.get(position), context);
		}
		dismiss();
	}
}
