package com.HungryBells.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.HungryBells.DTO.ServiceListenerType;
import com.HungryBells.DTO.UserLocationDTO;
import com.HungryBells.activity.adapter.PlaceListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Arunkumar on 02/12/2014.
 */
public class SearchCityActivity extends UserActivity implements AdapterView.OnItemClickListener {

    ListView listPlace;
    EditText placeSearchText;
    List<UserLocationDTO> array_sort = new ArrayList<UserLocationDTO>();
    int textlength = 0;
    List<UserLocationDTO> allLocations = new ArrayList<UserLocationDTO>();
    PlaceListAdapter placeAdapter;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.fragment_search);
        appState = (GlobalAppState) getApplication();
        if (appState != null && appState.getCity() != null && appState.getUrl() != null) {
            actionBar = getActionBar();
            actionBar.setTitle("");
            LayoutInflater mInflater = LayoutInflater.from(this);
            View mCustomView = mInflater.inflate(R.layout.actionbar_custom,
                    null);
            android.util.Log.e("Action bar ", "Action bar created:"
                    + new Date());
            actionBar.setCustomView(mCustomView);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            String location = appState.getCity();
            android.util.Log.e("Inside Search Locations", "Location data");

            ((TextView) findViewById(R.id.textViewlocation)).setText(location);
            ImageView img = (ImageView) findViewById(R.id.imageViewicaction);
            img.setVisibility(View.GONE);
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listViewCreation();
    }

    private void listViewCreation() {
        listPlace = (ListView) findViewById(R.id.ListView01);

        placeSearchText = (EditText) findViewById(R.id.EditText01);
        allLocations = appState.getUserLocations();
        if (allLocations == null) {
            allLocations = new ArrayList<UserLocationDTO>();
        }
        if (allLocations.size() > 0)
            if (allLocations.get(0).getName().contains("My Location")) {
                allLocations.remove(0);
            }
        UserLocationDTO locationNow = new UserLocationDTO();
        locationNow.setName("My Location (" + appState.getCity() + ")");
        allLocations.add(0, locationNow);
        // placeSearchText.setText("");
        listPlace.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        placeAdapter=new PlaceListAdapter(this, allLocations);
        listPlace.setAdapter(placeAdapter);
        listPlace.setSelector(getResources().getDrawable(R.drawable.listviewselector));
       /* listPlace.setOnItemClickListener(this);*/
        placeSearchText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                textlength = placeSearchText.getText().length();
                array_sort.clear();
                for (int i = 0; i < allLocations.size(); i++) {
                    if (textlength <= allLocations.get(i).getName().length()) {
                        if (placeSearchText
                                .getText()
                                .toString()
                                .equalsIgnoreCase(
                                        (String) allLocations.get(i).getName()
                                                .subSequence(0, textlength))) {
                            array_sort.add(allLocations.get(i));

                        }
                    }
                }
                if (array_sort.size() == 0) {
                    UserLocationDTO locations = new UserLocationDTO();
                    locations.setName("My Location (" + appState.getCity()
                            + ")");
                    array_sort.add(locations);
                }
                placeAdapter =new PlaceListAdapter(SearchCityActivity.this, array_sort);
                listPlace.setAdapter(placeAdapter);

            }
        });

    }

    @Override
    public void onUndo(Parcelable token) {

    }

    @Override
    public void processMessage(Bundle message, ServiceListenerType what) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                .getWindowToken(), 0);
        listPlace.setItemChecked(position, true);
        view.setBackgroundColor(Color.BLUE);
        placeAdapter.notifyDataSetChanged();
        if (array_sort.size() > 0) {

            searchByLocation(array_sort.get(position),
                    this);
        } else {
            searchByLocation(
                    allLocations.get(position), this);
        }

    }

}
