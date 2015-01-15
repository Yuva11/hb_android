package com.HungryBells.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class MerchantBranchDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;
    String mobileNumber;

    String eMail;

    MerchantDto merchant; // this Branch belongs to which Merchant

    String branchName; //Name of the Branch

    String address;

    long pincode;

    Integer rating;

    String startTime;//Merchant Branch start time

    String endTime;//Merchant Branch end time

    CityDTO city;

    LocationDTO location;

    Double lattitue,longitude; // Latitude and Longitude of the Branch

    Status status;	//Is the Branch is Active or Blocked

    List<Deals> deals; //Deals Created by this branch

    boolean deletestatus;

    User createdBy;

    String createdDate;


}
