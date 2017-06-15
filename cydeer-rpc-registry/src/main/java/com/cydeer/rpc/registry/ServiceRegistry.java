package com.cydeer.rpc.registry;

/**
 * Created by zhangsong on 2017/6/2.
 */
public interface ServiceRegistry {

	/**
	 * 服务提供者注册服务
	 * @param serviceName
	 */
	void registry(String serviceName,String serviceAddress);
}
