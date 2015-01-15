package com.HungryBells.DTO;

import java.io.Serializable;

import lombok.Data;

/**
 * Created by Arunkumar on 28/11/2014.
 */
@Data
public class VersonDetailsDTO implements Serializable {
    Long id;
    String versionName;
    Integer versionCode;
    String releaseDate;
    boolean upgrade;
    String apkURL;
}
