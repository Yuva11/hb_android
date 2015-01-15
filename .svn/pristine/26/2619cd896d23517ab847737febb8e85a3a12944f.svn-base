package com.HungryBells.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.HungryBells.activity.R;
import com.HungryBells.activity.SocialAuthActivity;
import com.HungryBells.service.ServiceProvider;

public class FaceBook implements ServiceProvider {
	@Override
	public void getAccessToken(Activity context) {
		Intent intent = new Intent(context, SocialAuthActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("authUrl", context.getString(R.string.serverurl_test)+"auth/facebook?scope=public_profile,email");
		intent.putExtras(bundle);
		context.startActivity(intent);
		context.finish();
	}
}
