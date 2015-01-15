package com.HungryBells.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class ContentDealDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    MerchantDto merchant;

    String name;

    String contentType;//Promotional OR Non-promotional

    ContentTemplate contentTemplate;
/*  1) Text only ( Text + Text),
 *  2) Image Only ( Image+image),
 *  3) Video only ( IMAGE OF VIDEO + video),
 *  4) Video + embedded text,
 *  5) Text + Image&Text*/

    String thumbnailText; // contentTemplate -1

    String detailText;  // contentTemplate -1

    String thumbNailURL;

    String displayURL;

    String videoURL;

    String startDate;

    String endDate;

    Integer viewCount;

    String createdDate; //Content Created Date

    String modifiedDate; //Last Modified Date

    Object location;

    Integer tileSize;

    DealStatus status;

    List<Customers> customer;

    Boolean isliked;

    List<AdFeedbackDTO> feedbacks;

    List<AdViewsDTO> views;

    Integer likeCount;

    Integer feedBackCount;
}
