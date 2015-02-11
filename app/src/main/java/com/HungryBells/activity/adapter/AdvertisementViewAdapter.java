package com.HungryBells.activity.adapter;

import java.util.List;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.taptwo.android.widget.TouchImageView;
import org.taptwo.android.widget.TouchImageView.OnTouchImageViewListener;
import org.taptwo.android.widget.ViewFlowEX;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.ContentTemplate;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.activity.R;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.fragments.ViewAdsFragment;
import com.HungryBells.service.VideoViewCustom;

import com.nostra13.universalimageloader.core.ImageLoader;

import lombok.Getter;
import lombok.Setter;


/*This method is used to view advertisement*/
@SuppressLint("InflateParams")
public class AdvertisementViewAdapter extends BaseAdapter {

    /*user context*/
	Context ct;

    /*inflater method called*/
	private LayoutInflater mInflater;

    /*list of advertisement elements*/

	List<ContentDealDTO> advertisement;

    /*Application context */
	Application mAppContext;

   /*Display size of device*/
	Display display;

     /*Third party library*/
	ViewFlowEX views;

   /* front and back view of advertisement*/
    View mFrontView,mBackView;

    /*Image loader library for image loading*/
	ImageLoader imageLoader;

    /*boolean variable for front page or not*/
    @Getter
    @Setter
    boolean frontPage = true;


    /*adapter constructor*/
	public AdvertisementViewAdapter(Context context, Application application,
			Display display, List<ContentDealDTO> advertisement,
			ViewFlowEX viewFlow) {
		try {
			ct = context;
			this.advertisement = advertisement;
			mAppContext = application;
			this.display = display;
			views = viewFlow;

			imageLoader = ImageLoader.getInstance();

			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		} catch (Exception e) {
		}
	}

	public int getCount() {
		return advertisement.size();// total number of elements in the list
	}

	public ContentDealDTO getItem(int position) {
		return advertisement.get(position);// single item in the list
	}

	public long getItemId(int position) {
		return position; // index number
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.fragment_advertisement,
					null);
			holder = new ViewHolder();
			holder.advertisementLayout = (LinearLayout) convertView
					.findViewById(R.id.advertisementLayout);
			holder.textLayout = (RelativeLayout) convertView
					.findViewById(R.id.textLayout);//text advertisement layout
			holder.imageLayout = (RelativeLayout) convertView
					.findViewById(R.id.imageLayout);//image advertisement layout
			holder.textOnlyContent = (WebView) convertView
					.findViewById(R.id.textOnlyContent);//having only text
			holder.zoomImageView = (TouchImageView) convertView
					.findViewById(R.id.zoomImageView);//image is used here
            //TouchImageView is thirdparty library used to zoom an image
			holder.imageViewPlaybutton = (ImageView) convertView
					.findViewById(R.id.imageViewPlaybutton);//play button image
			holder.imageWithText = (TextView) convertView
					.findViewById(R.id.imageWithText);//textview with imagelayout
			holder.videoView = (VideoViewCustom) convertView
					.findViewById(R.id.videoView1);//videoview in advertisement
            holder.textViewAdslike = (ImageView) convertView
                    .findViewById(R.id.textViewAdslike);
            holder.textViewfeedback = (ImageView) convertView
                    .findViewById(R.id.textViewfeedback);//feedback button image
            holder.imageViewtw = (ImageView) convertView
                    .findViewById(R.id.imageViewtw);//twitter button image
            holder.imageViewli = (ImageView) convertView
                    .findViewById(R.id.imageViewli);//linkedin button image
            holder.imageViewfb = (ImageView) convertView
                    .findViewById(R.id.imageViewfb);//facebook button image
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ContentDealDTO item = advertisement.get(position);
          //checking content template
		if (item.getContentTemplate() == ContentTemplate.IMAGE_ONLY
				|| item.getContentTemplate() == ContentTemplate.VIDEO_AND_TEXT) {
			holder.imageLayout.setVisibility(View.VISIBLE);
			holder.textLayout.setVisibility(View.GONE);

			if (item.getContentTemplate() == ContentTemplate.VIDEO_AND_TEXT) {

                //playbutton visible
				holder.imageViewPlaybutton.setVisibility(View.VISIBLE);
				// new AQuery(act)
				holder.zoomImageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						setUrlForUser(holder.imageLayout, holder.textLayout,
								holder.videoView, item);
					}
				});
			} else {
				holder.imageViewPlaybutton.setVisibility(View.GONE);

			}

            String url = item.getDisplayURL();
			imageLoader.displayImage(url, holder.zoomImageView);
            holder.imageWithText.setVisibility(View.GONE);


			// holder.imageView3.setVisibility(View.VISIBLE);
			holder.imageViewPlaybutton
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							setUrlForUser(holder.imageLayout,
									holder.textLayout, holder.videoView, item);
						}
					});
			holder.zoomImageView
					.setOnTouchImageViewListener(new OnTouchImageViewListener() {

						@Override
						public void onMove() {
							boolean isZoomed = holder.zoomImageView.isZoomed();
							if (isZoomed) {
								views.setScrollable(false);
							} else {
								views.setScrollable(true);
							}
						}
					});
		} else {
			holder.imageLayout.setVisibility(View.GONE);
			holder.textLayout.setVisibility(View.VISIBLE);
			String detailedText = item.getDetailText();
			String mimeType = "text/html";
			String encoding = "UTF-8";
			// holder.textOnlyContent.setBackgroundResource(Color
			// .parseColor(backColor));
			holder.textOnlyContent.loadDataWithBaseURL("", detailedText,
					mimeType, encoding, "");

		}
        if (item.getIsliked()) {
            holder.textViewAdslike.setImageResource(R.drawable.unlikes);
        } else {
            holder.textViewAdslike.setImageResource(R.drawable.likes);
        }

        holder.textViewAdslike.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewAdsActivity) ct).addAdvertisementLike();
            }
        });
        holder.textViewfeedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewAdsActivity)ct).adFeedBack();
            }
        });
        holder.imageViewtw.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewAdsActivity)ct).updateStatus(SocialAuthAdapter.Provider.TWITTER);
            }
        });
        holder.imageViewli.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewAdsActivity)ct).updateStatus(SocialAuthAdapter.Provider.LINKEDIN);
            }
        });
        holder.imageViewfb.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ViewAdsActivity)ct).updateStatus(SocialAuthAdapter.Provider.FACEBOOK);
            }
        });

		return convertView;
	}

	class ViewHolder {
		LinearLayout advertisementLayout;
		TouchImageView zoomImageView;
		ImageView imageViewPlaybutton,textViewAdslike,textViewfeedback,imageViewtw,imageViewli,imageViewfb;
		WebView textOnlyContent;
		RelativeLayout textLayout, imageLayout;
		TextView imageWithText;
		VideoViewCustom videoView;
	}

    /*
    * This method is called every time share button clicked
    * when fragment is in back page scroll will be disabled
    * */
    public void pageChange(int position){
       View v = views.getSelectedView();
        mFrontView = v.findViewById(R.id.front);
        mBackView = v.findViewById(R.id.back);
        pageChange(mFrontView,mBackView,1500);
        if(frontPage){
            views.setScrollable(false);
        }else{
            views.setScrollable(true);
        }

    }

    /*
    * share page and advertisement page animation
    * frontpage is advertisement page and back is that advertisement
    * */
    public void pageChange(final View front, final View back, final int duration) {
        if (front==null){
            Log.e("front","front changes");
        }
        if (frontPage) {
            frontPage=false;
            front.animate().rotationX(90).setDuration(duration / 2).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    front.setVisibility(View.GONE);
                    back.setRotationX(90);
                    back.setVisibility(View.VISIBLE);
                    back.animate().rotationX(0).setDuration(duration / 2).setListener(null);
                }
            });
        } else{
            frontPage=true;
            back.animate().rotationX(90).setDuration(duration / 2).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    back.setVisibility(View.GONE);
                    front.setRotationX(90);
                    front.setVisibility(View.VISIBLE);
                    front.animate().rotationX(0).setDuration(duration / 2).setListener(null);
                }
            });
        }

    }

    /*
    * video view called in this method
    *url is already set in the item contentdto
    * OnCompletionListener called when the video successfully completed
    *
    * */
	private void setUrlForUser(final RelativeLayout img_content2,
			RelativeLayout img_contents, final VideoViewCustom videoView,
			ContentDealDTO item) {
		views.setScrollable(false);
		String link = item.getVideoURL();
		img_content2.setVisibility(View.GONE);
		img_contents.setVisibility(View.GONE);
		videoView.setVisibility(View.VISIBLE);
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		if (height > width) {
			height = (width / 4) * 3;
		}
		videoView.getHolder().setFixedSize(width, height);
		videoView.setDimensions(width, height);
		MediaController mediaController = new MediaController(ct);
		mediaController.setAnchorView(videoView);
		Uri video = Uri.parse(link);
		videoView.setMediaController(mediaController);
		videoView.setVideoURI(video);
		videoView.requestFocus();
		videoView.start();
		videoView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				views.setScrollable(true);
                ((ViewAdsActivity)ct).playVideo(false);
				img_content2.setVisibility(View.VISIBLE);
				if (extra == -1004) {
					videoView.setVisibility(View.GONE);
					return true;
				}
				Log.e("Error", extra + "");
				Log.e("Error", what + "");
				return false;
			}
		});
		videoView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				views.setScrollable(true);
                ((ViewAdsActivity)ct).playVideo(false);
				img_content2.setVisibility(View.VISIBLE);
				videoView.setVisibility(View.GONE);
			}
		});

	}
}
