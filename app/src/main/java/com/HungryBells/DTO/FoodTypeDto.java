package com.HungryBells.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class FoodTypeDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    FoodType type;


}
