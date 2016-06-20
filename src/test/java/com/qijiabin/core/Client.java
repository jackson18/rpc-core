package com.qijiabin.core;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RpcProxy rpcProxy;
    
    @BeforeClass
	public static void before() {
    	try {
    		Thread.sleep(5000);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
	}

    @Test
    public void helloTest1() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello("World");
        Assert.assertEquals("Hello! World", result);
    }

    @Test
    public void helloTest2() {
        HelloService helloService = rpcProxy.create(HelloService.class);
        String result = helloService.hello(new Person("Yong", "Huang"));
        Assert.assertEquals("Hello! Yong Huang", result);
    }
}
