package com.HungryBells.util;


import com.HungryBells.DTO.ContentTemplate;

import lombok.Data;

@Data
public class ItemUrl {
	String url;
	int height;
	ContentTemplate tileType;
	String text;
	Long id;


}
