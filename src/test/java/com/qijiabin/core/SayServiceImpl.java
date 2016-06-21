package com.qijiabin.core;

/**
 * ========================================================
 * 日 期：2016年6月21日 下午12:10:26
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class SayServiceImpl implements SayService {

	@Override
	public String say(String name) {
		return "say:" + name;
	}

}
