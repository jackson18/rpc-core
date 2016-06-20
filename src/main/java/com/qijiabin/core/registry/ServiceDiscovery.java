package com.qijiabin.core.registry;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:34:04
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：服务发现
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class ServiceDiscovery implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);
    
    //zk客户端
  	private CuratorFramework zkClient;
  	// 监控ZNode的子节点
 	private PathChildrenCache cachedPath;
 	// 注册服务
 	private String service;
  	// 服务版本号
 	private String version;
 	// 默认权重
 	private static final Integer DEFAULT_WEIGHT = 1;
 	// 加锁对象
 	private Object lock = new Object();
 	// 用来保存当前provider所接触过的地址记录,当zookeeper集群故障时,可以使用trace中地址,作为"备份"
 	private Set<String> trace = new HashSet<String>();
 	// IP套接字地址（IP地址+端口号）容器
 	private final List<InetSocketAddress> container = new ArrayList<InetSocketAddress>();
 	// IP套接字地址（IP地址+端口号）队列
 	private Queue<InetSocketAddress> inner = new LinkedList<InetSocketAddress>();


  	@Override
	public void afterPropertiesSet() throws Exception {
		// 如果zk尚未启动,则启动
		if (zkClient.getState() == CuratorFrameworkState.LATENT) {
			zkClient.start();
		}
		buildPathChildrenCache(zkClient, getServicePath(), true);
		cachedPath.start(StartMode.POST_INITIALIZED_EVENT);
	}
  	
  	/**
	 * 获取服务地址
	 * @return
	 */
	private String getServicePath(){
		return "/" + service + "/" + version;
	}

  	/**
	 * 构建cachedPath
	 * @param client
	 * @param path
	 * @param cacheData
	 * @throws Exception
	 */
	private void buildPathChildrenCache(final CuratorFramework client, String path, Boolean cacheData) throws Exception {
		cachedPath = new PathChildrenCache(client, path, cacheData);
		cachedPath.getListenable().addListener(new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				PathChildrenCacheEvent.Type eventType = event.getType();
				switch (eventType) {
				case CONNECTION_RECONNECTED:
					LOGGER.info(">>>Connection is reconection.");
					break;
				case CONNECTION_SUSPENDED:
					LOGGER.info(">>>Connection is suspended.");
					break;
				case CONNECTION_LOST:
					LOGGER.warn(">>>Connection error,waiting...");
					return;
				default:
				}
				// 任何节点的时机数据变动,都会rebuild,此处为一个"简单的"做法.
				cachedPath.rebuild();
				rebuild();
			}

			protected void rebuild() throws Exception {
				List<ChildData> children = cachedPath.getCurrentData();
				if (children == null || children.isEmpty()) {
					// 有可能所有的thrift server都与zookeeper断开了链接
					// 但是,有可能,thrift client与thrift server之间的网络是良好的
					// 因此此处是否需要清空container,是需要多方面考虑的.
					container.clear();
					LOGGER.error(">>>thrift server-cluster error....");
					return;
				}
				List<InetSocketAddress> current = new ArrayList<InetSocketAddress>();
				String path = null;
				for (ChildData data : children) {
					path = data.getPath();
					LOGGER.debug(">>>get path:{}", path);
					path = path.substring(getServicePath().length()+1);
					LOGGER.debug(">>>get serviceAddress:{}", path);
					String address = new String(path.getBytes(), "utf-8");
					current.addAll(transfer(address));
					trace.add(address);
				}
				Collections.shuffle(current);
				synchronized (lock) {
					container.clear();
					container.addAll(current);
					inner.clear();
					inner.addAll(current);
				}
			}
		});
	}
	
	/**
	 * 根据权重将address添加到集合
	 * @param address
	 * @return
	 */
	private List<InetSocketAddress> transfer(String address) {
		String[] hostname = address.split(":");
		Integer weight = DEFAULT_WEIGHT;
		if (hostname.length == 3) {
			weight = Integer.valueOf(hostname[2]);
		}
		String ip = hostname[0];
		Integer port = Integer.valueOf(hostname[1]);
		List<InetSocketAddress> result = new ArrayList<InetSocketAddress>();
		// 根据优先级，将ip：port添加多次到地址集中，然后随机取地址实现负载
		for (int i = 0; i < weight; i++) {
			result.add(new InetSocketAddress(ip, port));
		}
		return result;
	}
	
	/**
	 * 选取一个合适的address,可以随机获取等,内部可以使用合适的算法.
	 */
	public synchronized InetSocketAddress selector() {
		if (inner.isEmpty()) {
			if (!container.isEmpty()) {
				Collections.shuffle(container);
				inner.addAll(container);
			} else if (!trace.isEmpty()) {
				synchronized (lock) {
					for (String hostname : trace) {
						container.addAll(transfer(hostname));
					}
					Collections.shuffle(container);
					inner.addAll(container);
				}
			}
		}
		return inner.poll();
	}


	public CuratorFramework getZkClient() {
		return zkClient;
	}

	public void setZkClient(CuratorFramework zkClient) {
		this.zkClient = zkClient;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}

