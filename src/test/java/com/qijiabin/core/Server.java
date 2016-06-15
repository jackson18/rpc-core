package com.qijiabin.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:36:57
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class Server {

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
