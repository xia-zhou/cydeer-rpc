package com.cydeer.rpc.sample.soa.api.settle.output;

import com.cydeer.rpc.sample.soa.api.settle.input.OrderMessage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhangsong on 2017/6/13.
 */
public class OrderMessageSettleResult implements Serializable {
	private Integer orderId;

	private BigDecimal settleAmount;

	private String formulaInfo;

	List<OrderMessage> orderMessageList;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getSettleAmount() {
		return settleAmount;
	}

	public void setSettleAmount(BigDecimal settleAmount) {
		this.settleAmount = settleAmount;
	}

	public String getFormulaInfo() {
		return formulaInfo;
	}

	public void setFormulaInfo(String formulaInfo) {
		this.formulaInfo = formulaInfo;
	}

	public List<OrderMessage> getOrderMessageList() {
		return orderMessageList;
	}

	public void setOrderMessageList(List<OrderMessage> orderMessageList) {
		this.orderMessageList = orderMessageList;
	}

	@Override public String toString() {
		final StringBuilder sb = new StringBuilder("{");
		sb.append("  \"orderId\":").append(orderId);
		sb.append(", \"settleAmount\":").append(settleAmount);
		sb.append(", \"formulaInfo\":\"").append(formulaInfo).append('\"');
		sb.append(", \"orderMessageList\":").append(orderMessageList);
		sb.append('}');
		return sb.toString();
	}
}
