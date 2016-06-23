package com.qijiabin.core.common;

import org.apache.commons.lang3.StringUtils;

import com.qijiabin.core.serialization.HessianUtil;
import com.qijiabin.core.serialization.ProtostuffUtil;
import com.qijiabin.core.serialization.SerializeUtil;

/**
 * ========================================================
 * 日 期：2016年6月23日 下午5:22:17
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class CommonUtil {

	/**
	 * 获取序列化工具
	 * @return
	 */
	public static SerializeUtil getSerializeUtil(String serialize) {
		if (StringUtils.isEmpty(serialize)) {
			return null;
		}
		if ("hessian".equalsIgnoreCase(serialize)) {
			return new HessianUtil();
		} else if ("protostuff".equalsIgnoreCase(serialize)) {
			return new ProtostuffUtil();
		}
		return null;
	}
	
}
