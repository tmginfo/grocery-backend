package com.test.grocery.business.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import com.test.grocery.business.entity.PricingUnit;
import com.test.grocery.business.entity.Product;
import com.test.grocery.business.entity.Promotion;
import com.test.grocery.business.entity.PromotionType;
import com.test.grocery.business.entity.Receipt;
import com.test.grocery.business.entity.ReceiptItem;
import com.test.grocery.business.exception.ElementNotFoundException;
import com.test.grocery.business.exception.InvalidQuantityException;

public class GroceryBusinessService {

	private Map<String, Receipt> receiptDB;
	private Map<String, Product> productDB;
	private Map<String, Promotion> promotionDB;
	
	public GroceryBusinessService() {
		receiptDB = new HashMap<>();
		
		productDB = new HashMap<>();
		Product p1 = new Product(UUID.randomUUID().toString(), "11111111", "Product1", PricingUnit.PIECE, new BigDecimal(100));
		Product p2 = new Product(UUID.randomUUID().toString(), "22222222", "Product2", PricingUnit.PIECE, new BigDecimal(200));
		Product p3 = new Product(UUID.randomUUID().toString(), "33333333", "Product3", PricingUnit.KILOGRAMM, new BigDecimal(300));
		productDB.put(p1.getBarcode(), p1);
		productDB.put(p2.getBarcode(), p2);
		productDB.put(p3.getBarcode(), p3);
		
		promotionDB = new HashMap<>();
		Promotion promo1 =  new Promotion(UUID.randomUUID().toString(), "Sale for Product 1", p1, PromotionType.BUY2GET1);
		Promotion promo2 =  new Promotion(UUID.randomUUID().toString(), "Super Sale for Product 2", p2, PromotionType.BUY1GET1);
		promotionDB.put(p1.getId(), promo1);
		promotionDB.put(p2.getId(), promo2);
		
	}

	public Receipt newReceipt() {
		Receipt receipt = new Receipt();
		
		receipt.setItems(new ArrayList<>());
		receipt.setPromotions(new HashSet<>());
		receipt.setTotalPrice(BigDecimal.ZERO);
		receipt.setTotalDiscount(BigDecimal.ZERO);
		receipt.setId(UUID.randomUUID().toString());
		
		receiptDB.put(receipt.getId(), receipt);
		return receipt;
	}
	
	public Product getProductByBarcode(String barcode) {
		if(productDB.containsKey(barcode)){
			return productDB.get(barcode);
		}else{
			throw new ElementNotFoundException("Product not found! Barcode: " + barcode);
		}
	}
	
	public Receipt addReceiptItem(String receiptId, String barcode, BigDecimal quantity) {
		if(!receiptDB.containsKey(receiptId)) {
			throw new ElementNotFoundException("Receipt not found! Id: " + receiptId);
			
		}
		
		if(!productDB.containsKey(barcode)){
			throw new ElementNotFoundException("Product not found! Barcode: " + barcode);
		}

		Receipt receipt = receiptDB.get(receiptId);

		receipt.getItems().add(createReceiptItem(barcode, quantity));
		calculateTotalPriceAndDiscount(receipt);
		
		return receipt;
		
		
	}
	
	public Receipt getReceiptById(String receiptId) {
		if(receiptDB.containsKey(receiptId)) {
			return receiptDB.get(receiptId);
		}else{
			throw new ElementNotFoundException("Receipt not found! Id: " + receiptId);
		}
	}
	
	public Promotion getPromotionByProductId(String productId) {
		return promotionDB.get(productId);
	}
	
	private ReceiptItem createReceiptItem(String barcode, BigDecimal quantity) {
		
		Product product = productDB.get(barcode);
		
		if(product.getPricingUnit()==PricingUnit.PIECE && quantity.scale()>0){
			throw new InvalidQuantityException("Quantity of piece must be whole number! Barcode: " + barcode+" Quantity:"+quantity.toString());
		}
		
		ReceiptItem item = new ReceiptItem();
		item.setProduct(product);
		item.setQuantity(quantity);
		item.setPrice(product.getUnitPrice().multiply(quantity));

		return item;
	}

	private void calculateTotalPriceAndDiscount(Receipt receipt) {
		
		//calculate grand total for all items and collect the affected promotions			
		BigDecimal totalPrice = BigDecimal.ZERO;
		for(ReceiptItem item : receipt.getItems()) {
			totalPrice = totalPrice.add(item.getPrice());
			
			//check for promotion
			Promotion promotion = promotionDB.get(item.getProduct().getId());					
			if(promotion!=null) {
				receipt.getPromotions().add(promotion);
			}
		}
		
		BigDecimal discount = BigDecimal.ZERO;
		//calculate discount for affected promotions
		for(Promotion promotion : receipt.getPromotions()) {
			
			int itemCount = 0;
			for(ReceiptItem item : receipt.getItems()) {
				if(promotion.getAffectedProduct().getId().equals(item.getProduct().getId())) {
					itemCount=itemCount+item.getQuantity().intValue();
				}
			}
			
			//calculate number of 'free' product
			int freeCount= 0;
			if(itemCount>0) { 
				if(promotion.getTpye() == PromotionType.BUY1GET1) {
				 freeCount=itemCount/2;
				}else if(promotion.getTpye() == PromotionType.BUY2GET1) {
				 freeCount=itemCount/3;	
				}
			}
			
			discount=discount.add(promotion.getAffectedProduct().getUnitPrice().multiply(new BigDecimal(freeCount)));
			
		}
		
		//calculate total price
		receipt.setTotalPrice(totalPrice.subtract(discount).setScale(0, RoundingMode.HALF_UP));
		receipt.setTotalDiscount(discount.setScale(0, RoundingMode.HALF_UP));
		
	}
	
}
