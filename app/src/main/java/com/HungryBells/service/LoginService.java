package com.HungryBells.service;

import android.app.Activity;

import com.HungryBells.util.ServiceType;

public class LoginService {

	public static boolean login(Activity context, ServiceType type) {

		ServiceProvider socialmedia = ServiceProviderFactory
				.getServiceProvider(type);
		socialmedia.getAccessToken(context);

		return false;
	}

}
