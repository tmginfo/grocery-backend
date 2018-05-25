package com.test.grocery.business.entity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class Receipt {

	private String id;
	private List<ReceiptItem> items;
	private BigDecimal totalPrice;
	private BigDecimal totalDiscount;
	private Set<Promotion> promotions;
}
