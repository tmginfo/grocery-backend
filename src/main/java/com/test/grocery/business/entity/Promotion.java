package com.test.grocery.business.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Promotion {
	String id;
	String description;
	Product affectedProduct;
	PromotionType tpye;
}
