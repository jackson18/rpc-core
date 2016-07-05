package com.qijiabin.core.common;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ========================================================
 * 日 期：2016年7月4日 下午3:14:58
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class Refer {

	private InetSocketAddress address;
	private AtomicLong activeCount;
	
	public Refer() {
	}
	
	public Refer(InetSocketAddress address) {
		super();
		this.address = address;
	}
	
	public Refer(InetSocketAddress address, AtomicLong activeCount) {
		super();
		this.address = address;
		this.activeCount = activeCount;
	}
	
	public void increse() {
		activeCount.incrementAndGet();
	}

	public InetSocketAddress getAddress() {
		return address;
	}
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	}
	public AtomicLong getActiveCount() {
		return activeCount;
	}
	public void setActiveCount(AtomicLong activeCount) {
		this.activeCount = activeCount;
	}
	
}
