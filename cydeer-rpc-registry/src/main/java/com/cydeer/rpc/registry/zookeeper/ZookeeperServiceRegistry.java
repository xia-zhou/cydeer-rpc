package com.cydeer.rpc.registry.zookeeper;

import com.cydeer.rpc.registry.ServiceRegistry;
import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangsong on 2017/6/12.
 */

public class ZookeeperServiceRegistry implements ServiceRegistry,InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

	/**
	 * zookeeper 地址
	 */
	private String connectString;
	/**
	 * 默认连接超时时间
	 */
	private int sessionTimeout = 5000;

	private CountDownLatch latch = new CountDownLatch(1);

	@Override public void registry(String serviceName,String serviceAddress) {
		if(StringUtils.isEmpty(serviceName)){
			throw new IllegalArgumentException("registry serviceName is null");
		}
		ZooKeeper zooKeeper = connectServer();
		if(zooKeeper!=null){
			createNode(zooKeeper,serviceName,serviceAddress);
			LOGGER.info("Service name :{},service address  :{} ,registry to :{}  success ",serviceName,serviceAddress,connectString);
		}
	}

	/**
	 * 创建服务提供者子节点，临时有序节点
	 * @param zooKeeper
	 * @param serviceName
	 */
	private void createNode(ZooKeeper zooKeeper, String serviceName,String serviceAddress) {
		String path = ZookeeperConst.rootPath+"/"+serviceName+ZookeeperConst.providerPath;
		try {
			if(zooKeeper.exists(path,false)==null){
				zooKeeper.create(path,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
			zooKeeper.create(path+"/"+serviceAddress,serviceAddress.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接zookeeper
	 * @return
	 */
	private ZooKeeper connectServer() {
		ZooKeeper zooKeeper = null;
		try {
			zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				@Override public void process(WatchedEvent event) {
					if(Event.KeeperState.SyncConnected.equals(event.getState())){
						LOGGER.info("zookeeper connect to :{} success",connectString);
						latch.countDown();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return zooKeeper;
	}

	@Override public void afterPropertiesSet() throws Exception {
		if(StringUtils.isEmpty(connectString)){
			throw new IllegalArgumentException("class:ZookeeperServiceRegistry start param connectString is not set");
		}
	}

	public String getConnectString() {
		return connectString;
	}

	public void setConnectString(String connectString) {
		this.connectString = connectString;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
}
