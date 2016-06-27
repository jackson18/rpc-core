package com.qijiabin.core.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import com.qijiabin.core.common.RpcRequest;
import com.qijiabin.core.common.RpcResponse;
import com.qijiabin.core.proxy.ProxyFactory;
import com.qijiabin.core.proxy.support.JdkProxyFactory;
import com.qijiabin.core.registry.ServiceDiscovery;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:35:25
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 代理（用于创建 RPC 服务代理）
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class RpcClientProxyFactory implements InitializingBean, FactoryBean<Object> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientProxyFactory.class);

    private ServiceDiscovery serviceDiscovery;
    private String serialize;
    private Object proxyClient;
    private static final ProxyFactory PROXY_FACTORY = new JdkProxyFactory();

    
    public RpcClientProxyFactory(ServiceDiscovery serviceDiscovery, String serialize) {
        this.serviceDiscovery = serviceDiscovery;
        this.serialize = serialize;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		proxyClient = PROXY_FACTORY.getProxy(Class.forName(serviceDiscovery.getService()), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);

                
                if (serviceDiscovery == null) {
                	LOGGER.info(">>>serviceDiscovery is null!");
                	return null;
                } 
                
                InetSocketAddress address = serviceDiscovery.selector();
                
                if (address == null) {
                	LOGGER.info(">>>address is null!");
                	return null;
                }
                RpcClientHandler client = new RpcClientHandler(address.getHostName(), address.getPort(), serialize);
                RpcResponse response = client.send(request);
                
                if (response.isError()) {
                	throw response.getError();
                } else {
                	return response.getResult();
                }
            }
        });
	}

	@Override
	public Object getObject() throws Exception {
		return proxyClient;
	}

	@Override
	public Class<?> getObjectType() {
		try {
			return Class.forName(serviceDiscovery.getService());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
	
}

