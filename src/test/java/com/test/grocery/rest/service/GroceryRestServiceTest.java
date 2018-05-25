package com.test.grocery.rest.service;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.test.grocery.GroceryAppTest;
import com.test.grocery.rest.bean.ReceiptRB;
import com.test.grocery.rest.bean.ScanRB;

import lombok.extern.java.Log;

@Log
public class GroceryRestServiceTest extends GroceryAppTest{

    @Test
    public void testCreateReceipt() throws Exception {
    	
    	HttpResponse<String> resp = get("/receipt");

    	assertEquals(200, resp.getStatus());
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	UUID.fromString(objectMapper.readValue(resp.getBody(), ReceiptRB.class).getId());
    }
    
    @Test
    public void testScanProduct() throws Exception {
    	
    	HttpResponse<String> resp = get("/product/11111111");

    	assertEquals(200, resp.getStatus());
    	
    	ObjectMapper objectMapper = new ObjectMapper();    	
    	ScanRB scanRB = objectMapper.readValue(resp.getBody(), ScanRB.class);    	
    	   	
    	assertEquals("11111111", scanRB.getBarcode());
    	assertEquals("Product1", scanRB.getName());
    	assertEquals("PIECE", scanRB.getUnit());
    	assertEquals(new BigDecimal(100), scanRB.getPrice());
    	assertEquals("Sale for Product 1", scanRB.getPromotion().getDescription());
    	assertEquals("BUY2GET1", scanRB.getPromotion().getType());
    	
    }
    
    @Test
    public void buyProduct() throws Exception {
    	
    	//Step 1. create receipt
    	HttpResponse<String> resp = get("/receipt");
    	assertEquals(200, resp.getStatus());    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	ReceiptRB receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	
    	//Step 2. buy product
    	resp = post("/receipt/" + receipt.getId()+"/product/11111111/quantity/1");
    	receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	    	
    	assertEquals("Product1", receipt.getItems().get(0).getDescription());
    	assertEquals(new BigDecimal(100), receipt.getItems().get(0).getPrice());
    	assertEquals(new BigDecimal(1), receipt.getItems().get(0).getQuantity());
    	assertEquals(new BigDecimal(100), receipt.getTotalPrice());
    	
    }
    
    @Test
    public void buyProductWithWrongQuantity() throws Exception {
    	
    	//Step 1. create receipt
    	HttpResponse<String> resp = get("/receipt");
    	assertEquals(200, resp.getStatus());    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	ReceiptRB receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	
    	//Step 2. buy product
    	resp = post("/receipt/" + receipt.getId()+"/product/11111111/quantity/1%2E5");    	    	    
    	assertEquals(400, resp.getStatus());
    	
    }
    
    @Test
    public void testShopping() throws Exception {
    	
    	//Step 1. create receipt
    	HttpResponse<String> resp = get("/receipt");
    	assertEquals(200, resp.getStatus());    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	ReceiptRB receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	
    	//Step 2. buy product 1.
    	resp = post("/receipt/" + receipt.getId()+"/product/11111111/quantity/3");
    	receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	    	
    	assertEquals("Product1", receipt.getItems().get(0).getDescription());
    	assertEquals(new BigDecimal(300), receipt.getItems().get(0).getPrice());
    	assertEquals(new BigDecimal(3), receipt.getItems().get(0).getQuantity());
    	assertEquals(new BigDecimal(200), receipt.getTotalPrice());
    	assertEquals(new BigDecimal(100), receipt.getTotalDiscount());
    	
    	//Step 3. buy product 2.
    	resp = post("/receipt/" + receipt.getId()+"/product/22222222/quantity/6");
    	receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	    	
    	assertEquals("Product2", receipt.getItems().get(1).getDescription());
    	assertEquals(new BigDecimal(800), receipt.getTotalPrice());
    	assertEquals(new BigDecimal(700), receipt.getTotalDiscount());
    	
    	//Step 4. buy product 1. once again
    	resp = post("/receipt/" + receipt.getId()+"/product/11111111/quantity/2");
    	receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	    	
    	assertEquals("Product1", receipt.getItems().get(2).getDescription());
    	assertEquals(new BigDecimal(1000), receipt.getTotalPrice());
    	assertEquals(new BigDecimal(700), receipt.getTotalDiscount());
    	
    	//Step 5. buy product 3.
    	resp = post("/receipt/" + receipt.getId()+"/product/33333333/quantity/1%2E5");
    	receipt = objectMapper.readValue(resp.getBody(), ReceiptRB.class);
    	    	
    	assertEquals("Product3", receipt.getItems().get(3).getDescription());
    	assertEquals(new BigDecimal(1450), receipt.getTotalPrice());
    	assertEquals(new BigDecimal(700), receipt.getTotalDiscount());
    	
    	//Step 5. getReceipt
    	resp = get("/receipt/"+ receipt.getId());
    	assertEquals(200, resp.getStatus());
    	log.info("***********Print Receipt*************");
    	log.info(resp.getBody());
    }
}
