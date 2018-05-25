package com.test.grocery.rest.bean;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class ReceiptRB {
	private String id;
	private BigDecimal totalPrice;
	private BigDecimal totalDiscount;
	private List<ReceiptItemRB> items;
	private List<PromotionRB> promotions;
}
