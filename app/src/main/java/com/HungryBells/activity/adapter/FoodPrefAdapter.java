package com.HungryBells.activity.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.HungryBells.activity.FoodPreferenceSwitch;
import com.HungryBells.activity.R;
import com.HungryBells.util.FoodPreferenceSelection;

public class FoodPrefAdapter extends BaseAdapter {

	List<FoodPreferenceSelection> notificationPreference;
    Context context;
	public FoodPrefAdapter(List<FoodPreferenceSelection> notificationPreference,Context context) {
		this.notificationPreference = notificationPreference;
        this.context = context;
	}

	@Override
	public int getCount() {
		return notificationPreference.size(); // total number of elements in the
	}

	@Override
	public Object getItem(int i) {
		return notificationPreference.get(i); // single item in the list
	}

	@Override
	public long getItemId(int i) {
		return i; // index number
	}

	@Override
	public View getView(int index, View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.adapter_switch, parent, false);
		}

		final FoodPreferenceSelection dataModel = notificationPreference
				.get(index);

		Switch prefSwitch = (Switch) view.findViewById(R.id.switch1);
		prefSwitch.setText(dataModel.getType().toString());
		prefSwitch.setChecked(false);
		if (dataModel.isChecked()) {
			prefSwitch.setChecked(true);
		}
		prefSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				dataModel.setChecked(isChecked);
             ((FoodPreferenceSwitch)context).savePreferences(notificationPreference);

			}
		});
		return view;
	}


}