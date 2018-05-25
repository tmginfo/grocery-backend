package com.test.grocery.business.entity;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceiptItem {
	Product product;
	BigDecimal quantity;
	BigDecimal price;
}
