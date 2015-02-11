package com.HungryBells.activity.adapter; // index number

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.HungryBells.DTO.NotificationPreference;
import com.HungryBells.activity.FoodPreferenceSwitch;
import com.HungryBells.activity.NotificationPreferenceSwitch;
import com.HungryBells.activity.R;

/*This adapter is used to show the notification preferences form server*/
public class NotificationPrefAdapter extends BaseAdapter {

     /*Notification preference list from the server*/
	List<NotificationPreference> notificationPreference;

    /*user context*/
     Context context;

    /*adapter constructor*/
	public NotificationPrefAdapter(
			List<NotificationPreference> notificationPreference,Context context) {
		this.notificationPreference = notificationPreference;
        this.context = context;
	}

    /*Base adapter override methods*/
	@Override
	public int getCount() {
		return notificationPreference.size(); // total number of elements in the list
	}

    /*Base adapter override methods*/
	@Override
	public Object getItem(int i) {
		return notificationPreference.get(i); // single item in the list
	}

    /*Base adapter override methods*/
	@Override
	public long getItemId(int i) {
		return i; // index number
	}
    /*Base adapter override methods*/
	@Override
	public View getView(int index, View view, final ViewGroup parent) {

		if (view == null) {
            /*layout for notification preference is adapter_switch*/
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.adapter_switch, parent, false);
		}

		final NotificationPreference dataModel = notificationPreference
				.get(index);
        /*on/off Button*/
		Switch prefSwitch = (Switch) view.findViewById(R.id.switch1);
		prefSwitch.setText(dataModel.getPreferenceType());
		prefSwitch.setChecked(false);
		if (dataModel.isTypeStatus()) {
			prefSwitch.setChecked(true);
		}
        /*switch button change listener*/
		prefSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				dataModel.setTypeStatus(isChecked);
                ((NotificationPreferenceSwitch)context).savePreferences(notificationPreference);

			}
		});
		return view;
	}

}