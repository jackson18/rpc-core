package com.qijiabin.core.proxy;

import java.lang.reflect.InvocationHandler;

/**
 * ========================================================
 * 日 期：2016年6月27日 下午2:21:43
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public interface ProxyFactory {

	<T> T getProxy(Class<T> clz, InvocationHandler invocationHandler);
	
}

