package com.HungryBells.DTO;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Data;

@SuppressWarnings("rawtypes")


@Data
public class Profile {
    @SerializedName("id")
    private long ID;
    private String createdDate;
    private String modifiedDate;
    private String firstName;
    private String lastName;

    private String email;
    private String address;
    private String mobileNumber;
    private List favouriteCusines;
    private List orders;

    private List notificationPreference;


    public Profile() {

    }

    public String serialize() {
        // Serialize this class into a JSON string using GSON
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Profile create(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, Profile.class);
    }
}
