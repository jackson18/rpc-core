package com.qijiabin.core.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ========================================================
 * 日 期：2016年6月16日 上午10:34:03
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class LocalNetWorkIpResolve {
	
	private static final Logger log = LoggerFactory.getLogger(LocalNetWorkIpResolve.class);
	
	// ip缓存
	private  static String serverIp;
	
	/**
	 * 获取本地机器ip
	 * @return
	 */
	public static String getServerIp() {
		if (StringUtils.isNotEmpty(serverIp)) {
			return serverIp;
		}
		// 一个主机有多个网络接口
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = netInterfaces.nextElement();
				// 每个网络接口,都会有多个"网络地址",比如一定会有lookback地址,会有siteLocal地址等.以及IPV4或者IPV6 .
				Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					InetAddress address = addresses.nextElement();
					if (address instanceof Inet6Address) {
						continue;
					}
					if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
						serverIp = address.getHostAddress();
						log.info("resolve server ip : {}", serverIp);
						break;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return serverIp;
	}
	
	/**
	 * 重置本地机器ip
	 */
	public static void reset() {
		serverIp = null;
	}

}
