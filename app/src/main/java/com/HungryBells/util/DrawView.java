package com.HungryBells.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
	Paint paint = new Paint();
	float[][] points;

	public DrawView(Context context, float[][] points2, int k) {
		super(context);
		paint.setColor(Color.BLUE);
		paint.setStrokeWidth(5);
		this.points = points2;
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawLine(0, getHeight(), getWidth(), 0, paint);
	}
}