package com.cydeer.rpc.registry;

/**
 * Created by zhangsong on 2017/6/2.
 */
public interface ServiceDiscovery {

	/**
	 * 服务使用者发现服务
	 * @param serviceName
	 * @return
	 */
	String lookup(String serviceName,String localAddress);
}
