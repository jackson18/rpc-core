package com.qijiabin.core;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server {

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring-server.xml");
    }
}
