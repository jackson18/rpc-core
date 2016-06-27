package com.qijiabin.core.rpc;

import com.qijiabin.core.common.CommonUtil;
import com.qijiabin.core.serialization.SerializeUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:33:40
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 编码器
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
@SuppressWarnings("rawtypes")
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;
    private String serialize;

    public RpcEncoder(Class<?> genericClass, String serialize) {
        this.genericClass = genericClass;
        this.serialize = serialize;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
        	SerializeUtil serializeUtil = CommonUtil.getSerializeUtil(serialize);
            byte[] data = serializeUtil.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}

