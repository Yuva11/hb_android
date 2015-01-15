package com.HungryBells.dialog;

import org.taptwo.android.widget.TouchImageView;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.HungryBells.activity.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ZoomImageDialog extends Dialog {

	Context dialogContext;
	ImageLoader imageLoader;
	String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		setContentView(R.layout.zoomimages);
		setCanceledOnTouchOutside(true);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		TouchImageView image = (TouchImageView) findViewById(R.id.zoomImageView);
		imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(url, image);
	}

	public ZoomImageDialog(Context context, String value) {
		super(context);
		dialogContext = context;
		url = value;
	}

}