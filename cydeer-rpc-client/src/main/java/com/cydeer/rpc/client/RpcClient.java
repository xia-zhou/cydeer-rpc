package com.cydeer.rpc.client;

import com.cydeer.rpc.common.bean.RpcRequest;
import com.cydeer.rpc.common.bean.RpcResponse;
import com.cydeer.rpc.common.protostuff.codec.RpcDecoder;
import com.cydeer.rpc.common.protostuff.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhangsong on 2017/6/13.
 */
public class RpcClient {
private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);
	private String address;
	private int port;

	public RpcClient(String address, int port) {
		this.address = address;
		this.port = port;
	}
	public RpcResponse send(final RpcRequest rpcRequest) {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		final RpcResponse[] rpcResponses = new RpcResponse[1];
		bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
		         .option(ChannelOption.SO_BACKLOG, 1024)
		         .option(ChannelOption.TCP_NODELAY, true)
		         .handler(new ChannelInitializer<SocketChannel>() {
			         @Override protected void initChannel(SocketChannel ch) throws Exception {
				         ch.pipeline()
				           .addLast(new RpcDecoder(RpcResponse.class))
				           .addLast(new RpcEncoder(RpcRequest.class))
				           .addLast(new ChannelHandlerAdapter() {
					           @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
						           LOGGER.info("send request from client:{}",rpcRequest);
						           ctx.writeAndFlush(rpcRequest);
					           }

					           @Override public void channelRead(ChannelHandlerContext ctx, Object msg)
							           throws Exception {
						           RpcResponse rpcResponse = (RpcResponse) msg;
						           LOGGER.info("get response result :{}",msg);
						           rpcResponses[0] = rpcResponse;
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
			channelFuture = bootstrap.connect(address, port).sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			channelFuture.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return rpcResponses[0];
	}

}
