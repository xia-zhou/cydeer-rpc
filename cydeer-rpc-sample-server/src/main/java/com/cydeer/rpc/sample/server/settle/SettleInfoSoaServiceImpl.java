package com.cydeer.rpc.sample.server.settle;

import com.cydeer.rpc.RpcService;
import com.cydeer.rpc.sample.soa.api.settle.SettleInfoSoaService;
import com.cydeer.rpc.sample.soa.api.settle.input.OrderMessage;
import com.cydeer.rpc.sample.soa.api.settle.output.OrderMessageSettleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by zhangsong on 2017/6/13.
 */
@RpcService(SettleInfoSoaService.class)
public class SettleInfoSoaServiceImpl implements SettleInfoSoaService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleInfoSoaServiceImpl.class);
	@Override public OrderMessageSettleResult settle(OrderMessage orderMessage) {
		LOGGER.info("settle param:{}",orderMessage);
		OrderMessageSettleResult result = new OrderMessageSettleResult();
		result.setOrderId(orderMessage.getOrderId());
		result.setSettleAmount(orderMessage.getOrderAmount().multiply(orderMessage.getQuantity()));
		result.setFormulaInfo(orderMessage.getOrderAmount()+"*"+orderMessage.getQuantity()+"="+result.getSettleAmount());
		result.setOrderMessageList(new ArrayList<>(Arrays.asList(orderMessage)));
		LOGGER.info("settle result :{}",result);
		return result;
	}

	@Override public String settleOrder(Integer orderId) {
		return "orderId:"+orderId;
	}

	@Override public String settleMessage(Integer orderId, Integer businessId, BigDecimal settleAmount) {
		return null;
	}
}
