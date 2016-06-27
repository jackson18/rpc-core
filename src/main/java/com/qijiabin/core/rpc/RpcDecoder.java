package com.qijiabin.core.rpc;

import java.util.List;

import com.qijiabin.core.common.CommonUtil;
import com.qijiabin.core.serialization.SerializeUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * ========================================================
 * 日 期：2016年6月15日 上午10:32:59
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：RPC 解码器
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;
    private String serialize;

    public RpcDecoder(Class<?> genericClass, String serialize) {
        this.genericClass = genericClass;
        this.serialize = serialize;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        SerializeUtil serializeUtil = CommonUtil.getSerializeUtil(serialize);
        Object obj = serializeUtil.deserialize(data, genericClass);
        out.add(obj);
    }
}
