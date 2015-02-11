package com.HungryBells.activity.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.R;
import com.HungryBells.activity.UserActivity;

/*This method is used to show list of nearest locations*/
public class PlaceListAdapter extends BaseAdapter {

    /*nearest locations list*/
	List<UserLocationDTO> placeTitle;

    /*user context*/
	Activity context;

   /*layout for background*/
    RelativeLayout listviews;

    /*Textview to show location names*/
    TextView title;

    /*adapter constructor*/
	public PlaceListAdapter(Activity context, List<UserLocationDTO> text) {
		placeTitle = text;
		this.context = context;
	}

	public int getCount() {
		return placeTitle.size();
        // total number of elements in the list
	}

	public Object getItem(int arg0) {
		return null;// single item in the list
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ViewHolder")
	public View getView(final int position, View convertView, ViewGroup parent) {
		View row;
		LayoutInflater inflater = context.getLayoutInflater();
        /*layout for nearest locations is listview_background*/
		row = inflater.inflate(R.layout.listview_background, parent, false);

        ImageView imageView = (ImageView)row.findViewById(R.id.imageView);
		title = (TextView) row.findViewById(R.id.placeText);
		title.setText(placeTitle.get(position).getName());
         listviews
                = (RelativeLayout)row.findViewById(R.id.listviews);
         /*
         *  my location is with different color
         * */
        if(placeTitle.get(position).getName().contains("My Location")){
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_home_1));
            listviews.setBackgroundColor(Color.parseColor("#10000000"));
            title.setBackgroundColor(Color.parseColor("#00000000"));
        }else{
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_back));
            listviews.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /*onclick listener for click on the list*/
        listviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setBackgroundColor(Color.parseColor("#ffffff"));
                view.setBackgroundColor(Color.parseColor("#d21617"));
                ((UserActivity)context).searchByLocation(placeTitle.get(position),
                        context);
            }
        });
		return (row);
	}

}
