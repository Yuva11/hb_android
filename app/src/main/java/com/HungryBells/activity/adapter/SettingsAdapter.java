package com.HungryBells.activity.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.HungryBells.DTO.NotificationPreference;
import com.HungryBells.activity.R;

/*This adapter is used to show the settings form server*/
public class SettingsAdapter extends ArrayAdapter<NotificationPreference> {

    /*user context*/
	Context con;

    /*Notification preference list from the server*/
	ArrayList<NotificationPreference> notificationsList;

    /*adapter constructor*/
	public SettingsAdapter(Context context, int textViewResourceId,
			List<NotificationPreference> notifications) {
		super(context, textViewResourceId, notifications);
		con = context;
		this.notificationsList = new ArrayList<NotificationPreference>();
		this.notificationsList.addAll(notifications);
	}

    /*View holder for base adapter*/
	private class ViewHolder {
		CheckBox name;
		TextView namePref;
		TextView timePref;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) con
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi
					.inflate(R.layout.settings_deals_notification, null);
			holder = new ViewHolder();
			holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.namePref = (TextView) convertView
					.findViewById(R.id.textViewname);
			holder.timePref = (TextView) convertView
					.findViewById(R.id.textViewtime);
			convertView.setTag(holder);
            /* check box for selecting notifications*/
			holder.name.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;

					NotificationPreference notification = (NotificationPreference) cb
							.getTag();
                    notification.setTypeStatus(cb.isChecked());

				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NotificationPreference prefs = notificationsList.get(position);
		holder.namePref.setText(prefs.getPreferenceType());
		holder.name.setChecked(prefs.isTypeStatus());
		if (prefs.getTiming() != null)
			holder.timePref.setText(prefs.getTiming());
		holder.name.setTag(prefs);
		return convertView;

	}
}