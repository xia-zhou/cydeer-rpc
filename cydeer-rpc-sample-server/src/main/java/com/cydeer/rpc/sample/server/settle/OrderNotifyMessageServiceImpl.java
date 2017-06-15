package com.cydeer.rpc.sample.server.settle;

import com.cydeer.rpc.RpcService;
import com.cydeer.rpc.sample.soa.api.settle.OrderNotifySoaService;

/**
 * Created by zhangsong on 2017/6/13.
 */
@RpcService(value = OrderNotifySoaService.class)
public class OrderNotifyMessageServiceImpl implements OrderNotifySoaService{
	@Override public boolean notify(Integer orderId) {
		return false;
	}
}
