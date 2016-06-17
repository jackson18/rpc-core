package com.qijiabin.core.registry;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.FactoryBean;

/**
 * ========================================================
 * 日 期：2016年6月16日 下午4:49:49
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：zk工厂类，用于创建zk客户端连接
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class ZookeeperFactory implements FactoryBean<CuratorFramework> {
	
	// zk地址集合
	private String zkHosts;
	// session超时
	private int sessionTimeout = 30000;
	// 连接超时
	private int connectionTimeout = 30000;
	// 共享一个zk链接
	private boolean singleton = true;
	// 全局path前缀,常用来区分不同的应用
	private String namespace;
	// zk客户端连接
	private CuratorFramework zkClient;

	
	@Override
	public CuratorFramework getObject() throws Exception {
		if (singleton) {
			if (zkClient == null) {
				zkClient = create(zkHosts, sessionTimeout, connectionTimeout, namespace);
				zkClient.start();
			}
			return zkClient;
		}
		return create(zkHosts, sessionTimeout, connectionTimeout, namespace);
	}

	@Override
	public Class<?> getObjectType() {
		return CuratorFramework.class;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}
	
	public void close() {
		if (zkClient != null) {
			zkClient.close();
		}
	}
	
	/**
	 * 创建zk连接
	 * @param connectString
	 * @param sessionTimeout
	 * @param connectionTimeout
	 * @param namespace
	 * @return
	 */
	public static CuratorFramework create(String connectString, int sessionTimeout, int connectionTimeout, String namespace) {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		return builder
				.connectString(connectString)
				.sessionTimeoutMs(sessionTimeout)
				.connectionTimeoutMs(30000)
				.canBeReadOnly(true)
				.namespace(namespace)
				.retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
				.defaultData(null)
				.build();
	}

	public String getZkHosts() {
		return zkHosts;
	}

	public void setZkHosts(String zkHosts) {
		this.zkHosts = zkHosts;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public CuratorFramework getZkClient() {
		return zkClient;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}
	
}
