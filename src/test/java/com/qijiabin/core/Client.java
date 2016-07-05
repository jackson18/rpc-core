package com.qijiabin.core;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:37:03
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@SuppressWarnings("resource")
public class Client {
    
	@Test
    public void simpleTest() {
    	ApplicationContext context = new ClassPathXmlApplicationContext("spring-client.xml");
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	for (int i = 0; i < 10; i++) {
    		HelloService helloService = (HelloService) context.getBean("helloService");
    		String result = helloService.hello("World");
    		System.out.println(result);
    		
    		SayService sayService = (SayService) context.getBean("sayService");
    		String result2 = sayService.say("tom");
    		System.out.println(result2);
    	}
    }

}

