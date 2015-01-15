package com.HungryBells.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;

import com.HungryBells.DTO.ContentDTO;
import com.HungryBells.DTO.ContentDealDTO;
import com.HungryBells.DTO.Deals;
import com.HungryBells.DTO.MerchantBranchDto;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.GlobalAppState;
import com.HungryBells.service.ServiceListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonParsing {

	public static void jsonParsingCities(Bundle data, GlobalAppState appState) {
		String response = data.getString(ServiceListener.RESPONSEDATA);
        Log.e("reps",response);
		response = "{\"userLocation\":"
				+ response.substring(0, response.length() - 1) + "}";
		response.replace("\n", "");
        /*JsonReader reader = new JsonReader(new StringReader(response));
        reader.setLenient(true);
        Gson gson = new Gson();
        List<UserLocationDTO> userinfo1 = Arrays.asList(gson.fromJson(reader, UserLocationDTO[].class));*/
		Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
		try {
			List<UserLocationDTO> allLocations = datas.userLocation;
			appState.setUserLocations(allLocations);
		} catch (Exception e) {
			Log.e("Json Parsing", e.toString(), e);
		}

	}

	public static void favorateRestaurent(Bundle data, GlobalAppState appState) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		String response = data.getString(ServiceListener.RESPONSEDATA);
		gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
		Gson gson = gsonBuilder.create();
		List<MerchantBranchDto> merchatBranch = new ArrayList<MerchantBranchDto>();
		try {
            Log.e("Response",response);
			merchatBranch = new LinkedList<MerchantBranchDto>(
					Arrays.asList(gson.fromJson(response,
							MerchantBranchDto[].class)));

		} catch (Exception e) {
			Log.e("Json Parsing", e.toString(), e);
			merchatBranch = new ArrayList<MerchantBranchDto>();
		}
		appState.setMerchatBranch(merchatBranch);
	}

	public static void allDealContents(Bundle data, GlobalAppState appState) {
		String response = data.getString(ServiceListener.RESPONSEDATA);
		Util.Sink datas = new Gson().fromJson(response, Util.Sink.class);
		List<ContentDealDTO> advertisement = datas.advertisements;
		List<Deals> allDeals = datas.deals;
		List<Deals> allPromos = datas.promos;
		ContentDTO contents = new ContentDTO();
		contents.setAdvertisements(advertisement);
		contents.setDeals(allDeals);
		contents.setPromos(allPromos);
		contents.setCurrentTime(new Date().getTime());
		if (appState.getAdvertisements() == null) {
			advertisement = new ArrayList<ContentDealDTO>();
		}
		if (appState.getAllDeals() == null) {
			allDeals = new ArrayList<Deals>();
		}
		if (appState.getAllPromos() == null) {
			allPromos = new ArrayList<Deals>();
		}
		Map<String, ContentDTO> contentData = ContentsCache.getInstance()
				.getContents();
		contentData.put(appState.getActionBarLocation(), contents);
		ContentsCache.getInstance().setContents(contentData);

	}
}
