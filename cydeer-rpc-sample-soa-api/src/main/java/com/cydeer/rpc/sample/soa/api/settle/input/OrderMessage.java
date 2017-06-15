package com.cydeer.rpc.sample.soa.api.settle.input;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by zhangsong on 2017/6/13.
 */
public class OrderMessage implements Serializable {

	private Integer orderId;

	private Integer businessId;

	private BigDecimal orderAmount;

	private BigDecimal quantity;

	private String serviceName;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	public BigDecimal getOrderAmount() {
		return orderAmount;
	}

	public void setOrderAmount(BigDecimal orderAmount) {
		this.orderAmount = orderAmount;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"orderId\":").append(orderId);
		sb.append(", \"businessId\":").append(businessId);
		sb.append(", \"orderAmount\":").append(orderAmount);
		sb.append(", \"quantity\":").append(quantity);
		sb.append(", \"serviceName\":\"").append(serviceName).append('\"');
		sb.append('}');
		return sb.toString();
	}
}
