package com.test.grocery.rest.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ScanRB {
	private String barcode;
	private String name;
	private String unit;
	private BigDecimal price;
	private PromotionRB promotion;
}
