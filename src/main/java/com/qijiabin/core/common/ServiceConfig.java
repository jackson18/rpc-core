package com.qijiabin.core.common;

/**
 * ========================================================
 * 日 期：2016年6月20日 上午11:31:27
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class ServiceConfig {
	
	private String version;
	private int port;
	private int weight;

	public ServiceConfig(String version, int port, int weight) {
		super();
		this.version = version;
		this.port = port;
		this.weight = weight;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}