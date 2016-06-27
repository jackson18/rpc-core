package com.qijiabin.core.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * ========================================================
 * 日 期：2016年6月23日 下午4:55:25
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：序列化工具类（基于 hessian2 实现）
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class HessianUtil implements SerializeUtil {
	
	/**
     * 序列化（对象 -> 字节数组）
     */
	public <T> byte[] serialize(T obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    Hessian2Output ho = new Hessian2Output(os);  
	    byte[] data = null;
	    try {
	    	ho.startMessage();
			ho.writeObject(obj);
			ho.completeMessage();
		    ho.close();
		    data =  os.toByteArray();  
		    os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	    return data;
	}

	/**
	 * 反序列化（字节数组 -> 对象）
	 */
	@SuppressWarnings("unchecked")
	public <T> T deserialize(byte[] data, Class<T> cls) {
		if (data == null) {
			throw new NullPointerException();
		}
		T t = null;
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			Hessian2Input hi = new Hessian2Input(is);
			hi.startMessage();
			t = (T) hi.readObject(cls);
			hi.completeMessage();
		    hi.close();
		    is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return t;
	}

}
