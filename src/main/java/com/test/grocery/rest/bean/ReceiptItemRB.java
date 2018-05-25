package com.test.grocery.rest.bean;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceiptItemRB {
	private String description;
	private BigDecimal quantity;
	private BigDecimal price;
}
