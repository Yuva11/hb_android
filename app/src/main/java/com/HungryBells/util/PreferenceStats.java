package com.HungryBells.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.HungryBells.DTO.NotificationPreference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PreferenceStats {
	private List<NotificationPreference> getAllNotificationList(
			List<NotificationPreference> notificationList, Context con) {
		List<NotificationPreference> notificationChanged = new ArrayList<NotificationPreference>();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(con);

		for (NotificationPreference notePref : notificationList) {
			notePref.setTypeStatus(prefs.getBoolean(
					notePref.getPreferenceType(), true));
			notificationChanged.add(notePref);
		}

		return notificationChanged;
	}

	public List<NotificationPreference> getData(Object notification, Context con) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		String notificationText = (String) notification;
		Gson gson = gsonBuilder.create();
		List<NotificationPreference> notifications = Arrays.asList(gson
				.fromJson(notificationText, NotificationPreference[].class));
		return getAllNotificationList(notifications, con);
	}
}
