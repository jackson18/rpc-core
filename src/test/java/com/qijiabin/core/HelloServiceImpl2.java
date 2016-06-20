package com.qijiabin.core;

import com.qijiabin.core.annotation.RpcService;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:36:41
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@RpcService(value = HelloService.class, version = "0.0.1", port = 8001, weight = 1)
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello222! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello222! " + person.getFirstName() + " " + person.getLastName();
    }
}
