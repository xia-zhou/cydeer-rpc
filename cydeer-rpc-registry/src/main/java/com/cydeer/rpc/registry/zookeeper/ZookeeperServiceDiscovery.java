package com.cydeer.rpc.registry.zookeeper;

import com.cydeer.rpc.registry.ServiceDiscovery;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhangsong on 2017/6/12.
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery {

	private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

	private String connectString;

	private int sessionTimeout = 5000;

	private List<String> providers = new CopyOnWriteArrayList<>();

	private CountDownLatch latch = new CountDownLatch(1);
	@Override public String lookup(String serviceName,String localAddress) {
		if(StringUtils.isEmpty(serviceName)){
			throw new IllegalArgumentException("lookup service param is null ");
		}
		ZooKeeper zooKeeper =  connectServer();
		if(zooKeeper!=null){
			String serviceAddress = lookup0(zooKeeper,serviceName,localAddress);
			LOGGER.info("get service address from registry:{},service name :{} ,service address is :{} ",connectString,serviceName,serviceAddress);
			return serviceAddress;
		}
		return null;
	}

	private String lookup0(ZooKeeper zooKeeper, String serviceName,String localAddress) {
		String basePath= ZookeeperConst.rootPath+"/"+serviceName;
		String path = basePath+ZookeeperConst.consumerPath;
		try {
			if(zooKeeper.exists(path,false)==null){
				zooKeeper.create(path,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
			}
			zooKeeper.create(path+"/"+localAddress,localAddress.getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
			watchNode(zooKeeper,basePath + ZookeeperConst.providerPath);
			return providers.get(0);
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void watchNode(final ZooKeeper zooKeeper, final String path) {
		try {
			List<String> nodes =  zooKeeper.getChildren(path, new Watcher() {
				@Override public void process(WatchedEvent event) {
					if(event.getType().equals(Event.EventType.NodeChildrenChanged)) {
						watchNode(zooKeeper, path);
					}
				}
			});
			for(String node:nodes){
				try {
					String url = new String(zooKeeper.getData(path+"/"+node,false,null),"utf-8");
					providers.add(url);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private ZooKeeper connectServer() {
		ZooKeeper zooKeeper =  null;
		try {
			zooKeeper = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
				@Override public void process(WatchedEvent event) {
					if(Event.KeeperState.SyncConnected.equals(event.getState())){
							LOGGER.info("connect to address:{} success", connectString);
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

