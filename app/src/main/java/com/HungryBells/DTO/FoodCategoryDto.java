package com.HungryBells.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class FoodCategoryDto implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    String name;

    String createdDate;

    User user;

    String lastUpdate;

    boolean deletestatus;


}
