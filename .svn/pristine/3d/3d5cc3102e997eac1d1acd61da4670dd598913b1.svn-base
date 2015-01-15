package com.HungryBells.activity.subactivity;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.HungryBells.activity.R;
import com.HungryBells.dialog.TwitterDialog;
import com.HungryBells.util.CustomProgressDialog;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthListener;

public class UpdateStatus {
	SocialAuthAdapter adapter;
	Activity activity;
   CustomProgressDialog progressDialog;
	public UpdateStatus(Activity activity) {
		this.activity = activity;
		changeStatus();
	}

	private void changeStatus() {
		try {
			adapter = new SocialAuthAdapter(new ResponseListener());
			adapter.addCallBack(SocialAuthAdapter.Provider.FACEBOOK,
					"http://www.hbells.com:8080/auth/facebook");

			adapter.addCallBack(SocialAuthAdapter.Provider.TWITTER, "http://hungrybells.mobi/");
			adapter.addCallBack(SocialAuthAdapter.Provider.GOOGLEPLUS,
					"http://www.finateltech.com");

			adapter.addConfig(SocialAuthAdapter.Provider.FACEBOOK,
					activity.getString(R.string.facebookkey),
					activity.getString(R.string.facebooksecret), null);
			adapter.addConfig(SocialAuthAdapter.Provider.TWITTER,
					activity.getString(R.string.twitterkey),
					activity.getString(R.string.twittersecret), null);
			adapter.addConfig(SocialAuthAdapter.Provider.LINKEDIN,
					activity.getString(R.string.linkedinkey),
					activity.getString(R.string.linkedinsecret), null);
		} catch (Exception e) {
			Log.e("Error in Change Status", e.toString(), e);
		}
	}

	public void updateStatus(SocialAuthAdapter.Provider provider,CustomProgressDialog progressDialog) {
		adapter.authorize(activity, provider);
        this.progressDialog = progressDialog;
	}

	private final class ResponseListener implements DialogListener {
		@Override
		public void onComplete(Bundle values) {
            progressDialog.dismiss();
			// Get name of provider after authentication
			final String providerName = values
					.getString(SocialAuthAdapter.PROVIDER);
			Log.d("Share-Bar", "Provider Name = " + providerName);
			try {
				if (providerName.equalsIgnoreCase("facebook")) {
					SharedPreferences prefs = activity.getSharedPreferences(
							"HB", Context.MODE_PRIVATE);
					String wallMessage = prefs.getString(
							"FB_WALL_POST_MESSAGE", "WOW! Nice Offers");
					adapter.updateStory(wallMessage, "HungryBells",
							"HungryBells is Good to get food deals",
							"Check for the latest deals from Restaurents",
							"http://hungrybells.mobi/",
							"http://hungrybells.mobi/images/banner.jpg",
							new MessageListener());
				} else if (providerName.equalsIgnoreCase("linkedin")) {
					SharedPreferences prefs = activity.getSharedPreferences(
							"HB", Context.MODE_PRIVATE);
					String wallMessage = prefs.getString("LINKEDIN_MESSAGE",
							"WOW! Nice Offers");
					adapter.updateStatus(wallMessage, new MessageListener(),
							true);
				} else {
					new TwitterDialog(activity, UpdateStatus.this).show();
				}
			} catch (Exception e) {
				Log.e("Error in FB", "Error");
			}

		}

		@Override
		public void onError(SocialAuthError error) {
			error.printStackTrace();
            progressDialog.dismiss();
			if (error.getMessage().contains("Status :403")) {
				Toast.makeText(activity, "Message Already posted ",
						Toast.LENGTH_LONG).show();
			}

			Log.d("Share-Bar", error.getMessage());
		}

		@Override
		public void onCancel() {
            progressDialog.dismiss();Log.d("Share-Bar", "Authentication Cancelled");
		}

		@Override
		public void onBack() {
            progressDialog.dismiss();Log.d("Share-Bar", "Dialog Closed by pressing Back Key");

		}
	}

	public void submitTwitts(String twitts) {
		adapter.updateStatus(twitts, new MessageListener(), true);
	}

	private final class MessageListener implements SocialAuthListener<Integer> {
		@Override
		public void onExecute(String provider, Integer t) {
            try {
            Integer status = t;
			if (status.intValue() == 200 || status.intValue() == 201
					|| status.intValue() == 204)
				Toast.makeText(activity, "Message posted on " + provider,
						Toast.LENGTH_LONG).show();
		 else
				Toast.makeText(activity, "Message not posted on " + provider,
						Toast.LENGTH_LONG).show();
            }catch (Exception e){

            }
		}

		@Override
		public void onError(SocialAuthError e) {

		}
	}
}
