package com.qijiabin.core.serialization;

/**
 * ========================================================
 * 日 期：2016年6月23日 下午5:18:18
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public interface SerializeUtil {

	/**
     * 序列化（对象 -> 字节数组）
     */
    public <T> byte[] serialize(T obj);
    
    /**
     * 反序列化（字节数组 -> 对象）
     */
    public <T> T deserialize(byte[] data, Class<T> cls);
	
}
