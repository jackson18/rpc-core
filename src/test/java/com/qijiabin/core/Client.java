package com.qijiabin.core;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.qijiabin.core.client.RpcProxy;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-client.xml")
public class Client {

    @Resource(name="rpcProxy")
    private RpcProxy rpcProxy;
    
    @Resource(name="rpcProxy2")
    private RpcProxy rpcProxy2;
    
    @Test
    public void helloTest1() {
    	System.out.println();
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        System.out.println(result);
    }

    @Test
    public void helloTest2() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("Yong", "Huang"));
        System.out.println(result);
    }
    
    @Test
    public void sayTest() {
    	SayService sayService = rpcProxy2.create(SayService.class);
    	String result = sayService.say("tom");
    	System.out.println(result);
    }
}
