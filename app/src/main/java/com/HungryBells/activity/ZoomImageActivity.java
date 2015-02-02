package com.HungryBells.activity;

import org.taptwo.android.widget.TouchImageView;

import android.os.Bundle;
import android.os.Parcelable;

import com.HungryBells.DTO.ServiceListenerType;
import com.nostra13.universalimageloader.core.ImageLoader;


/*This class is used to zoon an image*/
public class ZoomImageActivity extends UserActivity {

    /*Image loader library for image loading*/
	ImageLoader imageLoader;

    /*Touch imageview used to zoom image and ot developed by Finatel*/
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.zoomimages);
		TouchImageView image = (TouchImageView) findViewById(R.id.zoomImageView);
		imageLoader = ImageLoader.getInstance();
		String url = (String) getIntent().getExtras().getString("url");
		imageLoader.displayImage(url, image);
	}

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void processMessage(Bundle message, ServiceListenerType what) {
		// TODO Auto-generated method stub

	}

}
