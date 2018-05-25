package com.test.grocery.rest.service;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.test.grocery.business.entity.Product;
import com.test.grocery.business.entity.Promotion;
import com.test.grocery.business.entity.Receipt;
import com.test.grocery.business.entity.ReceiptItem;
import com.test.grocery.business.exception.ElementNotFoundException;
import com.test.grocery.business.exception.InvalidQuantityException;
import com.test.grocery.business.service.GroceryBusinessService;
import com.test.grocery.rest.bean.PromotionRB;
import com.test.grocery.rest.bean.ReceiptItemRB;
import com.test.grocery.rest.bean.ReceiptRB;
import com.test.grocery.rest.bean.ScanRB;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class GroceryRestService {
	
	private final GroceryBusinessService businessService;

	public GroceryRestService(GroceryBusinessService businessService) {
		super();
		this.businessService = businessService;
	}
	
	
    @GET
    @Path("/receipt")
    public Response createReceipt() {
        	ReceiptRB receiptRB = new ReceiptRB();
        	receiptRB.setId(businessService.newReceipt().getId());
            return Response.ok(receiptRB).build();
    }
    
    @GET
    @Path("/receipt/{id}")
    public Response getReceipt(@PathParam("id") String id) {
        try {      	        	
        	ReceiptRB receiptRB = mapReceipt(businessService.getReceiptById(id));
            return Response.ok(receiptRB).build();
            
        } catch (ElementNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }
    
    @GET
    @Path("/product/{barcode}")
    public Response scanProduct(@PathParam("barcode") String barcode) {
        try {
        	ScanRB scanRB = new ScanRB();
        	
        	Product product = businessService.getProductByBarcode(barcode);
        	scanRB.setBarcode(barcode);
        	scanRB.setName(product.getName());
        	scanRB.setPrice(product.getUnitPrice());
        	scanRB.setUnit(product.getPricingUnit().name());
        	
        	Promotion promotion = businessService.getPromotionByProductId(product.getId());
        	scanRB.setPromotion(mapPromotion(promotion));
        	
            return Response.ok(scanRB).build();
        } catch (ElementNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        }
    }
    
    @POST
    @Path("/receipt/{id}/product/{barcode}/quantity/{quantity}")
    public Response addReceiptItem(@PathParam("id") String id, @PathParam("barcode") String barcode, @PathParam("quantity") BigDecimal quantity) {
        try {
            return Response.ok(mapReceipt(businessService.addReceiptItem(id, barcode, quantity))).build();
        } catch (ElementNotFoundException ex) {
            return Response.status(Response.Status.NOT_FOUND).entity(ex.getMessage()).build();
        } catch (InvalidQuantityException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    private ReceiptRB mapReceipt(Receipt receipt) {
    	
    	ReceiptRB receiptRB = new ReceiptRB();
    	if(receipt==null) return receiptRB;
	    
    	receiptRB.setId(receipt.getId());        	
		receiptRB.setTotalPrice(receipt.getTotalPrice());        	
		receiptRB.setTotalDiscount(receipt.getTotalDiscount());
		receiptRB.setItems(receipt.getItems().stream().map(this::mapReceiptItem).collect(Collectors.toList()));
		receiptRB.setPromotions(receipt.getPromotions().stream().map(this::mapPromotion).collect(Collectors.toList()));
		
		return receiptRB;
    }
    
    private ReceiptItemRB mapReceiptItem(ReceiptItem item) {
    	
    	ReceiptItemRB rb = new ReceiptItemRB();
    	if(item==null) return rb;
    	
    	rb.setDescription(item.getProduct().getName());
    	rb.setPrice(item.getPrice());
    	rb.setQuantity(item.getQuantity());
    	
    	return rb;
    }

    private PromotionRB mapPromotion(Promotion promotion) {
    	
    	PromotionRB rb = new PromotionRB();
    	if(promotion==null) return rb;
    	
    	rb.setDescription(promotion.getDescription());
    	rb.setType(promotion.getTpye().name());
    	
    	return rb;
    }
    
}
