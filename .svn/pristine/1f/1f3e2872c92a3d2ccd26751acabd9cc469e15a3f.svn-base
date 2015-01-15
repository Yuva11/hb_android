package com.HungryBells.activity.subactivity;

import android.app.Activity;
import android.util.Log;

import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.activity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arunkumar on 10/12/2014.
 */
public class NewDealsProms {
    Activity activity;
    public NewDealsProms(Activity activity){
               this.activity = activity;
    }
    public  List<ContentDealDTO>  getLists(List<ContentDealDTO> contents){
        List<ContentDealDTO>promotional = new ArrayList<ContentDealDTO>();
        List<ContentDealDTO>nonPromotional = new ArrayList<ContentDealDTO>();
        for(ContentDealDTO ads :contents){
            if(ads.getContentType().equals("PROMOTIONAL")){
                promotional.add(ads);
            }else{
                nonPromotional.add(ads);
            }
        }
        return changePromotionals(promotional,nonPromotional);
    }
    private List<ContentDealDTO> changePromotionals(List<ContentDealDTO> promotional,List<ContentDealDTO> nonpromotional){
        List<ContentDealDTO>contents = new ArrayList<ContentDealDTO>();
        boolean finished = true;
        while(finished) {
            if (promotional.size() > 0 && nonpromotional.size() > 0) {
                if (promotional.size() >= Integer.parseInt(activity.getString(R.string.promotional))) {
                    Integer promo = Integer.parseInt(activity.getString(R.string.promotional));
                    for(int i=0;i<promo;i++){
                        contents.add(promotional.get(0));
                        promotional.remove(0);
                    }
                } else {
                    contents.addAll(promotional);
                    promotional.removeAll(promotional);
                }
                if (nonpromotional.size() >= Integer.parseInt(activity.getString(R.string.nonpromotional))) {
                    Integer nonpromo = Integer.parseInt(activity.getString(R.string.nonpromotional));
                    for(int i=0;i<nonpromo;i++){
                        contents.add(nonpromotional.get(0));
                        nonpromotional.remove(0);
                    }
                } else {
                    contents.addAll(nonpromotional);
                    promotional.removeAll(nonpromotional);
                }

            } else {
                contents.addAll(promotional);
                contents.addAll(nonpromotional);
                finished = false;
            }
        }
       return  contents;
    }
}
