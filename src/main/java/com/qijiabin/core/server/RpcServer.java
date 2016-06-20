package com.qijiabin.core.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.qijiabin.core.annotation.RpcService;
import com.qijiabin.core.common.RpcRequest;
import com.qijiabin.core.common.RpcResponse;
import com.qijiabin.core.common.ServiceConfig;
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
public class RpcServer implements ApplicationContextAware, InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private ServiceRegistry serviceRegistry;
    private Map<String, Object> handlerMap = new HashMap<String, Object>();
    private Map<String, ServiceConfig> configMap = new HashMap<String, ServiceConfig>();
    
    
    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
        	for (Map.Entry<String, Object> service : serviceBeanMap.entrySet()) {
        		RpcService annotation = service.getValue().getClass().getAnnotation(RpcService.class);
        		String interfaceName = annotation.value().getName();
        		ServiceConfig serviceConfig = new ServiceConfig(annotation.version(), annotation.port(), annotation.weight());
        		configMap.put(interfaceName, serviceConfig);
                handlerMap.put(interfaceName, service.getValue());
        	}
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    	for (Map.Entry<String, Object> service : handlerMap.entrySet()) {
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
    			ServiceConfig serviceConfig = configMap.get(service.getKey());
    			if (serviceConfig == null) {
    				continue;
    			}
    			String serviceVersion = serviceConfig.getVersion();
    			int port = serviceConfig.getPort();
    			int serviceWeight = serviceConfig.getWeight();
    			
    			ChannelFuture future = bootstrap.bind(host, port).sync();
    			LOGGER.debug(">>>server started on port {}", port);
    			
    			// 服务注册
    			if (serviceRegistry != null) {
    				serviceRegistry.register(handlerMap, serviceVersion, host + ":" + port + ":" + serviceWeight);
    			}
    			
    			future.channel().closeFuture().sync();
    		} finally {
    			workerGroup.shutdownGracefully();
    			bossGroup.shutdownGracefully();
    		}
    	}
    }
    
}
