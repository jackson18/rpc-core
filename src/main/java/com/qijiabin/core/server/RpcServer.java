package com.qijiabin.core.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.qijiabin.core.annotation.RpcService;
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
public class RpcServer implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private ServiceRegistry serviceRegistry;
    private Map<String, List<Object>> handlerMap = new HashMap<String, List<Object>>();
    
    
    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        try {
			Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
			if (MapUtils.isNotEmpty(serviceBeanMap)) {
				for (Map.Entry<String, Object> service : serviceBeanMap.entrySet()) {
					final RpcService annotation = service.getValue().getClass().getAnnotation(RpcService.class);
					final String interfaceName = annotation.value().getName();
					Object serviceBean = service.getValue();
					
					if (handlerMap.get(interfaceName) == null) {
						List<Object> list = new ArrayList<Object>();
						list.add(serviceBean);
						handlerMap.put(interfaceName, list);
					} else {
						List<Object> list = handlerMap.get(interfaceName);
						list.add(serviceBean);
						handlerMap.put(interfaceName, list);
					}
					
					// 创建服务
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								buildService(handlerMap, interfaceName, annotation.version(), annotation.port(), annotation.weight());
							} catch (Exception e) {
								e.printStackTrace();
							};
						}
					}).start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void buildService(final Map<String, List<Object>> handlerMap, String serviceInterface, String serviceVersion, int port, int serviceWeight) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
			.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline()
					.addLast(new RpcDecoder(RpcRequest.class))
					.addLast(new RpcEncoder(RpcResponse.class))
					.addLast(new RpcHandler(handlerMap));
				}
			})
			.option(ChannelOption.SO_BACKLOG, 128)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			String host = LocalNetWorkIpResolve.getServerIp();
			ChannelFuture future = bootstrap.bind(host, port).sync();
			LOGGER.debug(">>>server started on port {}", port);
			
			// 服务注册
			if (serviceRegistry != null) {
				serviceRegistry.register(serviceInterface, serviceVersion, host + ":" + port + ":" + serviceWeight);
			}
			
			future.channel().closeFuture().sync();
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
    }

}

