package com.HungryBells.DTO;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Customers implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    String createdDate;

    String modifiedDate;

    String firstName, lastName;

    String email;

    String address;

    String mobileNumber;
    String dob;
    String authenticationId;

    @SuppressWarnings({"unchecked", "rawtypes"})
    Set<CusineNotification> favouriteCusines = new HashSet();

    Set<MerchantBranchDto> favouriteRestaurant = new HashSet<MerchantBranchDto>();

    User user;

    Boolean status;
    List<FoodTypeDto> favouriteFoodtype = new ArrayList<FoodTypeDto>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public Set<CusineNotification> getFavouriteCusines() {
        return favouriteCusines;
    }

    public void setFavouriteCusines(Set<CusineNotification> favouriteCusines) {
        this.favouriteCusines = favouriteCusines;
    }

    public Set<MerchantBranchDto> getFavouriteRestaurant() {
        return favouriteRestaurant;
    }

    public void setFavouriteRestaurant(Set<MerchantBranchDto> favouriteRestaurant) {
        this.favouriteRestaurant = favouriteRestaurant;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<FoodTypeDto> getFavouriteFoodtype() {
        return favouriteFoodtype;
    }

    public void setFavouriteFoodtype(List<FoodTypeDto> favouriteFoodtype) {
        this.favouriteFoodtype = favouriteFoodtype;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Customers create(String serializedData) {
        Gson gson = new Gson();
        return gson.fromJson(serializedData, Customers.class);
    }

}
