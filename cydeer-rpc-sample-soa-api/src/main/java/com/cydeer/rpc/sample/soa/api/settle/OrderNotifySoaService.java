package com.cydeer.rpc.sample.soa.api.settle;


/**
 * Created by zhangsong on 2017/6/13.
 */
public interface OrderNotifySoaService {
	boolean notify(Integer orderId);
}
