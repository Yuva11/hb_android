package com.HungryBells.util;

import java.io.Serializable;
import java.util.List;
import com.HungryBells.DTO.Customers;
import com.HungryBells.DTO.MerchantDto;


public class OrderStatusDTO implements Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	Long id;
	Double amount;
	String status;
	Customers customer;
	MerchantDto merchant;
	boolean deliveryStatus;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    String orderId;
	String dealordersdate;
	private List<OrderDetailsDTO> orderDetails;

      public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public MerchantDto getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantDto merchant) {
        this.merchant = merchant;
    }

    public boolean isDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(boolean deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDealordersdate() {
        return dealordersdate;
    }

    public void setDealordersdate(String dealordersdate) {
        this.dealordersdate = dealordersdate;
    }

    public List<OrderDetailsDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailsDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
