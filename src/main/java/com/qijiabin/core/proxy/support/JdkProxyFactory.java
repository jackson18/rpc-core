package com.qijiabin.core.proxy.support;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import com.qijiabin.core.proxy.ProxyFactory;

/**
 * ========================================================
 * 日 期：2016年6月27日 下午3:57:23
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class JdkProxyFactory implements ProxyFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProxy(Class<T> clz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {clz}, invocationHandler);
    }

}

