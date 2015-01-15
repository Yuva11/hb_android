package com.HungryBells.util;

import java.io.Serializable;

import lombok.Data;

@Data
public class TermsConditions implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String termkey;
	private String value;


}
