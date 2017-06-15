package com.cydeer.rpc.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by zhangsong on 2017/6/13.
 */
public class RpcBootStrap {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("spring.xml");
		try {
			Thread.sleep(1000*60*30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
