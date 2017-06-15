package com.cydeer.rpc.sample.soa.api.settle;

import com.cydeer.rpc.sample.soa.api.settle.input.OrderMessage;
import com.cydeer.rpc.sample.soa.api.settle.output.OrderMessageSettleResult;

import java.math.BigDecimal;

/**
 * Created by zhangsong on 2017/6/13.
 */
public interface SettleInfoSoaService {
	OrderMessageSettleResult settle(OrderMessage orderMessage);

	String settleOrder(Integer orderId);

	String settleMessage(Integer orderId,Integer businessId,BigDecimal settleAmount);
}
