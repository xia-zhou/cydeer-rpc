package com.cydeer.rpc.sample.client;

import com.cydeer.rpc.client.RpcProxy;
import com.cydeer.rpc.sample.soa.api.settle.SettleInfoSoaService;
import com.cydeer.rpc.sample.soa.api.settle.input.OrderMessage;
import com.cydeer.rpc.sample.soa.api.settle.output.OrderMessageSettleResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;

/**
 * Created by zhangsong on 2017/6/13.
 */
public class RpcClientBootstrap {
	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		RpcProxy proxy = (RpcProxy) applicationContext.getBean("rpcProxy");
		SettleInfoSoaService settleInfoSoaService = proxy.create(SettleInfoSoaService.class);
		String result = settleInfoSoaService.settleOrder(123);
		System.out.println(result);

		OrderMessage orderMessage = new OrderMessage();
		orderMessage.setBusinessId(804);
		orderMessage.setOrderAmount(BigDecimal.TEN);
		orderMessage.setQuantity(BigDecimal.valueOf(3));
		orderMessage.setOrderId(78786878);
		orderMessage.setServiceName("精致洗车");
		OrderMessageSettleResult settleResult = settleInfoSoaService.settle(orderMessage);
		System.out.println(settleResult.getOrderMessageList());
		System.out.println(settleResult);

		System.out.println("fsdfsdfd");

		System.out.println("=-=======");
	}
}
