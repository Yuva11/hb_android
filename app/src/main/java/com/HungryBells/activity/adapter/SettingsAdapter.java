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

public class SettingsAdapter extends ArrayAdapter<NotificationPreference> {
	Context con;
	ArrayList<NotificationPreference> countryList;

	public SettingsAdapter(Context context, int textViewResourceId,
			List<NotificationPreference> notifications) {
		super(context, textViewResourceId, notifications);
		con = context;
		this.countryList = new ArrayList<NotificationPreference>();
		this.countryList.addAll(notifications);
	}

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

			holder.name.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					CheckBox cb = (CheckBox) v;

					NotificationPreference country = (NotificationPreference) cb
							.getTag();
					country.setTypeStatus(cb.isChecked());

				}
			});
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		NotificationPreference prefs = countryList.get(position);
		holder.namePref.setText(prefs.getPreferenceType());
		holder.name.setChecked(prefs.isTypeStatus());
		if (prefs.getTiming() != null)
			holder.timePref.setText(prefs.getTiming());
		holder.name.setTag(prefs);
		return convertView;

	}
}