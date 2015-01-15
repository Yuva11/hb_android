package com.HungryBells.activity.subactivity;

import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.HungryBells.activity.R;

public class AnimationRunActivty {
	private Animation animation1;
	ImageView animationTarget;
	Activity context;
	int id;

	public AnimationRunActivty(Activity _context) {
		context = _context;
	}

	public void runAnimation(int _id) {
		id = _id;
		animationTarget = (ImageView) context.findViewById(id);
		animation1 = AnimationUtils.loadAnimation(context, R.anim.pendulam);
		animationTarget.setAnimation(animation1);
	}

}
