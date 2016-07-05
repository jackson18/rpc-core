package com.qijiabin.core.cluster;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.qijiabin.core.common.Refer;

/**
 * ========================================================
 * 日 期：2016年7月4日 下午4:55:55
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class LoadBalance {
	
	private static AtomicInteger idx = new AtomicInteger(0);
	
	public static Refer getRefer(List<Refer> refers, String loadbalance) {
		switch (loadbalance) {
			case "random":
				return getRandom(refers);
			case "roundrobin":
				return getRoundRobin(refers);
			case "activeWeight":
				return getActiveWeight(refers);
			default:
				return getRandom(refers);
		}
	}

	/**
	 * 随机
	 * @param refers
	 * @return
	 */
	public static Refer getRandom(List<Refer> refers) {
		int idx = (int) (Math.random() * refers.size());
        Refer refer = refers.get(idx % refers.size());
        refer.increse();
        return refer;
	}
	
	/**
	 * 轮循
	 * @param refers
	 * @return
	 */
	public static Refer getRoundRobin(List<Refer> refers) {
		int index = idx.incrementAndGet();
        Refer refer = refers.get(index % refers.size());
        refer.increse();
		return refer;
	}
	
	/**
	 * 低并发度优先
	 * @param refers
	 * @return
	 */
	public static Refer getActiveWeight(List<Refer> refers) {
		Collections.sort(refers, new LowActivePriorityComparator());
		Refer refer =  refers.get(0);
		refer.increse();
		return refer;
	}
	
	static class LowActivePriorityComparator implements Comparator<Refer> {

		@Override
		public int compare(Refer r1, Refer r2) {
			return (int)(r1.getActiveCount().get() - r2.getActiveCount().get());
		}
		
	}
	
}
