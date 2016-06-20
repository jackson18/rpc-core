package com.qijiabin.core.registry;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

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
  	
  	
    public ServiceRegistry(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

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
    
    /**
     * 取消服务注册
     * @param name
     * @param version
     * @param address
     */
	public void unregister(String name, String version, String address) {
		String servicePath = "/"+name + "/" + version +"/"+ address;
		try {
			if(zkClient.getState() == CuratorFrameworkState.LATENT) {
				zkClient.start();
			}
			if(zkClient.blockUntilConnected(3000, TimeUnit.MILLISECONDS)) {
				zkClient.delete().forPath(servicePath);
				LOGGER.debug(">>>delete path [{}] successful.", servicePath);
			}
		} catch (InterruptedException e) {
			LOGGER.error(">>>CuratorFramework Client interrupted. Exception message [{}].", e.getMessage());
		} catch (Exception e) {
			LOGGER.error(">>>delete path[{}] failed. Message:{}.", servicePath, e.getMessage());
		}
	}
	
	/**
	 * 关闭服务链接
	 */
	public void close(){
		zkClient.close();
	}

}

