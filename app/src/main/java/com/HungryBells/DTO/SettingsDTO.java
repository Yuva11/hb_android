package com.HungryBells.DTO;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SettingsDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long customerId;

    List<NotificationPreference> customerNotificationPreferenceDTOList;

    List<CusineNotification> cuisineDTOList;

    List<FoodTypeDto> foodtypeDTOList;


}