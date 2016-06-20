package com.qijiabin.core.registry;

import java.io.UnsupportedEncodingException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:31:08
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：服务注册
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    //zk客户端
  	private CuratorFramework zkClient;

    /**
     * 服务注册
     * @param serviceMap
     * @param version
     * @param hostName
     */
    public void register(String serviceInterface, String version, String hostName) {
    	String node = "/" + serviceInterface + "/" + version + "/" + hostName;
    	LOGGER.info("service register address is : {}", node);
    	register(node);
    }

    /**
     * 服务注册
     * @param data
     */
    public void register(String data) {
        if(zkClient.getState() == CuratorFrameworkState.LATENT){
			zkClient.start();
		}
		//临时节点
		try {
			zkClient.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(data);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(">>>register service address to zookeeper exception:{}",e);
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.error(">>>register service address to zookeeper exception:{}",e);
			e.printStackTrace();
		}
    }

	public CuratorFramework getZkClient() {
		return zkClient;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}
    
}

