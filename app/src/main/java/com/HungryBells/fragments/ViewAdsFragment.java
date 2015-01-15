package com.HungryBells.fragments;

import org.brickred.socialauth.android.SocialAuthAdapter;
import org.taptwo.android.widget.TouchImageView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.ContentTemplate;
import com.HungryBells.activity.R;
import com.HungryBells.activity.ViewAdsActivity;
import com.HungryBells.service.VideoViewCustom;
import com.HungryBells.util.MyTextView;
import com.HungryBells.util.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import lombok.Getter;
import lombok.Setter;

public class ViewAdsFragment extends Fragment implements OnClickListener{
    ImageLoader imageLoader;
    ContentDealDTO contents;
    View rootView,mFrontView,mBackView;
    RelativeLayout textLayout, imageLayout;
    VideoViewCustom videoView;
    int position;

    @Getter @Setter
    boolean frontPage = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        rootView = inflater.inflate(R.layout.fragment_advertisement, container,
                false);
        mFrontView = rootView.findViewById(R.id.front);
        mBackView = rootView.findViewById(R.id.back);
        imageLoader = ImageLoader.getInstance();
        Bundle args = getArguments();
        contents = (ContentDealDTO) args.getSerializable("ContentDealDTO");
        position = args.getInt("page_position");
        addViews(rootView);
        return rootView;
    }

    public void update() {
        flip(mFrontView, mBackView, 1000);
        Log.e("Datachange","Data changes");
    }
    public void flip(final View front, final View back, final int duration) {
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
    private void addViews(final View rootView) {
        try {
            TouchImageView zoomImageView;
            ImageView imageViewPlaybutton;
            WebView textOnlyContent;
            MyTextView imageWithText;
            ((ImageView)rootView. findViewById(R.id.textViewAdslike))
                    .setOnClickListener(this);
            ((ImageView)rootView. findViewById(R.id.textViewfeedback))
                    .setOnClickListener(this);
            ((ImageView) rootView.findViewById(R.id.imageViewfb))
                    .setOnClickListener(this);
            ((ImageView)rootView. findViewById(R.id.imageViewtw))
                    .setOnClickListener(this);
            ((ImageView) rootView.findViewById(R.id.imageViewli))
                    .setOnClickListener(this);
            ((ImageView) rootView
                    .findViewById(R.id.imageViewPlaybutton)).setOnClickListener(this);
            textLayout = (RelativeLayout) rootView
                    .findViewById(R.id.textLayout);
            imageLayout = (RelativeLayout) rootView
                    .findViewById(R.id.imageLayout);
            textOnlyContent = (WebView) rootView
                    .findViewById(R.id.textOnlyContent);
            zoomImageView = (TouchImageView) rootView
                    .findViewById(R.id.zoomImageView);

            imageViewPlaybutton = (ImageView) rootView
                    .findViewById(R.id.imageViewPlaybutton);
            imageWithText = (MyTextView) rootView
                    .findViewById(R.id.imageWithText);
            videoView = (VideoViewCustom) rootView
                    .findViewById(R.id.videoView1);
            textLayout.setVisibility(View.GONE);
           /* zoomImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Image","Clicked inside");
                    setUrlForUser(imageLayout, textLayout, videoView,
                            contents);
                }
            });*/
            String url = contents.getDisplayURL();
            if (contents.getContentTemplate() == ContentTemplate.IMAGE_ONLY
                    || contents.getContentTemplate() == ContentTemplate.VIDEO_AND_TEXT) {
                imageLayout.setVisibility(View.VISIBLE);

                if (contents.getContentTemplate() == ContentTemplate.VIDEO_AND_TEXT) {
                    imageViewPlaybutton.setVisibility(View.VISIBLE);
                    Log.e("URL Video",contents.getVideoURL());
                } else {
                    imageViewPlaybutton.setVisibility(View.GONE);
                }
                imageLoader.displayImage(url, zoomImageView);
                Log.e("imageViewPlaybutton","Registering");

                imageViewPlaybutton.setOnClickListener(this);
                Log.e("imageViewPlaybutton","Registering After Register ");
                if (contents.getContentTemplate() == ContentTemplate.IMAGE_ONLY) {
                    imageWithText.setVisibility(View.GONE);
                } else {
                    imageWithText.setVisibility(View.VISIBLE);
                }
                imageWithText.setText(contents.getDetailText());
                // imageView3.setVisibility(View.VISIBLE);

            } else {
                SharedPreferences prefs = getActivity().getSharedPreferences(
                        "HB", Context.MODE_PRIVATE);

                imageLayout.setVisibility(View.GONE);
                textLayout.setVisibility(View.VISIBLE);
                String detailedText = contents.getDetailText();
                String mimeType = "text/html";
                String encoding = "UTF-8";
                textOnlyContent. setBackgroundColor(0);
                Log.e("detailedText",detailedText);
                textOnlyContent.loadDataWithBaseURL(null, detailedText, mimeType,
                        encoding, "about:blank");

            }
        } catch (Exception e) {
            Log.e("Error inside", e.toString(), e);
        }

    }

    @Override
    public void onClick(View view) {
        Log.e("Id on Click",view.getId()+"");
        if(networkChanges())
        switch (view.getId()) {
            case R.id.textViewfeedback:
                ((ViewAdsActivity)getActivity()).adFeedBack();
                break;
            case R.id.imageViewPlaybutton:
                Log.e("Viewads","Clicked");
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                setUrlForUser(imageLayout, textLayout, videoView,
                        contents);
                break;
            case R.id.imageViewfb:
                    ((ViewAdsActivity)getActivity()).updateStatus(SocialAuthAdapter.Provider.FACEBOOK);
                break;
            case R.id.textViewAdslike:
                ((ViewAdsActivity)getActivity()).addAdvertisementLike();
                break;
            case R.id.imageViewtw:
                ((ViewAdsActivity)getActivity()).updateStatus(SocialAuthAdapter.Provider.TWITTER);
                break;
            case R.id.imageViewli:
                ((ViewAdsActivity)getActivity()).updateStatus(SocialAuthAdapter.Provider.LINKEDIN);
                break;
            default:

                break;
        }
    }
     private boolean networkChanges(){
        if(Util.isNetworkConnectionAvailablity(getActivity())){
            return true;
        }else{
            Util.customToast(getActivity(),"No network connection");
            return false;
        }
     }
    private void setUrlForUser(final RelativeLayout img_content2,
                               RelativeLayout img_contents, final VideoViewCustom videoView,
                               ContentDealDTO item) {
        ((ViewAdsActivity)getActivity()).playVideo(true);
        String link = item.getVideoURL();
        Log.e("Link",link);
        img_content2.setVisibility(View.GONE);
        img_contents.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        Point size = new Point();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (height > width) {
            height = (width / 4) * 3;
        }
        videoView.getHolder().setFixedSize(width, height);
        videoView.setDimensions(width, height);
        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse(link);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        videoView.requestFocus();
        Log.e("Image","Clicked inside Test Env");
        videoView.start();
        videoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                img_content2.setVisibility(View.VISIBLE);
                if (extra == -1004) {
                    videoView.setVisibility(View.GONE);
                    return true;
                }
                Log.e("Error", extra + "");
                Log.e("Error", what + "");
                ((ViewAdsActivity)getActivity()).playVideo(false);
                return false;
            }
        });
        videoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e("Image","Clicked inside Complete");
                ((ViewAdsActivity)getActivity()).playVideo(false);
                img_content2.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
            }
        });

    }

}