package com.HungryBells.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.HungryBells.DTO.FoodType;
import com.HungryBells.DTO.FoodTypeDto;
import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.SettingsDTO;
import com.HungryBells.activity.adapter.FoodPrefAdapter;
import com.HungryBells.service.ServiceListener;
import com.HungryBells.util.FoodPreferenceSelection;
import com.HungryBells.util.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FoodPreferenceSwitch extends UserActivity {
	Object notification;
	SettingsDTO settingsContent;
	String category;
	List<FoodPreferenceSelection> foodPref;
    Button buttonupdate;
    String notificationText;
    GsonBuilder gsonBuilder;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_notifilist);
		prefs = getSharedPreferences("HB", MODE_PRIVATE);
		notification = getIntent().getExtras().getSerializable("Notific");
		category = (String) getIntent().getExtras().getSerializable("category");
        GlobalAppState appState = (GlobalAppState) getApplication();
        httpConnection = new ServiceListener(appState);
        buttonupdate = (Button)findViewById(R.id.buttonupdate);
        foodPref = new ArrayList<FoodPreferenceSelection>();
        addPreferences();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	private void addPreferences() {
		List<FoodTypeDto> notifications, foodTypeNotifications;
        GsonBuilder gsonBuilder = new GsonBuilder();
		notificationText = (String) notification;
		Gson gson = gsonBuilder.create();
		settingsContent = new SettingsDTO();
		settingsContent = gson.fromJson(notificationText, SettingsDTO.class);
		notifications = settingsContent.getFoodtypeDTOList();
		Log.e("FoodNotifi", notifications.toString());
		foodTypeNotifications = Arrays.asList(gson.fromJson(category,
				FoodTypeDto[].class));
		for (int j = 0; j < foodTypeNotifications.size(); j++) {
			FoodPreferenceSelection food = new FoodPreferenceSelection();
			food.setId(foodTypeNotifications.get(j).getId());
			food.setType(foodTypeNotifications.get(j).getType());
			food.setChecked(getChecked(foodTypeNotifications.get(j).getType()));
			foodPref.add(food);
		}
        showList();

	}
 private void showList(){
     ListView prefList = (ListView) findViewById(R.id.list_data);
     prefList.setAdapter(new FoodPrefAdapter(foodPref,this));
         buttonupdate.setText("Update");
         buttonupdate.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 updateSettings();
             }
         });

 }
	private boolean getChecked(FoodType foodType) {
		for (int i = 0; i < settingsContent.getFoodtypeDTOList().size(); i++) {
			if (foodType == settingsContent.getFoodtypeDTOList().get(i)
					.getType()) {
				return true;
			}
		}
		return false;
	}
 public void savePreferences(List<FoodPreferenceSelection>notificationPreference){
     foodPref = notificationPreference;
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
        switch (what){
            case GET_FOODCATEGORY:
                progressBar.dismiss();
                drawCusineBackground(message);
                break;
            default:
                    super.onBackPressed();
        }
	}
    private void drawCusineBackground(Bundle data) {
        if(networkChanges()){
            String response = data.getString(ServiceListener.RESPONSEDATA);
            Bundle bundle = new Bundle();
            bundle.putSerializable("cusine", response);
            bundle.putSerializable("Notific", notificationText);
            Intent settings = new Intent(this, CusinePreferenceActivity.class);
            settings.putExtras(bundle);
            startActivitiesUser(settings,this);}
            finish();

    }
	private void updateSettings() {

		String url = "settings/updatefoodtype";
		try {
			List<FoodTypeDto> notifications = new ArrayList<FoodTypeDto>();
			for (FoodPreferenceSelection foodSelection : foodPref) {
				if (foodSelection.isChecked()) {
					FoodTypeDto food = new FoodTypeDto();
					food.setId(foodSelection.getId());
					food.setType(foodSelection.getType());
					notifications.add(food);
				}
			}
            if(notifications.size()==0){
                Util.customToast(this,"Please select at least one foodtype");
                return;
            }
            progressBar.show();
			settingsContent = new SettingsDTO();
			settingsContent.setCustomerId(appState.getProfile().getId());
			settingsContent.setFoodtypeDTOList(notifications);
			Log.e("Food", notifications.toString());
			String notificDeals = new Gson().toJson(settingsContent);
			StringEntity se = new StringEntity(notificDeals, HTTP.UTF_8);
			httpConnection.sendRequest(url, null,
					ServiceListenerType.NOTIFI_PREF_EDIT, SyncHandler, "PUT",
					se);
		} catch (Exception e) {
			Log.e("Error", e.toString(), e);
		}

	}


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String data = gson.toJson(foodPref);
        savedInstanceState.putSerializable("values",data);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Gson gson = new Gson();
        String values =  (String)savedInstanceState.getSerializable("values");
        foodPref =Arrays.asList(gson.fromJson(values,
                FoodPreferenceSelection[].class));
        showList();
    }
}
