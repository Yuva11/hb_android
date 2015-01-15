package com.HungryBells.activity.subactivity;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.HungryBells.activity.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class UILWrapper {

	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true)
				.resetViewBeforeLoading(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(R.drawable.placholder)
				.showImageOnFail(R.drawable.placholder)
				.imageScaleType(ImageScaleType.EXACTLY)
				.showImageOnLoading(R.drawable.placholder).build();
		File cacheDir = new File(context.getCacheDir(), "imgcachedir");
		if (!cacheDir.exists())
			cacheDir.mkdir();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.threadPoolSize(4).denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new HashCodeFileNameGenerator())
				.discCacheSize(100 * 1024 * 1024)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.memoryCache(new WeakMemoryCache())
				.defaultDisplayImageOptions(options)
				.tasksProcessingOrder(QueueProcessingType.FIFO).build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
}
