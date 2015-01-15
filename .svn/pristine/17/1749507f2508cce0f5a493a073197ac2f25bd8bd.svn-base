package com.HungryBells.DTO;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class MerchantDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 633597833336444989L;
    Long id;

    String name; //Brand name should be unique - AlphaNumeric & symbols[75]

    Object category; //Merchant serves Different Categories

    Object speciality;//Merchant can prepare many kind of cusines

    boolean partyDealAllowed; //Party Allowed or not

    String code;

    Status status; //Is the Merchant is Active or not

    String webUrl;

    String logoUrl;

    List<MerchantBranchDto> branches; // Merchant Can Have many Branches

    //Bank Details
    Object bank;

    String bankBranch;

    String bankIFSC;

    String bankAccno; //Merchant Account Number

    boolean deletestatus;

    String createdDate;

    Object groupentity;

    User createdBy;

    boolean checkImage=false;



}
