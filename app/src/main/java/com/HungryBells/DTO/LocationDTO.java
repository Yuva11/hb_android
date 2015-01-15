package com.HungryBells.DTO;

import java.io.Serializable;

import lombok.Data;

@Data
public class LocationDTO implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Long id;

    String name;

    String createdDate;

    Object user;

    String lastUpdate;

    boolean deletestatus;

    Double latitude;

    Double longitude;


}
