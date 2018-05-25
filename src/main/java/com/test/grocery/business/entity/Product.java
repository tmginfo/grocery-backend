package com.test.grocery.business.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {

	private String id;
	private String barcode;
	private String name;
	private PricingUnit pricingUnit;
	private BigDecimal unitPrice;
	
}
