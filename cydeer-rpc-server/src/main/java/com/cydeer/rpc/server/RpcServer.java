package com.cydeer.rpc.server;

import com.cydeer.rpc.RpcService;
import com.cydeer.rpc.common.bean.RpcRequest;
import com.cydeer.rpc.common.bean.RpcResponse;
import com.cydeer.rpc.common.protostuff.codec.RpcDecoder;
import com.cydeer.rpc.common.protostuff.codec.RpcEncoder;
import com.cydeer.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangsong on 2017/6/12.
 */


public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);
	private ServiceRegistry serviceRegistry;

	private Map<String, Object> handleMap = new HashMap<>();

	public ServiceRegistry getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}
	/*public static void main(String[] args) {
		System.out.println(new RpcServer().getClass().getCanonicalName());
		*//*ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
		ServiceRegistry serviceRegistry = (ServiceRegistry) applicationContext.getBean("serviceRegistry");
		ServiceDiscovery serviceDiscovery = (ServiceDiscovery) applicationContext.getBean("serviceDiscovery");*//*
		// serviceRegistry.registry("com.song.a.test.HelloWord","localhost:8080");
	}*/

	@Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		Map<String, Object> providers = applicationContext.getBeansWithAnnotation(RpcService.class);
		if (CollectionUtils.isEmpty(providers)) {
			return;
		}
		for (Map.Entry<String, Object> entry : providers.entrySet()) {
			Object bean = entry.getValue();
			RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
			handleMap.put(rpcService.value().getCanonicalName(), bean);
		}
	}

	@Override public void afterPropertiesSet() throws Exception {
		bind();
	}

	public void bind() {
		EventLoopGroup masterEventLoopGroup = new NioEventLoopGroup();
		EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(masterEventLoopGroup, workEventLoopGroup).channel(NioServerSocketChannel.class)
		               .option(ChannelOption.SO_LINGER, 1024)
		               .option(ChannelOption.SO_KEEPALIVE, true)
		               .handler(new LoggingHandler(LogLevel.INFO))
		               .childHandler(new ChannelInitializer<SocketChannel>() {
			               @Override protected void initChannel(SocketChannel ch) throws Exception {
				               ch.pipeline()
				                 .addLast(new RpcDecoder(RpcRequest.class))
				                 .addLast(new RpcEncoder(RpcResponse.class))
				                 .addLast(new ChannelHandlerAdapter() {
					                 @Override public void channelRead(ChannelHandlerContext ctx, Object msg)
							                 throws Exception {
						                 LOGGER.info("get request from client :{}",msg);
						                 RpcRequest rpcRequest = (RpcRequest) msg;
						                 Object bean = handleMap.get(rpcRequest.getServiceName());
						                 RpcResponse rpcResponse = new RpcResponse();
						                 rpcResponse.setRequestId(rpcRequest.getRequestId());
						                 Class<?> serviceClass = bean.getClass();
						                 FastClass fastClass = FastClass.create(serviceClass);
						                 FastMethod fastMethod = fastClass
								                 .getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
						                 Object result = fastMethod.invoke(bean, rpcRequest.getParameters());
						                 rpcResponse.setResult(result);
						                 LOGGER.info("return response from server result :{}",rpcResponse);
						                 ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
					                 }

					                 @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
							                 throws Exception {
						                 super.exceptionCaught(ctx, cause);
					                 }
				                 });
			               }
		               });

		ChannelFuture channelFuture = null;
		try {
			channelFuture = serverBootstrap.bind(new InetSocketAddress("127.0.0.1", 8091)).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!StringUtils.isEmpty(handleMap)) {
			for (Map.Entry<String, Object> entry : handleMap.entrySet()) {
				serviceRegistry.registry(entry.getKey(), "127.0.0.1:8091");
			}
		}
		try {
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
