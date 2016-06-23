package com.qijiabin.core.server;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.qijiabin.core.common.RpcRequest;
import com.qijiabin.core.common.RpcResponse;
import com.qijiabin.core.registry.ServiceRegistry;
import com.qijiabin.core.rpc.RpcDecoder;
import com.qijiabin.core.rpc.RpcEncoder;
import com.qijiabin.core.util.LocalNetWorkIpResolve;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:32:05
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 服务器（用于发布 RPC 服务）
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class RpcServer implements  InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

	private String service;
	private String serviceVersion;
	private String servicePort;
	private String serviceWeight;
    private ServiceRegistry serviceRegistry;
    private String serviceInterface;
    private String serialize;
    private Map<String, Object> handlerMap = new HashMap<String, Object>();
    
    
    public RpcServer(String service, String serviceVersion, String servicePort, String serviceWeight,
			ServiceRegistry serviceRegistry, String serialize) {
		super();
		this.service = service;
		this.serviceVersion = serviceVersion;
		this.servicePort = servicePort;
		this.serviceWeight = serviceWeight;
		this.serviceRegistry = serviceRegistry;
		this.serialize = serialize;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void afterPropertiesSet() throws Exception {
		Class serviceClass = Class.forName(service);
		Class[] interfaces = serviceClass.getInterfaces();
		for (Class clazz : interfaces) {
			serviceInterface = clazz.getName();
			handlerMap.put(serviceInterface, serviceClass.newInstance());
			break;
		}
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				buildService();
			}
		}).start();
	}
	
	private void buildService() {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast(new RpcDecoder(RpcRequest.class, serialize))
					.addLast(new RpcEncoder(RpcResponse.class, serialize))
					.addLast(new RpcServerHandler(handlerMap));
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			final String host = LocalNetWorkIpResolve.getServerIp();
			int port = Integer.parseInt(servicePort);
			try {
				final ChannelFuture future = bootstrap.bind(host, port).sync();
				LOGGER.debug(">>>server started on port {}", servicePort);
				
				// 服务注册
				if (serviceRegistry != null) {
					serviceRegistry.register(serviceInterface, serviceVersion, host + ":" + servicePort + ":" + serviceWeight);
				}
				
				// 优雅退出
				Runtime.getRuntime().addShutdownHook(new Thread() {
		            public void run() {  
		                try {  
		                	if(serviceRegistry != null) {
		                		serviceRegistry.unregister(serviceInterface, serviceVersion, host + ":" + servicePort + ":" + serviceWeight);
		                		serviceRegistry.close();
		        			}
		                	Thread.sleep(1000 * 5);  
		                	future.cancel(true);
		                } catch (Exception e) {  
							e.printStackTrace();
						}
		            }
		        });		
				
				future.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}

}

